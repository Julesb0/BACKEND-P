package com.example.market.web.dto;

public class ProductOverviewDto {
  private String id;
  private String name;
  private String platform;
  private double avgPrice;
  private double rating;
  private int sales;

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getPlatform() { return platform; }
  public void setPlatform(String platform) { this.platform = platform; }
  public double getAvgPrice() { return avgPrice; }
  public void setAvgPrice(double avgPrice) { this.avgPrice = avgPrice; }
  public double getRating() { return rating; }
  public void setRating(double rating) { this.rating = rating; }
  public int getSales() { return sales; }
  public void setSales(int sales) { this.sales = sales; }
}

