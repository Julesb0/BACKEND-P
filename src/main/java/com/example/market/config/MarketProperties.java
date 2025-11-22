package com.example.market.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "market")
public class MarketProperties {
  private String meliSite;

  public String getMeliSite() { return meliSite; }
  public void setMeliSite(String meliSite) { this.meliSite = meliSite; }
}