package tn.youssef.api_gateway;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.util.UUID;

public class TestJwtGenerator {

    public static void main(String[] args) {
        // EXACT secret from your .env file
        String secret = "b7Kp9mR2xL4nW8vJ3hF6tY1sA5dG0qZw";
        
        // This is the UUID the API Gateway will extract and pass to Forum Service
        String simulatedUserId = UUID.randomUUID().toString();
        
        String token = Jwts.builder()
                .setSubject(simulatedUserId) // The 'sub' claim
                .claim("role", "ROLE_USER")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 hours
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();

        System.out.println("==================================================");
        System.out.println("USER ID (simulated): " + simulatedUserId);
        System.out.println("==================================================");
        System.out.println("Bearer " + token);
        System.out.println("==================================================");
    }
}
