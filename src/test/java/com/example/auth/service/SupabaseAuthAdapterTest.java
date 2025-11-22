package com.example.auth.service;

import com.example.auth.SupabaseProperties;
import com.example.auth.web.dto.AuthRequestLogin;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SupabaseAuthAdapterTest {
  @Test
  void loginEmailNotConfirmedShowsFriendlyMessage() {
    SupabaseProperties props = new SupabaseProperties();
    SupabaseAuthService supabase = new SupabaseAuthService(props) {
      @Override
      public Result login(String email, String password) {
        String body = "{\"code\":400,\"error_code\":\"email_not_confirmed\",\"msg\":\"Email not confirmed\"}";
        return new Result(400, body);
      }
    };
    SupabaseAuthAdapter adapter = new SupabaseAuthAdapter(supabase, null);

    AuthRequestLogin req = new AuthRequestLogin();
    req.setEmail("user@example.com");
    req.setPassword("secret");

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> adapter.login(req));
    assertEquals("Revisa tu correo para autenticarlo", ex.getMessage());
  }
}

