package com.example.market.service;

import com.example.finance.config.AiProperties;
import com.example.finance.service.AiAdvisor;
import com.example.market.service.adapter.MercadoLibreAdapter;
import com.example.market.web.dto.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MarketFacade {
  private final AiAdvisor ai;
  private final Random rnd = new Random(42);
  private final MercadoLibreAdapter meli = new MercadoLibreAdapter();
  private final java.util.concurrent.ConcurrentHashMap<String, java.util.List<PricePointDto>> history = new java.util.concurrent.ConcurrentHashMap<>();

  public MarketFacade(AiProperties aiProps) { this.ai = new AiAdvisor(aiProps); }

  public List<RankingItemDto> rankings(String platform, String category, int limit) {
    List<RankingItemDto> out = new ArrayList<>();
    if (platform != null && platform.equalsIgnoreCase("mercadolibre")) {
      return meli.rankings(category, limit);
    }
    for (int i = 1; i <= Math.max(1, limit); i++) {
      RankingItemDto it = new RankingItemDto();
      it.setId(platform + "-" + category + "-" + i);
      it.setName("Producto " + i);
      it.setPlatform(platform);
      it.setPrice(10 + rnd.nextDouble() * 90);
      it.setSales(1000 + rnd.nextInt(5000));
      it.setRating(3.0 + rnd.nextDouble() * 2.0);
      out.add(it);
    }
    return out;
  }

  public ProductOverviewDto overview(String platform, String id) {
    ProductOverviewDto o = new ProductOverviewDto();
    if (platform != null && platform.equalsIgnoreCase("mercadolibre")) {
      return meli.overview(id);
    }
    o.setId(id); o.setPlatform(platform);
    o.setName("Producto " + id);
    o.setAvgPrice(20 + rnd.nextDouble() * 80);
    o.setRating(3.5 + rnd.nextDouble() * 1.5);
    o.setSales(500 + rnd.nextInt(7000));
    return o;
  }

  public List<PricePointDto> priceHistory(String platform, String id, int days) {
    recordCurrentPrice(platform, id);
    long cutoff = System.currentTimeMillis() - Math.max(1, days) * 86_400_000L;
    List<PricePointDto> series = history.getOrDefault(key(platform, id), new ArrayList<>());
    List<PricePointDto> out = new ArrayList<>();
    for (PricePointDto p : series) { if (p.getTs() >= cutoff) out.add(p); }
    return out.isEmpty() ? series : out;
  }

  public RiskDto risk(String platform, String id) {
    // factores simulados
    double volatility = 0.15 + rnd.nextDouble() * 0.35; // 0..0.5
    double rating = 3.0 + rnd.nextDouble() * 2.0; // 3..5
    double negatives = rnd.nextDouble() * 0.3; // 0..0.3
    double ratingNorm = (rating - 1.0) / 4.0;
    double riskScore = clamp(100 * (0.5 * volatility + 0.3 * (1 - ratingNorm) + 0.2 * negatives));
    RiskDto r = new RiskDto();
    r.setVolatility(volatility);
    r.setRatingFactor(rating);
    r.setNegativeReviewFactor(negatives);
    r.setRiskScore(riskScore);
    String prompt = "Evalúa riesgo del producto id " + id + " en " + platform + 
      ". Factores: volatilidad=" + to2(volatility) + ", rating=" + to2(rating) + ", negativas=" + to2(negatives) + 
      ". Devuelve 5 recomendaciones para mitigar riesgo.";
    r.setAiTips(ai.advise(prompt));
    return r;
  }

  public ProfitabilityDto profitability(String platform, String id, Double cost) {
    ProductOverviewDto o = overview(platform, id);
    double c = cost != null ? cost : o.getAvgPrice() * 0.7; // coste estimado
    double margin = Math.max(0, o.getAvgPrice() - c);
    double roi = c > 0 ? margin / c : 0;
    String trend = margin > o.getAvgPrice() * 0.2 ? "sube" : (margin < o.getAvgPrice() * 0.1 ? "baja" : "igual");
    ProfitabilityDto p = new ProfitabilityDto();
    p.setExpectedROI(to2(roi));
    p.setMarginPercent(to2(margin / (o.getAvgPrice() == 0 ? 1 : o.getAvgPrice())));
    p.setRevenueTrend(trend);
    String prompt = "Evalúa rentabilidad del producto id " + id + " en " + platform + 
      ". avgPrice=" + to2(o.getAvgPrice()) + ", cost=" + to2(c) + ", ROI=" + to2(roi) + 
      ". Devuelve 5 recomendaciones para mejorar ROI.";
    p.setAiTips(ai.advise(prompt));
    return p;
  }

  public java.util.Map<String, Object> insights(String platform, String id, Double cost) {
    java.util.Map<String, Object> out = new java.util.HashMap<>();
    var ov = overview(platform, id);
    var r = risk(platform, id);
    var pr = profitability(platform, id, cost);
    out.put("overview", ov);
    out.put("risk", r);
    out.put("profitability", pr);
    String prompt = "Genera 5 recomendaciones para el producto id " + id + 
      " combinando overview, riesgo y rentabilidad en " + platform + ".";
    out.put("aiAdvice", ai.advise(prompt));
    return out;
  }

  public java.util.Map<String, Object> aiFromUrl(String url) {
    java.util.Map<String, Object> out = new java.util.HashMap<>();
    String host = host(url);
    String html = fetch(url);
    String title = extract(html, "<title>", "</title>");
    String desc = extractMeta(html, "description");
    if (title == null || title.isBlank()) {
      String mt = extractMeta(html, "title");
      if (mt != null && !mt.isBlank()) title = mt;
    }
    if (title == null || title.isBlank()) title = guessTitle(url, host);
    if (desc == null || desc.isBlank()) desc = guessDescription(host, title);
    out.put("url", url);
    out.put("host", host);
    out.put("title", title);
    out.put("description", desc);
    String prompt = "Analiza la URL y explica qué es y cómo evaluarla para inversión: URL=" + url + 
      ", dominio=" + host + ", título=" + safe(title) + ", descripción=" + safe(desc) + ". Devuelve 5 recomendaciones.";
    java.util.List<String> tips = ai.advise(prompt);
    if (tips == null || tips.isEmpty()) tips = defaultTips(host, title, desc);
    out.put("aiAdvice", tips);
    return out;
  }

  private double clamp(double v) { return Math.max(0, Math.min(100, v)); }
  private double to2(double v) { return Math.round(v * 100.0) / 100.0; }
  private String key(String platform, String id) { return (platform == null ? "" : platform.toLowerCase()) + "|" + id; }
  private void recordCurrentPrice(String platform, String id) {
    double price;
    if (platform != null && platform.equalsIgnoreCase("mercadolibre")) {
      try { price = meli.overview(id).getAvgPrice(); } catch (Exception e) { price = 0; }
    } else {
      price = 20 + rnd.nextDouble() * 80;
    }
    PricePointDto p = new PricePointDto();
    p.setTs(System.currentTimeMillis());
    p.setPrice(price);
    history.computeIfAbsent(key(platform, id), k -> new ArrayList<>()).add(p);
    List<PricePointDto> list = history.get(key(platform, id));
    if (list.size() > 500) { list.remove(0); }
  }
  private String host(String url) {
    try { return java.net.URI.create(url).getHost(); } catch (Exception e) { return ""; }
  }
  private String fetch(String url) {
    try {
      var req = java.net.http.HttpRequest.newBuilder().uri(java.net.URI.create(url)).header("User-Agent","Mozilla/5.0").GET().build();
      var res = java.net.http.HttpClient.newHttpClient().send(req, java.net.http.HttpResponse.BodyHandlers.ofString());
      return res.body();
    } catch (Exception e) { return ""; }
  }
  private String extract(String html, String startTag, String endTag) {
    try {
      java.util.regex.Pattern p = java.util.regex.Pattern.compile("(?is)" + java.util.regex.Pattern.quote(startTag) + "\\s*(.*?)\\s*" + java.util.regex.Pattern.quote(endTag));
      java.util.regex.Matcher m = p.matcher(html);
      if (m.find()) return m.group(1).trim();
      return "";
    } catch (Exception e) { return ""; }
  }
  private String extractMeta(String html, String name) {
    try {
      String n = name.toLowerCase();
      java.util.regex.Pattern p = java.util.regex.Pattern.compile("(?is)<meta[^>]*?(?:name|property)\\s*=\\s*\"(?:" + n + "|og:" + n + ")\"[^>]*?content\\s*=\\s*\"([^\"]+)\"[^>]*>");
      java.util.regex.Matcher m = p.matcher(html);
      if (m.find()) return m.group(1).trim();
      return "";
    } catch (Exception e) { return ""; }
  }
  private String safe(String s) { return s == null ? "" : s.replace("\n"," ").replace("\r"," ").trim(); }
  private java.util.List<String> defaultTips(String host, String title, String desc) {
    java.util.List<String> out = new java.util.ArrayList<>();
    String h = host == null ? "" : host.toLowerCase();
    out.add("Verifica reputación del vendedor y reseñas recientes");
    out.add("Compara precios históricos y actuales para detectar variaciones");
    out.add("Evalúa costos ocultos: envío, impuestos y devoluciones");
    out.add("Analiza demanda: ranking, disponibilidad y frecuencia de reposición");
    if (h.contains("mercadolibre")) out.add("En Mercado Libre, prioriza publicaciones con Mercado Envíos y reputación verde");
    else if (h.contains("amazon")) out.add("En Amazon, revisa Buy Box y ofertas de vendedores con Prime");
    else if (h.contains("aliexpress")) out.add("En AliExpress, evalúa protección al comprador y tiempos de entrega");
    else out.add("Comprueba políticas de garantía del dominio y soporte postventa");
    while (out.size() > 5) out.remove(out.size()-1);
    return out;
  }
  private String guessTitle(String url, String host) {
    try {
      java.net.URI u = java.net.URI.create(url);
      String path = u.getPath();
      if (path == null || path.isBlank() || "/".equals(path)) return "Página en " + host;
      String last = path.substring(path.lastIndexOf('/') + 1);
      last = java.net.URLDecoder.decode(last, java.nio.charset.StandardCharsets.UTF_8);
      last = last.replace('-', ' ').replace('_', ' ').trim();
      if (last.isBlank()) return "Página en " + host;
      return toTitleCase(last);
    } catch (Exception e) { return "Página en " + host; }
  }
  private String guessDescription(String host, String title) {
    String t = safe(title);
    if (t.isBlank()) t = host;
    return "Descripción estimada: recurso en " + host + " — " + t;
  }
  private String toTitleCase(String s) {
    String[] parts = s.split("\\s+");
    StringBuilder sb = new StringBuilder();
    for (String p : parts) {
      if (p.isEmpty()) continue;
      sb.append(Character.toUpperCase(p.charAt(0))).append(p.length()>1 ? p.substring(1) : "").append(' ');
    }
    return sb.toString().trim();
  }
}

