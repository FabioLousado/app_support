package com.pst.support.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

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
	
	public List<Message> getMessagesByTicketId(Long ticketId) {
		return messageRepo.findByTicketId(ticketId);
	}
	
	public ResponseEntity<List<Message>> getMessagesByIdTicket(Long id){
		var messages = messageRepo.findByTicketId(id);
		
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
	
	public ResponseEntity<String> addMessage(String content, 
			String envoyePar, Long ticketId, MultipartFile file) throws URISyntaxException {

		var m = new Message();
		m.setContentOrFile(content, file);

		if (m.isFile()) {
			try {
				var path = fileService.addFile(file);
				m.setContent(path);
			} catch (IOException e) {
				return ResponseEntity.badRequest().body("Le fichier n'a pu être enregistré.");
			}
		}
		
		updateTicketStatusIfFirstSupportResponse(ticketId, envoyePar);

		m.setEnvoyePar(envoyePar);
		m.setTicketId(ticketId);

		saveMessage(m);
		
		return ResponseEntity.created(new URI("/api/support/message/" + m.getId())).build();

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
