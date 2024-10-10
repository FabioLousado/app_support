package com.pst.support.controller;

import java.net.URISyntaxException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pst.support.model.Message;
import com.pst.support.service.MessageService;

@RestController
@RequestMapping("/message")
public class MessageController {

	@Autowired
	private MessageService messageService;

	@GetMapping
	public List<Message> getMessages() {
		return messageService.getMessages();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Message> getMessageById(@PathVariable Long id) {
		return messageService.getMessageByIdWTF(id);
	}
	
	@GetMapping("/ticket/{id}")
	public ResponseEntity<List<Message>> getMessagesByIdTicket(@PathVariable Long id){
		return messageService.getMessagesByIdTicket(id);
	}

	@GetMapping("/file/{id}")
	public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
		return messageService.downloadFile(id);
	}
	
	@GetMapping("/view/{id}")
	public ResponseEntity<byte[]> viewFile(@PathVariable Long id) {
	    return messageService.viewFile(id);
	}

	@PostMapping
	public ResponseEntity<String> addMessage(@RequestParam(value = "content", required = false) String content,
			@RequestParam(value = "envoyePar") String envoyePar, @RequestParam(value = "ticketId") Long ticketId,
			@RequestParam(value = "file", required = false) MultipartFile file) throws URISyntaxException {

		return messageService.addMessage(content, envoyePar, ticketId, file);

	}
}
