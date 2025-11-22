package com.example.market.web.dto;

import java.util.List;

public class RiskDto {
  private double riskScore;
  private double volatility;
  private double ratingFactor;
  private double negativeReviewFactor;
  private List<String> aiTips;

  public double getRiskScore() { return riskScore; }
  public void setRiskScore(double riskScore) { this.riskScore = riskScore; }
  public double getVolatility() { return volatility; }
  public void setVolatility(double volatility) { this.volatility = volatility; }
  public double getRatingFactor() { return ratingFactor; }
  public void setRatingFactor(double ratingFactor) { this.ratingFactor = ratingFactor; }
  public double getNegativeReviewFactor() { return negativeReviewFactor; }
  public void setNegativeReviewFactor(double negativeReviewFactor) { this.negativeReviewFactor = negativeReviewFactor; }
  public List<String> getAiTips() { return aiTips; }
  public void setAiTips(List<String> aiTips) { this.aiTips = aiTips; }
}

