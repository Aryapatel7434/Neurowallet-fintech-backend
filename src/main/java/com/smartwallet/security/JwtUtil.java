package com.smartwallet.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component//create object of Jwtutil automatically
public class JwtUtil {
//Secret key
    private static final String SECRET =
            "smartwalletsecretkeysmartwalletsecretkey123456";
     //this method convert secret string into cryptographic key
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }
    //Extract email with Token

    public String generateToken(String email, String role) {
    return Jwts.builder()
            .subject(email)
            .claim("role", role)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
            .signWith(getSigningKey())
            .compact();
}
    public String extractEmail(String token){
        Claims claims=Jwts.parser().verifyWith((javax.crypto.SecretKey)getSigningKey()).build().parseSignedClaims(token).getPayload();
        
        return claims.getSubject();
    }
    public boolean validateToken(String token, UserDetails userDetails){
        try{
            Jwts.parser().verifyWith((javax.crypto.SecretKey)getSigningKey()).build().parseSignedClaims(token);
            
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
   public String extractRole(String token) {
    Claims claims = Jwts.parser()
            .verifyWith((javax.crypto.SecretKey) getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();

    return claims.get("role", String.class);
}
}