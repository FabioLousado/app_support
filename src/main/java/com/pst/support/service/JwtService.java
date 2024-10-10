package com.pst.support.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    // Récupération de la clé secrète depuis les fichiers de config
    @Value("${jwt.secret}")
    private String secret; // Assure-toi que cela est configuré

    private static final String secret_key = "mysecretkey"; // Remplacez ceci par votre clé secrète si nécessaire

    // Fonction pour valider et décrypter le token JWT
    public Claims decryptToken(String token) {
        // Décodage et validation du token
        return Jwts.parser()
                .setSigningKey(secret_key) // Utilise la clé secrète
                .parseClaimsJws(token) // On parse le JWT et récupère les "claims" (données)
                .getBody(); // Renvoie les données contenues dans le JWT
    }

    public String getEmailFromToken(String token) {
        Claims claims = decryptToken(token);
        return claims.getSubject(); // Extraction de l'email du payload
    }

    public String getRoleFromToken(String token) {
        Claims claims = decryptToken(token);
        return claims.get("role", String.class); // Extraction du rôle du payload
    }
}