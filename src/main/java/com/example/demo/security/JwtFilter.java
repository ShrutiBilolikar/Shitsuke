package com.example.demo.security;

import com.example.demo.service.CustomUserDetailsService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    public JwtFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService){
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws java.io.IOException, jakarta.servlet.ServletException {
        final String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // Only process JWT if Authorization header is present
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtUtil.extractSubject(token);
                System.out.println("JWT Filter: Extracted username: " + username + " for path: " + request.getRequestURI());
            } catch (JwtException e) {
                // Invalid token format or expired - log but continue
                // Spring Security will handle authentication failure
                System.out.println("JWT extraction error for path " + request.getRequestURI() + ": " + e.getMessage());
            }
        } else {
            // No Authorization header - this is expected for public endpoints
            if (!request.getRequestURI().startsWith("/auth") && !request.getRequestURI().startsWith("/h2-console")) {
                System.out.println("JWT Filter: No Authorization header for path: " + request.getRequestURI());
            }
        }

        // If we have a username from the token, try to authenticate
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("JWT Filter: ✓ Authentication successful for user: " + username + " on path: " + request.getRequestURI());
                } else {
                    // Token validation failed - log but continue
                    // Spring Security will return 403 if endpoint requires authentication
                    System.out.println("JWT Filter: ✗ Token validation failed for user: " + username + " on path: " + request.getRequestURI());
                    System.out.println("JWT Filter: Token subject: " + username + ", UserDetails username: " + userDetails.getUsername());
                }
            } catch (UsernameNotFoundException e) {
                // User not found - this is a critical error
                System.out.println("JWT Filter: ✗✗✗ USER NOT FOUND in database: " + username + " on path: " + request.getRequestURI());
                System.out.println("JWT Filter: This means the user logged in but doesn't exist in the database anymore.");
                System.out.println("JWT Filter: Possible causes: database reset, user deleted, or email mismatch.");
                // Don't set authentication - Spring Security will return 403
            } catch (Exception e) {
                // Any other exception - log but continue
                System.out.println("JWT Filter: ✗ Error during authentication for path " + request.getRequestURI() + ": " + e.getMessage());
                e.printStackTrace();
            }
        } else if (username == null && authHeader != null) {
            // Token was provided but couldn't extract username
            System.out.println("JWT Filter: ✗ Could not extract username from token for path: " + request.getRequestURI());
        }

        // Continue the filter chain
        // If authentication wasn't set and endpoint requires it, Spring Security will return 403
        filterChain.doFilter(request, response);
    }

}
