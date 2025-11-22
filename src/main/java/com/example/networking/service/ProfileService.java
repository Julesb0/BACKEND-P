package com.example.networking.service;

import com.example.auth.SupabaseProperties;
import com.example.networking.domain.Profile;
import com.example.networking.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileService {
  private final ProfileRepository repository;

  public ProfileService(SupabaseProperties props) {
    this.repository = new ProfileRepository(props);
  }

  public Profile createProfile(String userId, String displayName) {
    Profile profile = new Profile(userId, displayName);
    return repository.save(profile);
  }

  public Profile getProfileByUserId(String userId) {
    return repository.findByUserId(userId);
  }

  public Profile updateProfile(Profile profile) {
    return repository.update(profile);
  }

  public List<Profile> getAllPublicProfiles() {
    return repository.findAllPublic();
  }

  public Profile getOrCreateProfile(String userId, String displayName) {
    Profile profile = repository.findByUserId(userId);
    if (profile == null) {
      profile = createProfile(userId, displayName);
    }
    return profile;
  }
}