package spring.authentication.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring.authentication.user.User;
import spring.authentication.user.userRepository;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final userRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstname(request.getFirstName())
                .lastname(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        //create a new user and save it in the databse
        userRepository.save(user);

        //generate a token for the user
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public AuthenticationResponse login(AuthenticationRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (Exception e) {
            log.error("Authentication failed", e);
            throw e;
        }

        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }



    //cookies only login
    public void loginCookiesOnly(AuthenticationRequest authenticationRequest, HttpServletResponse response){

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getEmail(),
                            authenticationRequest.getPassword()
                    )
            );
        } catch (Exception e) {
            log.error("Authentication failed", e);
            throw e;
        }

        var user = userRepository.findByEmail(authenticationRequest.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var jwtRefreshToken = jwtService.generateRefreshToken(user);

        //store token in the cookies
        ResponseCookie cookie = ResponseCookie.from("access_token", jwtToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofDays(1))
                .build();

        //store token in the cookies
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", jwtRefreshToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

    }

    public void logout(HttpServletResponse response) {

        ResponseCookie cookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );
    }

    public void refresh(HttpServletRequest request, HttpServletResponse response) {
        //read the refresh token from the API header or the cookies
        Cookie[] cookies = request.getCookies();

        String requestRefreshToken = null;

        Boolean isTokenValid = false;

        if (cookies == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        for (Cookie cookie : cookies) {
            //read the refresh token from the cookies in the request
            if ("refresh_token".equals(cookie.getName())) {
               requestRefreshToken =  cookie.getValue();

               //get the user email from the token's claims
                String email = jwtService.extractEmail(requestRefreshToken);

                // get the user by its email
                UserDetails user = userRepository.findByEmail(email)
                        .orElseThrow();

                //check if the refresh token is still valid
               isTokenValid = this.jwtService.validatToken(requestRefreshToken, user);

                //if yes: generate a new access token
                if (isTokenValid) {
                    //generate a new access token
                    String jwtAccessToken = this.jwtService.generateToken(user);
                    //store token in the cookies
                    ResponseCookie accessTokencookie = ResponseCookie.from("access_token", jwtAccessToken)
                            .httpOnly(true)
                            .secure(false)
                            .sameSite("Strict")
                            .path("/")
                            .maxAge(Duration.ofDays(1))
                            .build();


                    response.addHeader(HttpHeaders.SET_COOKIE, accessTokencookie.toString());


                    return;
                }
                //else: 403 user u,authenticated
                else {
                    response.setStatus(401);
                }
            }
            //else: 403 user u,authenticated
            if (requestRefreshToken == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }






    }
}
