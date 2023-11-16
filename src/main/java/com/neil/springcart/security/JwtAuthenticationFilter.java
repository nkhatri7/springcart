package com.neil.springcart.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * A JWT Authentication Filter to filter incoming requests and authenticate
 * them.
 */
@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    /**
     * Filters incoming requests by checking the JWT tokens and authenticating
     * the user making the request.
     * @param request The incoming request.
     * @param response The response from the request.
     * @param filterChain The filter chain for the request.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthenticatedErrorMessage(response);
            return;
        }

        final String token = authHeader.substring(7);
        final String userEmail = jwtUtils.extractUsername(token);
        if (userEmail == null) {
            sendUnauthenticatedErrorMessage(response);
            return;
        }

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if (authentication == null) {
            UserDetails userDetails = this.userDetailsService
                    .loadUserByUsername(userEmail);
            if (!jwtUtils.isTokenValid(token, userDetails)) {
                sendForbiddenErrorMessage(response);
                return;
            }
            setRequestAuthentication(userDetails, request);
        }

        filterChain.doFilter(request, response);
    }

    private void sendUnauthenticatedErrorMessage(HttpServletResponse response)
            throws IOException {
        response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid token");
    }

    private void sendForbiddenErrorMessage(HttpServletResponse response)
            throws IOException {
        response.sendError(HttpStatus.FORBIDDEN.value(), "Invalid token");
    }

    /**
     * Updates the authentication details for the security context holder.
     * @param userDetails The user details of the user making the request.
     * @param request The incoming request.
     */
    private void setRequestAuthentication(UserDetails userDetails,
                                          HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource()
                .buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}