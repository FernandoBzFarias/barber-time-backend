package com.barbertime.security;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.barbertime.repository.BarbeiroRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private BarbeiroRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // 🔓 LOGICA DE IGNORAR REFORMULADA
        if (path.startsWith("/api/barbeiros/publico/") || 
            path.equals("/api/barbeiros/login") ||
            path.equals("/api/barbeiros/cadastro") ||
            path.equals("/api/barbeiros/esqueci-senha") ||
            path.equals("/api/barbeiros/redefinir-senha") ||
            path.equals("/api/barbeiros") || 
            path.equals("/api/barbeiros/disponibilidade")) {

            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.replace("Bearer ", "");

            try {
                String email = jwtService.extractEmail(token);

                var barbeiro = repository.findByEmail(email).orElse(null);

                if (barbeiro != null) {
                    var auth = new UsernamePasswordAuthenticationToken(
                            barbeiro.getEmail(),
                            null,
                            java.util.Collections.emptyList()
                    );

                    SecurityContextHolder.getContext().setAuthentication(auth);
                }

            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
  }