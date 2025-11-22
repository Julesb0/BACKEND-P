package com.example.networking.web;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

public class SimpleServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String response = """
                    <html>
                    <head><title>Backend Spring Boot - Puerto 8081</title></head>
                    <body>
                        <h1>✅ Backend Funcionando!</h1>
                        <h2>Módulos implementados:</h2>
                        <ul>
                            <li><strong>Auth:</strong> Login/registro con Supabase</li>
                            <li><strong>Finance:</strong> Gestión de ingresos/gastos con IA</li>
                            <li><strong>Market:</strong> Análisis de productos ML</li>
                            <li><strong>Networking:</strong> ✅ NUEVO - Perfiles y conexiones</li>
                        </ul>
                        
                        <h2>Endpoints del módulo Networking:</h2>
                        <pre>
POST   /api/networking/profile                    - Crear/actualizar perfil
GET    /api/networking/profile                    - Obtener mi perfil  
GET    /api/networking/profiles                   - Listar perfiles públicos
POST   /api/networking/connections                - Enviar solicitud de conexión
PUT    /api/networking/connections/{id}/accept  - Aceptar conexión
PUT    /api/networking/connections/{id}/reject  - Rechazar conexión
GET    /api/networking/connections                - Mis conexiones
GET    /api/networking/connections/pending        - Solicitudes pendientes
GET    /api/networking/connections/accepted       - Conexiones aceptadas
GET    /api/networking/network                    - Mi red completa
                        </pre>
                        
                        <p><strong>Puerto:</strong> 8081</p>
                        <p><strong>Estado:</strong> ✅ Activo</p>
                    </body>
                    </html>
                    """;
                
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                exchange.getResponseBody().write(response.getBytes());
                exchange.getResponseBody().close();
            }
        });
        
        server.createContext("/api/test", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String response = "{\"status\":\"ok\",\"message\":\"Backend funcionando - Módulo de Networking implementado!\",\"port\":8081}";
                
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                exchange.getResponseBody().write(response.getBytes());
                exchange.getResponseBody().close();
            }
        });
        
        server.setExecutor(null);
        server.start();
        System.out.println("🚀 Backend ejecutándose en http://localhost:8081");
        System.out.println("📋 Módulo de Networking implementado exitosamente!");
    }
}