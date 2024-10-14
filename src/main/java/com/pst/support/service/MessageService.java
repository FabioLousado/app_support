package com.pst.support.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.pst.support.exception.NoMessageForTicketException;
import com.pst.support.model.Message;
import com.pst.support.repository.MessageRepo;

@Service
public class MessageService {
	
	@Autowired
	private TicketService ticketService;
	
	@Autowired
	private MessageRepo messageRepo;
	
	@Autowired
	private EtatService etatService;
	
	@Autowired
	private FileService fileService;
	
	public List<Message> getMessages() {	
		return messageRepo.findAll();
	}
	
	public Optional<Message> getMessageById(Long id) {
		return messageRepo.findById(id);
	}
	
	public Message saveMessage(Message message) {
		return messageRepo.save(message);
	}
	
	public List<Message> saveMessages(List<Message> message) {
		return messageRepo.saveAll(message);
	}
	
	public List<Message> getMessagesByTicketId(Long ticketId) {
		return messageRepo.findByTicketId(ticketId);
	}
	
	public ResponseEntity<List<Message>> getMessagesByIdTicket(Long id){
		var messages = messageRepo.findByTicketId(id).stream()
	            .sorted(Comparator.comparing(Message::getDate))
	            .collect(Collectors.toList());
		
		return ResponseEntity.ok(messages);
	}
	
	private void updateTicketStatusIfFirstSupportResponse(Long ticketId, String envoyePar) {
	    if (envoyePar.equalsIgnoreCase("support")) {
	        var ticket = ticketService.getTicket(ticketId);
	        if (ticket != null && !ticket.getEtat().getId().equals((long) 2)) {
	            var etat = etatService.getEtat((long) 2);
	            if (etat != null) {
	                ticket.setEtat(etat);
	                ticketService.saveTicket(ticket);
	            }
	        }
	    }
	}
	
	private void addMessagesFiles(String content, 
			String envoyePar, Long ticketId, List<MultipartFile> files, List<Message> listMessage) {
		if(files != null) {
			files.forEach(file -> {
				try {
					var m = new Message();
					var path = fileService.addFile(file);
					m.setEnvoyePar(envoyePar);
					m.setTicketId(ticketId);
					m.setFile(true);
					m.setContent(path);
					listMessage.add(m);
				} catch (IOException e) {
				}
		});
		}
	}
	
	private void addMessageTicket(String content, 
			String envoyePar, Long ticketId, List<Message> listMessage) {
		if(content != null) {
			var m = new Message();
			m.setEnvoyePar(envoyePar);
			m.setTicketId(ticketId);
			m.setContent(content);
			listMessage.add(m);
		}
	}
	
	public ResponseEntity<List<Message>> addMessage(String content, 
			String envoyePar, Long ticketId, List<MultipartFile> files) throws URISyntaxException {

		
		List<Message> listMessage = new ArrayList<>();
		
		updateTicketStatusIfFirstSupportResponse(ticketId, envoyePar);
		
		addMessageTicket(content, envoyePar, ticketId, listMessage);
		addMessagesFiles(content, envoyePar, ticketId, files, listMessage);
		
		saveMessages(listMessage);

		return ResponseEntity.ok(listMessage);

	}
	
	public ResponseEntity<Message> getMessageByIdWTF(Long id) {
		try {

			var m = getMessageById(id)
					.orElseThrow(() -> new NoMessageForTicketException("Ce message n'est répertorié sur aucun ticket"));

			if (m.isFile()) {
	            // Construct the file download URL
	            String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
	                    .path("/message/file/")
	                    .path(id.toString())
	                    .toUriString();

	            // Return the Message object with a custom header for file download
	            return ResponseEntity.ok()
	                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + m.getContent() + "\"")
	                    .header("File-Download-URL", downloadUrl)
	                    .body(m);
	        }
			
			return ResponseEntity.ok(m);
		} catch (NoMessageForTicketException e) {
			return ResponseEntity.notFound().build();
		}
	}
	
	public ResponseEntity<byte[]> downloadFile(Long id) {
		try {
			
			var m = getMessageById(id)
					.orElseThrow(() -> new NoMessageForTicketException("Ce message n'est répertorié sur aucun ticket"));

			if (m.isFile()) {
				// Load file as Resource
				Resource resource = fileService.loadFileAsResource(m.getContent());

				// Set the content-disposition header so the file is downloaded as an attachment
				String contentDisposition = "attachment; filename=\"" + resource.getFilename() + "\"";
		
				var mediaType = fileService.getMediaTypeFromResource(resource);
				
				return ResponseEntity.ok()
						.contentLength(resource.getContentAsByteArray().length)
						.contentType(mediaType)
						.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
						.body(resource.getContentAsByteArray());
			}
			else {
				throw new IOException();
			}

			
		} catch (IOException ex) {
			return ResponseEntity.notFound().build();
		} catch (NoMessageForTicketException e) {
			return ResponseEntity.notFound().build();
		}
	}
	
	public ResponseEntity<byte[]> viewFile(Long id) {
	    try {
	        var m = getMessageById(id)
	                .orElseThrow(() -> new NoMessageForTicketException("Ce message n'est répertorié sur aucun ticket"));

	        if (m.isFile()) {
	            Resource resource = fileService.loadFileAsResource(m.getContent());

	            // En-tête pour afficher le PDF dans le navigateur
	            String contentDisposition = "inline; filename=\"" + resource.getFilename() + "\"";
	            var mediaType = fileService.getMediaTypeFromResource(resource);

	            return ResponseEntity.ok()
	                    .contentLength(resource.contentLength())
	                    .contentType(mediaType)
	                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
	                    .body(resource.getInputStream().readAllBytes());
	        } else {
	            throw new IOException();
	        }
	    } catch (IOException ex) {
	        return ResponseEntity.notFound().build();
	    } catch (NoMessageForTicketException e) {
	        return ResponseEntity.notFound().build();
	    }
	}
}
