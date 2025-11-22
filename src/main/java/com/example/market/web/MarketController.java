package com.example.market.web;

import com.example.finance.config.AiProperties;
import com.example.market.service.MarketFacade;
import com.example.market.web.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/market")
public class MarketController {
  private final MarketFacade facade;
  public MarketController(AiProperties aiProps) { this.facade = new MarketFacade(aiProps); }

  @GetMapping("/rankings")
  public ResponseEntity<List<RankingItemDto>> rankings(@RequestHeader(value = "Authorization", required = false) String auth,
                                                       @RequestParam String platform,
                                                       @RequestParam(required = false, defaultValue = "all") String category,
                                                       @RequestParam(required = false, defaultValue = "5") int limit) {
    return ResponseEntity.ok(facade.rankings(platform, category, limit));
  }

  @GetMapping("/product/{id}/overview")
  public ResponseEntity<ProductOverviewDto> overview(@RequestHeader(value = "Authorization", required = false) String auth,
                                                     @PathVariable String id,
                                                     @RequestParam String platform) {
    return ResponseEntity.ok(facade.overview(platform, id));
  }

  @GetMapping("/product/{id}/price-history")
  public ResponseEntity<List<PricePointDto>> priceHistory(@RequestHeader(value = "Authorization", required = false) String auth,
                                                          @PathVariable String id,
                                                          @RequestParam String platform,
                                                          @RequestParam(required = false, defaultValue = "90") int days) {
    return ResponseEntity.ok(facade.priceHistory(platform, id, days));
  }

  @GetMapping("/product/{id}/risk")
  public ResponseEntity<RiskDto> risk(@RequestHeader(value = "Authorization", required = false) String auth,
                                      @PathVariable String id,
                                      @RequestParam String platform) {
    return ResponseEntity.ok(facade.risk(platform, id));
  }

  @GetMapping("/product/{id}/profitability")
  public ResponseEntity<ProfitabilityDto> profitability(@RequestHeader(value = "Authorization", required = false) String auth,
                                                        @PathVariable String id,
                                                        @RequestParam String platform,
                                                        @RequestParam(required = false) Double cost) {
    return ResponseEntity.ok(facade.profitability(platform, id, cost));
  }

  @GetMapping("/product/{id}/insights")
  public ResponseEntity<Map<String, Object>> insights(@RequestHeader(value = "Authorization", required = false) String auth,
                                                      @PathVariable String id,
                                                      @RequestParam String platform,
                                                      @RequestParam(required = false) Double cost) {
    return ResponseEntity.ok(facade.insights(platform, id, cost));
  }

  @GetMapping("/ai-url")
  public ResponseEntity<Map<String, Object>> aiUrl(@RequestParam String url) {
    return ResponseEntity.ok(facade.aiFromUrl(url));
  }
}

