package com.pst.support.tools;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.pst.support.model.Etat;
import com.pst.support.service.EtatService;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInit implements CommandLineRunner {

    private final EtatService etatService; // Assurez-vous que ce service est correctement injecté

    public DataInit(EtatService etatService) {
        this.etatService = etatService;
    }

    @Override
    public void run(String... args) throws Exception {
        initializeEtats();
    }

    private void initializeEtats() {
        List<Etat> etats = Arrays.asList(
            new Etat(1L, "Ouvert"), // Exemple d'état
            new Etat(2L, "En cours"),
            new Etat(3L, "Résolu"),
            new Etat(4L, "Rejeté")
        );

        for (Etat etat : etats) {
            if (etatService.getEtat(etat.getId()) == null) { // Vérifier si l'état existe déjà
                etatService.saveEtat(etat); // Enregistrer l'état
            }
        }
    }
}