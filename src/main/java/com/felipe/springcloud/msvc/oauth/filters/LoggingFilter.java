package com.felipe.springcloud.msvc.oauth.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class LoggingFilter extends OncePerRequestFilter {
        private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

        @Override
        protected void doFilterInternal(HttpServletRequest request,
                        HttpServletResponse response,
                        FilterChain filterChain)
                        throws ServletException, IOException {

                long start = System.currentTimeMillis();

                // Log del request entrante
                String query = request.getQueryString() != null
                                ? "?" + request.getQueryString()
                                : "";
                log.info("→ REQUEST  {} {}{}",
                                request.getMethod(),
                                request.getRequestURL(),
                                query);

                // Continúa la cadena de filtros / servlet
                filterChain.doFilter(request, response);

                // Al volver, registro status y tiempo
                long duration = System.currentTimeMillis() - start;
                log.info("← RESPONSE {} {}{} → {} ({} ms)",
                                request.getMethod(),
                                request.getRequestURL(),
                                query,
                                response.getStatus(),
                                duration);
        }
}