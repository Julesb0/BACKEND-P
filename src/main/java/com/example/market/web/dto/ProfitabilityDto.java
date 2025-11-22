package com.example.market.web.dto;

import java.util.List;

public class ProfitabilityDto {
  private double expectedROI;
  private double marginPercent;
  private String revenueTrend;
  private List<String> aiTips;

  public double getExpectedROI() { return expectedROI; }
  public void setExpectedROI(double expectedROI) { this.expectedROI = expectedROI; }
  public double getMarginPercent() { return marginPercent; }
  public void setMarginPercent(double marginPercent) { this.marginPercent = marginPercent; }
  public String getRevenueTrend() { return revenueTrend; }
  public void setRevenueTrend(String revenueTrend) { this.revenueTrend = revenueTrend; }
  public List<String> getAiTips() { return aiTips; }
  public void setAiTips(List<String> aiTips) { this.aiTips = aiTips; }
}

