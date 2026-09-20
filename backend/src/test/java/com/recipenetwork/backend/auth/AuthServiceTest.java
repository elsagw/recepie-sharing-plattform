package com.recipenetwork.backend.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private AuthService authService;

  @Test
  void shouldHashPasswordWhenRegistering() {
    AuthRequests.Register request = new AuthRequests.Register(" elsa ", "ELSA@example.com ", "secret123");
    when(passwordEncoder.encode("secret123")).thenReturn("hashed-password");
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    User user = authService.register(request);

    assertThat(user.getUsername()).isEqualTo("elsa");
    assertThat(user.getEmail()).isEqualTo("elsa@example.com");
    assertThat(user.getPasswordHash()).isEqualTo("hashed-password");
    verify(passwordEncoder).encode("secret123");
  }

  @Test
  void shouldRejectDuplicateUsername() {
    AuthRequests.Register request = new AuthRequests.Register("elsa", "elsa@example.com", "secret123");
    when(userRepository.existsByUsername("elsa")).thenReturn(true);

    assertThatThrownBy(() -> authService.register(request))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("Username is already in use");
  }
}