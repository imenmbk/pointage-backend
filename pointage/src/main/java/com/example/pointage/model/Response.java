package com.example.pointage.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Response {
    private String token;
    private String type = "Bearer"; // Ajouter le type de token (optionnel mais courant)
    private String role; // Ajouter le champ pour le rôle de l'utilisateur
    private User user;

    // Constructeur existant (si vous en avez besoin sans le rôle)
    public Response(String token, User user) {
        this.token = token;
        this.user = user;
    }

    // Nouveau constructeur incluant le rôle
    public Response(String token, String role, User user) {
        this.token = token;
        this.role = role;
        this.user = user;
    }


}