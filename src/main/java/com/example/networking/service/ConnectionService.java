package com.example.networking.service;

import com.example.auth.SupabaseProperties;
import com.example.networking.domain.Connection;
import com.example.networking.repository.ConnectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionService {
  private final ConnectionRepository repository;

  public ConnectionService(SupabaseProperties props) {
    this.repository = new ConnectionRepository(props);
  }

  public Connection sendConnectionRequest(String requesterId, String addresseeId, String message) {
    Connection existing = repository.findByUsers(requesterId, addresseeId);
    if (existing != null) {
      return existing;
    }
    Connection connection = new Connection(requesterId, addresseeId, message);
    return repository.save(connection);
  }

  public Connection acceptConnection(String connectionId) {
    return repository.updateStatus(connectionId, "accepted");
  }

  public Connection rejectConnection(String connectionId) {
    return repository.updateStatus(connectionId, "rejected");
  }

  public List<Connection> getConnectionsByUserId(String userId) {
    return repository.findByUserId(userId);
  }

  public List<Connection> getPendingRequestsReceived(String userId) {
    return repository.findByAddresseeId(userId).stream()
      .filter(c -> "pending".equals(c.getStatus()))
      .toList();
  }

  public List<Connection> getAcceptedConnections(String userId) {
    return repository.findByUserId(userId).stream()
      .filter(c -> "accepted".equals(c.getStatus()))
      .toList();
  }

  public Connection getConnectionByUsers(String requesterId, String addresseeId) {
    return repository.findByUsers(requesterId, addresseeId);
  }
}