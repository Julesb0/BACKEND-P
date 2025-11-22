package com.example.market.web.dto;

public class RankingItemDto {
  private String id;
  private String name;
  private String platform;
  private double price;
  private int sales;
  private double rating;

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getPlatform() { return platform; }
  public void setPlatform(String platform) { this.platform = platform; }
  public double getPrice() { return price; }
  public void setPrice(double price) { this.price = price; }
  public int getSales() { return sales; }
  public void setSales(int sales) { this.sales = sales; }
  public double getRating() { return rating; }
  public void setRating(double rating) { this.rating = rating; }
}

