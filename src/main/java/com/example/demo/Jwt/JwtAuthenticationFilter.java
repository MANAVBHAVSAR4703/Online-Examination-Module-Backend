package com.example.demo.Jwt;

import com.example.demo.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtService;

    @Autowired
    private ApplicationContext applicationContext;

    // Method to lazily fetch the UserService bean from the ApplicationContext
    // This is done to avoid Circular Dependency issues
    private UserService getUserService() {
        return applicationContext.getBean(UserService.class);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String userName = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            userName = jwtService.extractUsername(token);
        }

        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = getUserService().loadUserByUsername(userName);
            if (jwtService.isTokenValid(token, userDetails)) {
                List<SimpleGrantedAuthority> authorities = userDetails.getAuthorities().stream()
                        .map(auth -> new SimpleGrantedAuthority("ROLE_" + auth.getAuthority()))
                        .toList();
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Authorization Header: " + authHeader);
        System.out.println( SecurityContextHolder.getContext().getAuthentication());
        filterChain.doFilter(request, response);
    }

}
