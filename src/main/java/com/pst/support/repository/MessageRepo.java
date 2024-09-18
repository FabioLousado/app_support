package com.pst.support.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pst.support.model.Message;

@Repository
public interface MessageRepo extends JpaRepository<Message, Long> {
	
	List<Message> findByTicketId(Long id);

}
