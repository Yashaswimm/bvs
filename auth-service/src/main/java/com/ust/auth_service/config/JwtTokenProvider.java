package com.ust.auth_service.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    public String createToken(String email, String roles) {
        long now = System.currentTimeMillis();
        long EXPIRATION_TIME = 3600000;
        Date expiryDate = new Date(now + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(now))
                .setExpiration(expiryDate)
                .claim("roles", roles)
                .signWith(SignatureAlgorithm.HS512, getSignKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(getSignKey()).parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            throw new RuntimeException("Token is expired");
        } catch (Exception e) {
            throw new RuntimeException("Invalid token");
        }
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    private Key getSignKey(){
        return Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }
}