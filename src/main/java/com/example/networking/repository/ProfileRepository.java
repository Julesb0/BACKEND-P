package com.example.networking.repository;

import com.example.auth.SupabaseProperties;
import com.example.networking.domain.Profile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ProfileRepository {
  private final SupabaseProperties props;
  private final HttpClient httpClient = HttpClient.newHttpClient();
  private final ObjectMapper mapper = new ObjectMapper();
  private final String table = "profiles";

  public ProfileRepository(SupabaseProperties props) {
    this.props = props;
  }

  public Profile save(Profile profile) {
    try {
      String json = toJson(profile);
      HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create(props.getUrl() + "/rest/v1/" + table))
        .header("Content-Type", "application/json")
        .header("apikey", apiKey())
        .header("Authorization", "Bearer " + apiKey())
        .header("Prefer", "return=representation")
        .POST(HttpRequest.BodyPublishers.ofString(json))
        .build();
      HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
      if (res.statusCode() >= 200 && res.statusCode() < 300) {
        JsonNode arr = mapper.readTree(res.body());
        if (arr.isArray() && arr.size() > 0) {
          return fromJson(arr.get(0));
        }
        return profile;
      }
      throw new RuntimeException("Supabase save failed: " + res.body());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public Profile findByUserId(String userId) {
    try {
      List<Profile> profiles = query("user_id=eq." + encode(userId));
      return profiles.isEmpty() ? null : profiles.get(0);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public List<Profile> findAllPublic() {
    return query("is_public=eq.true");
  }

  public Profile update(Profile profile) {
    try {
      String json = toUpdateJson(profile);
      HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create(props.getUrl() + "/rest/v1/" + table + "?id=eq." + encode(profile.getId())))
        .header("Content-Type", "application/json")
        .header("apikey", apiKey())
        .header("Authorization", "Bearer " + apiKey())
        .header("Prefer", "return=representation")
        .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
        .build();
      HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
      if (res.statusCode() >= 200 && res.statusCode() < 300) {
        JsonNode arr = mapper.readTree(res.body());
        if (arr.isArray() && arr.size() > 0) {
          return fromJson(arr.get(0));
        }
        return profile;
      }
      throw new RuntimeException("Supabase update failed: " + res.body());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private List<Profile> query(String filter) {
    try {
      HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create(props.getUrl() + "/rest/v1/" + table + "?" + filter))
        .header("apikey", apiKey())
        .header("Authorization", "Bearer " + apiKey())
        .build();
      HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
      if (res.statusCode() >= 200 && res.statusCode() < 300) {
        List<Profile> list = new ArrayList<>();
        JsonNode arr = mapper.readTree(res.body());
        if (arr.isArray()) {
          for (JsonNode n : arr) {
            list.add(fromJson(n));
          }
        }
        return list;
      }
      throw new RuntimeException("Supabase query failed: " + res.body());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private String toJson(Profile p) {
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    if (p.getId() == null || p.getId().isEmpty()) {
      p.setId(java.util.UUID.randomUUID().toString());
    }
    sb.append("\"id\":\"").append(p.getId()).append("\",");
    sb.append("\"user_id\":\"").append(escape(p.getUserId())).append("\",");
    sb.append("\"display_name\":\"").append(escape(p.getDisplayName())).append("\",");
    if (p.getBio() != null) sb.append("\"bio\":\"").append(escape(p.getBio())).append("\",");
    if (p.getLocation() != null) sb.append("\"location\":\"").append(escape(p.getLocation())).append("\",");
    if (p.getCompany() != null) sb.append("\"company\":\"").append(escape(p.getCompany())).append("\",");
    if (p.getRole() != null) sb.append("\"role\":\"").append(escape(p.getRole())).append("\",");
    if (p.getLinkedinUrl() != null) sb.append("\"linkedin_url\":\"").append(escape(p.getLinkedinUrl())).append("\",");
    if (p.getWebsite() != null) sb.append("\"website\":\"").append(escape(p.getWebsite())).append("\",");
    sb.append("\"is_public\":").append(p.isPublic()).append(",");
    sb.append("\"created_at\":\"").append(java.time.Instant.now().toString()).append("\",");
    sb.append("\"updated_at\":\"").append(java.time.Instant.now().toString()).append("\"");
    sb.append("}");
    return sb.toString();
  }

  private String toUpdateJson(Profile p) {
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    boolean first = true;
    if (p.getDisplayName() != null) { sb.append("\"display_name\":\"").append(escape(p.getDisplayName())).append("\""); first = false; }
    if (p.getBio() != null) { if (!first) sb.append(","); sb.append("\"bio\":\"").append(escape(p.getBio())).append("\""); first = false; }
    if (p.getLocation() != null) { if (!first) sb.append(","); sb.append("\"location\":\"").append(escape(p.getLocation())).append("\""); first = false; }
    if (p.getCompany() != null) { if (!first) sb.append(","); sb.append("\"company\":\"").append(escape(p.getCompany())).append("\""); first = false; }
    if (p.getRole() != null) { if (!first) sb.append(","); sb.append("\"role\":\"").append(escape(p.getRole())).append("\""); first = false; }
    if (p.getLinkedinUrl() != null) { if (!first) sb.append(","); sb.append("\"linkedin_url\":\"").append(escape(p.getLinkedinUrl())).append("\""); first = false; }
    if (p.getWebsite() != null) { if (!first) sb.append(","); sb.append("\"website\":\"").append(escape(p.getWebsite())).append("\""); first = false; }
    sb.append(",\"is_public\":").append(p.isPublic());
    sb.append(",\"updated_at\":\"").append(java.time.Instant.now().toString()).append("\"");
    sb.append("}");
    return sb.toString();
  }

  private Profile fromJson(JsonNode n) {
    Profile p = new Profile();
    if (n.get("id") != null) p.setId(n.get("id").asText());
    if (n.get("user_id") != null) p.setUserId(n.get("user_id").asText());
    if (n.get("display_name") != null) p.setDisplayName(n.get("display_name").asText());
    if (n.get("bio") != null) p.setBio(n.get("bio").asText());
    if (n.get("location") != null) p.setLocation(n.get("location").asText());
    if (n.get("company") != null) p.setCompany(n.get("company").asText());
    if (n.get("role") != null) p.setRole(n.get("role").asText());
    if (n.get("linkedin_url") != null) p.setLinkedinUrl(n.get("linkedin_url").asText());
    if (n.get("website") != null) p.setWebsite(n.get("website").asText());
    if (n.get("is_public") != null) p.setPublic(n.get("is_public").asBoolean());
    if (n.get("created_at") != null) p.setCreatedAt(n.get("created_at").asText());
    if (n.get("updated_at") != null) p.setUpdatedAt(n.get("updated_at").asText());
    return p;
  }

  private String escape(String s) { return s == null ? "" : s.replace("\"","\\\""); }
  private String encode(String s) { return java.net.URLEncoder.encode(s, StandardCharsets.UTF_8); }
  private String apiKey() { return (props.getServiceRoleKey() != null && !props.getServiceRoleKey().isBlank()) ? props.getServiceRoleKey() : props.getAnonKey(); }
}