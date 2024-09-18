package com.pst.support.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pst.support.model.Ticket;

@Repository
public interface TicketRepo extends JpaRepository<Ticket, Long>{

}
