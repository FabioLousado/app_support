package com.pst.support.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.pst.support.exception.ArgumentInvalidException;
import com.pst.support.model.Message;
import com.pst.support.model.Ticket;
import com.pst.support.repository.MessageRepo;
import com.pst.support.repository.TicketRepo;

@RestController
@RequestMapping("/ticket")
public class TicketController {

	@Autowired
	private TicketRepo ticketRepo;
	
	@Autowired
	private MessageRepo messageRepo;

	@GetMapping
	public List<Ticket> getTickets() {
		return ticketRepo.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<List<Message>> getTicketById(@PathVariable Long id){
		var messages = messageRepo.findByTicketId(id);
		
		return ResponseEntity.ok(messages);
	}
	
	@PostMapping
	public ResponseEntity<String> addTicket(@RequestBody Map<String, String> body){
		var title = body.get("title");
		var creerPar = body.get("creerPar");
		
		try {
			var t = new Ticket();
			t.setTitle(title);
			t.setCreerPar(creerPar);
			ticketRepo.save(t);
			return ResponseEntity.created(new URI("/ticket/" + t.getId())).build();
		}catch(ArgumentInvalidException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		} catch (URISyntaxException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
			

	}
}
