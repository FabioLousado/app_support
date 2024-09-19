package com.pst.support.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.pst.support.exception.NoMessageForTicketException;
import com.pst.support.model.Message;
import com.pst.support.repository.MessageRepo;
import com.pst.support.service.FileService;

@RestController
@RequestMapping("/message")
public class MessageController {

	@Autowired
	private MessageRepo messageRepo;

	@Autowired
	private FileService fileService;

	@GetMapping
	public List<Message> getTickets() {
		return messageRepo.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Message> getTicketById(@PathVariable Long id) {
		try {

			var m = messageRepo.findById(id)
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

	@GetMapping("/file/{id}")
	public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
		try {
			
			var m = messageRepo.findById(id)
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

	@PostMapping
	public ResponseEntity<String> addTicket(@RequestParam(value = "content", required = false) String content,
			@RequestParam(value = "envoyePar") String envoyePar, @RequestParam(value = "ticketId") Long ticketId,
			@RequestParam(value = "file", required = false) MultipartFile file) throws URISyntaxException {

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

		m.setEnvoyePar(envoyePar);
		m.setTicketId(ticketId);

		messageRepo.save(m);

		return ResponseEntity.created(new URI("/api/support/message/" + m.getId())).build();

	}
}
