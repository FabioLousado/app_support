package com.pst.support.controller;

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
import com.pst.support.model.Ticket;
import com.pst.support.service.TicketService;

@RestController
@RequestMapping("/ticket")
public class TicketController {

	@Autowired
	private TicketService ticketService;

	@GetMapping("/list/{role}/{mail}")
	public List<Ticket> getTickets(@PathVariable String role, @PathVariable String mail) {
		System.out.println(role);
		return ticketService.getTickets(role, mail);
	}
	
	@GetMapping("/{id}/{etatId}")
	public ResponseEntity<Ticket> setTicketEtat(@PathVariable Long id, @PathVariable Long etatId) {
	    return ticketService.updateTicketState(id, etatId);
	}
	
	@PostMapping
	public ResponseEntity<String> addTicket(@RequestBody Map<String, String> body){
		var title = body.get("title");
		var creerPar = body.get("creerPar");
		
		return ticketService.addTicket(title, creerPar);
			

	}
}
