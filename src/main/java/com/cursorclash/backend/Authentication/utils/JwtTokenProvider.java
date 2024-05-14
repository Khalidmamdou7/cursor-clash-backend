package com.cursorclash.backend.Authentication.utils;

import com.cursorclash.backend.Authentication.entities.User;
import com.cursorclash.backend.Authentication.repositories.UserRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.security.Key;

public class JwtTokenProvider {

    private final Key key;

    @Autowired
    private UserRepo userRepo;

    public JwtTokenProvider() {
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }
    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 3600000);

        return Jwts.builder()
                .setClaims(null)
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public Claims extractClaims(String token) {
        Jws<Claims> jws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        return jws.getBody();
    }

    public User getCurrentUser(String token) {
        token = token.substring(7);
        Claims claims = extractClaims(token);
        return userRepo.findByEmail(claims.getSubject());
    }

}
