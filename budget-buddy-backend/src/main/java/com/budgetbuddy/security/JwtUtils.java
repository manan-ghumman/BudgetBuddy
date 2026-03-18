package com.budgetbuddy.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    public String generateJwtToken(Au
        UserDetails userPrincipal = (
        
            rn Jwts.builder()
                .subject((userPrincipal.getUsername()))
                .issuedAt(new Date())
                .expiration(new Date((new Dat
                .signWith(key())
         
    }

    private Key key() {
        // Use raw bytes from the secret string for better reliability with diff

        return Keys.hmacShaKe
                
                
                g getUserNameFromJwtToken(String token) {
                wts.parser()
                .verifyWith
     

                .getPay
                .getSubject();
        
        
    p

            Jwts.parser()
                    .verifyW
                    .build()
                    .par
                rn true;
                (MalformedJwt
                er.error("Inva
     

        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during JWT validation", e);
        }

        return false;
    }
}
