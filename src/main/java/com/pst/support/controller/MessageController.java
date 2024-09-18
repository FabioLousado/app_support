package com.pst.support.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pst.support.FileService;
import com.pst.support.exception.NoMessageForTicketException;
import com.pst.support.model.Message;
import com.pst.support.repository.MessageRepo;

@RestController
@RequestMapping("/message")
public class MessageController {

	@Autowired
	private MessageRepo messageRepo;

	@GetMapping
	public List<Message> getTickets() {
		return messageRepo.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Message> getTicketById(@PathVariable Long id){
		try {
			return ResponseEntity.ok(messageRepo.findById(id).orElseThrow(() -> new NoMessageForTicketException("Aucun message n'est répertorié sur ce ticket")));
		}catch(NoMessageForTicketException e) {
			return ResponseEntity.notFound().build();
		}
	}
	
	@PostMapping
	public ResponseEntity<String> addTicket(
			@RequestParam(value = "content", required = false) String content,
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestBody Map<String, String> body) throws URISyntaxException{
		
		var envoyePar = body.get("envoyePar");	
		var ticketId = Long.valueOf(body.get("ticketId"));
		
		var m = new Message();
		
		m.setContentOrFile(content, file);
		
		if(m.isFile()) {
			try {
				var path = FileService.addFile(file);
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
