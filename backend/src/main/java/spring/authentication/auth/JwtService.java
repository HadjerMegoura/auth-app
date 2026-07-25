package spring.authentication.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secretKey;


    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //generate the token using just the user details without extra claims
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateRefreshToken(new HashMap<>(), userDetails);
    }

    public String generateToken(String email) {
        return generateToken(new HashMap<>(), email);
    }

    public String generateToken(Map<String, Object> extraClaims, String email) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // see bug below
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts
                .builder() //builder design pattern (constructor)
                .setClaims(extraClaims) //claims we want to include in our token
                .setSubject(userDetails.getUsername()) //unique value to be extracted and used in auth
                .setIssuedAt(new Date (System.currentTimeMillis()) ) // created at
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24)) //experation time
                .signWith(getSigningKey(),SignatureAlgorithm.HS256) //needed params for generation (algo + key)
                .compact(); //generate and return the token
    }

    public String generateRefreshToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts
                .builder() //builder design pattern (constructor)
                .setClaims(extraClaims) //claims we want to include in our token
                .setSubject(userDetails.getUsername()) //unique value to be extracted and used in auth
                .setIssuedAt(new Date (System.currentTimeMillis()) ) // created at
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 *  60 * 24 * 7)) //experation time
                .signWith(getSigningKey(),SignatureAlgorithm.HS256) //needed params for generation (algo + key)
                .compact(); //generate and return the token
    }

    //Checks if the token belongs to the user
    public Boolean validatToken(String token, UserDetails userDetails) {
        final String userEmail = extractEmail(token);
        return userEmail.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String extractJwtFromCookies(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if ("access_token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    private Boolean isTokenExpired(String token) {
        return extractExperation(token).before(new Date());
    }

    private Date extractExperation(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
