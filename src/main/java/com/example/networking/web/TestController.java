package com.example.networking.web;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @GetMapping("/ping")
    public String ping() {
        return "Backend funcionando - Módulo de Networking implementado!";
    }
    
    @GetMapping("/networking")
    public String networkingInfo() {
        return """
            Módulo de Networking implementado:
            - Perfiles de usuario
            - Conexiones entre usuarios
            - Estados: pending, accepted, rejected
            - Endpoints disponibles en /api/networking/
            """;
    }
}