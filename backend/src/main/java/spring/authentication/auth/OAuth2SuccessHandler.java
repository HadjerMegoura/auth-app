package spring.authentication.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import spring.authentication.auth.services.JwtService;
import spring.authentication.entities.Role;
import spring.authentication.entities.User;
import spring.authentication.repositories.userRepository;


import java.io.IOException;
import java.time.Duration;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final userRepository userepository;
    @Value("${app.frontend-url}")
    private String front_uri;

    public OAuth2SuccessHandler(JwtService jwtService, userRepository userepository) {
        this.jwtService = jwtService;
        this.userepository = userepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");

        // Auto-provision: create the user if this is their first login
        userepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setPassword(""); // no password, OAuth-only account
            newUser.setRole(Role.USER); // adjust to your enum/default
            return userepository.save(newUser);
        });

        String token = jwtService.generateToken(email);

        ResponseCookie cookie = ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .secure(false)        // false only for local http testing
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.sendRedirect(front_uri + "/home");
    }
}
