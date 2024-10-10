package com.pst.support.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pst.support.model.Etat;
import com.pst.support.repository.EtatRepo;

@Service
public class EtatService {
	
	@Autowired
	private EtatRepo etatRepo;
	
	public Etat getEtat(Long id) {
		return etatRepo.findById(id).orElse(null);
	}
	
	public Etat saveEtat(Etat etat) {
		return etatRepo.save(etat);
	}
}
