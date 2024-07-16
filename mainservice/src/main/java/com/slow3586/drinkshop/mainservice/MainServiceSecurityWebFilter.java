package com.slow3586.drinkshop.mainservice;

import com.slow3586.drinkshop.api.mainservice.entity.Worker;
import com.slow3586.drinkshop.mainservice.repository.CustomerRepository;
import com.slow3586.drinkshop.mainservice.repository.WorkerRepository;
import com.slow3586.drinkshop.mainservice.service.WorkerService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public class MainServiceSecurityWebFilter extends OncePerRequestFilter {
    WorkerService workerService;

    @Override
    protected void doFilterInternal(
        @NonNull final HttpServletRequest request,
        @NonNull final HttpServletResponse response,
        @NonNull final FilterChain filterChain
    ) throws ServletException, IOException {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
            null,
            null);

        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            Worker worker = workerService.token(authorizationHeader.substring("Bearer ".length()));
            token = new UsernamePasswordAuthenticationToken(
                worker.getName(),
                null,
                AuthorityUtils.createAuthorityList(worker.getRole()));
        }

        SecurityContextHolder.getContext().setAuthentication(token);
        filterChain.doFilter(request, response);
    }
}
