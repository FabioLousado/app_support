package com.pst.support.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.pst.support.exception.ArgumentInvalidException;
import com.pst.support.model.Ticket;
import com.pst.support.repository.TicketRepo;

@Service
public class TicketService {
	
	@Autowired
	private TicketRepo ticketRepo;
	
	@Autowired
	private EtatService etatService;
	
	public List<Ticket> getTickets(String role, String mail) {
	    LocalDateTime nowMinusTwoDays = LocalDateTime.now().minus(2, ChronoUnit.DAYS);
	    var tickets = ticketRepo.findAll();
	    if(role == "support") {
	    	tickets = tickets.stream()
	    			.filter(ticket -> ticket.getCreerPar().equals("support"))
	    			.toList();
	    }
	    return tickets.stream()
	        .filter(ticket -> ticket.getDateCloture() == null ||
	                         ticket.getDateCloture().toLocalDateTime().isAfter(nowMinusTwoDays))
	        .toList();
	}
	
	public Ticket getTicket(Long id) {
		return ticketRepo.findById(id).orElse(null);
	}
	
	public Ticket saveTicket(Ticket ticket) {
		return ticketRepo.save(ticket);
	}
	
	public ResponseEntity<String> addTicket(String title, String creerPar) {
		try {
			var t = new Ticket();
			t.setTitle(title);
			t.setCreerPar(creerPar);
			t.setEtat(etatService.getEtat((long) 1));
			saveTicket(t);
			return ResponseEntity.created(new URI("/ticket/" + t.getId())).build();
		}catch(ArgumentInvalidException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		} catch (URISyntaxException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	public ResponseEntity<Ticket> updateTicketState(Long ticketId, Long etatId) {
	    var ticket = getTicket(ticketId);
	    
	    var etat = etatService.getEtat(etatId);
	   
	    ticket.setEtat(etat);
	    if(etatId == 3 || etatId == 4) {
	    	ticket.setDateCloture(new Timestamp(new Date().getTime()));
	    }
	    
	    saveTicket(ticket);

	    return ResponseEntity.ok(ticket);
	}
}
