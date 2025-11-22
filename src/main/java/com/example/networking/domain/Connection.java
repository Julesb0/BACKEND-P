package com.example.networking.domain;

public class Connection {
  private String id;
  private String requesterId;
  private String addresseeId;
  private String status;
  private String message;
  private String createdAt;
  private String updatedAt;

  public Connection() {}

  public Connection(String requesterId, String addresseeId, String message) {
    this.requesterId = requesterId;
    this.addresseeId = addresseeId;
    this.message = message;
    this.status = "pending";
  }

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getRequesterId() { return requesterId; }
  public void setRequesterId(String requesterId) { this.requesterId = requesterId; }
  public String getAddresseeId() { return addresseeId; }
  public void setAddresseeId(String addresseeId) { this.addresseeId = addresseeId; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public String getMessage() { return message; }
  public void setMessage(String message) { this.message = message; }
  public String getCreatedAt() { return createdAt; }
  public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
  public String getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}