package com.neil.springcart.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * A service containing methods to generate and validate JWT tokens and to
 * get user information from JWT tokens.
 */
@Component
@AllArgsConstructor
public class JwtUtils {
    private final Environment environment;

    /**
     * Generates a JWT token from the given user details.
     * @param userDetails The user details of the user making the request.
     * @return A JWT token.
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a JWT token from the given extra claims and user details.
     * @param extraClaims Any extra properties to be a part of the JWT token.
     * @param userDetails The user details of the user making the request.
     * @return A JWT token.
     */
    public String generateToken(Map<String, Object> extraClaims,
                                UserDetails userDetails) {
        final int ONE_WEEK = 7 * 24 * 60 * 60 * 1000;
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ONE_WEEK))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Checks if the given token is not expired and is the token generated for
     * the user making the request.
     * @param token The token supplied in the request.
     * @param userDetails The user details of the user making the request.
     * @return `true` if the token is valid and `false` if not.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    /**
     * Extracts the username (email) from the given JWT token.
     * @param token A JWT token.
     * @return The username (email) stored in the token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Checks if the given JWT token is expired or not.
     * @param token A JWT token.
     * @return `true` if the token is expired, `false` if not.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration datetime from the given JWT token.
     * @param token A JWT token.
     * @return The expiration date of the JWT token.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a claim from the given token by using the given claimsResolver.
     * @param token A JWT token.
     * @param claimsResolver A method from the Claims interface.
     * @return The value of the claim.
     */
    private <T> T extractClaim(String token,
                               Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all the claims from the given token.
     * @param token A JWT token.
     * @return the claims from a JWT token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Creates a key from a JWT token from a secret key string
     * @return A key.
     */
    private Key getSignInKey() {
        String SECRET_KEY = environment.getProperty("jwt.key");
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}