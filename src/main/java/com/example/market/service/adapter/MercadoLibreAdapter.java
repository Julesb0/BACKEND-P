package com.example.market.service.adapter;

import com.example.market.web.dto.ProductOverviewDto;
import com.example.market.web.dto.RankingItemDto;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class MercadoLibreAdapter {
  private final HttpClient http = HttpClient.newHttpClient();
  private final com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

  public List<RankingItemDto> rankings(String category, int limit) {
    try {
      String site = System.getProperty("MELI_SITE", "MCO");
      String url = "https://api.mercadolibre.com/sites/" + site + "/search?q=" + escape(category) + "&limit=" + Math.max(1, limit);
      HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
      HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
      List<RankingItemDto> out = new ArrayList<>();
      if (res.statusCode() >= 200 && res.statusCode() < 300) {
        var root = mapper.readTree(res.body());
        var results = root.get("results");
        if (results != null && results.isArray()) {
          int n = Math.min(results.size(), Math.max(1, limit));
          for (int i = 0; i < n; i++) {
            var it = results.get(i);
            String id = it.get("id").asText();
            RankingItemDto r = new RankingItemDto();
            r.setId(id);
            r.setName(it.get("title") == null ? id : it.get("title").asText());
            r.setPlatform("mercadolibre");
            r.setPrice(it.get("price") == null ? 0 : it.get("price").asDouble());
            r.setSales(0);
            r.setRating(0);
            out.add(r);
          }
        }
      }
      return out;
    } catch (Exception e) { return new ArrayList<>(); }
  }

  public ProductOverviewDto overview(String id) {
    try {
      String url = "https://api.mercadolibre.com/items/" + escape(id);
      HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
      HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
      ProductOverviewDto o = new ProductOverviewDto();
      o.setId(id);
      o.setPlatform("mercadolibre");
      if (res.statusCode() >= 200 && res.statusCode() < 300) {
        var root = mapper.readTree(res.body());
        o.setName(root.get("title") == null ? id : root.get("title").asText());
        o.setAvgPrice(root.get("price") == null ? 0 : root.get("price").asDouble());
        o.setSales(root.get("sold_quantity") == null ? 0 : root.get("sold_quantity").asInt());
        o.setRating(0);
      } else {
        o.setName(id);
        o.setAvgPrice(0);
        o.setSales(0);
        o.setRating(0);
      }
      return o;
    } catch (Exception e) {
      ProductOverviewDto o = new ProductOverviewDto();
      o.setId(id); o.setPlatform("mercadolibre"); o.setName(id); o.setAvgPrice(0); o.setSales(0); o.setRating(0);
      return o;
    }
  }

  private String escape(String s) { return s == null ? "" : s.replace(" ", "%20"); }
}

