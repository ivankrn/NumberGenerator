package ru.ivankrn.numbergenerator.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.ivankrn.numbergenerator.api.exception.ErrorResponse;

import java.io.IOException;

// TODO Спросить, возможно написание фильтров вручную уже неактуально:
//  https://github.com/spring-projects/spring-security-samples/tree/main/servlet/spring-boot/java/jwt/login

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER_NAME = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String INVALID_TOKEN = "Invalid token";
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper mapper;

    // TODO Спросить помечают ли одиночные конструкторы аннотацией @Autowired для читаемости
    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService, ObjectMapper mapper) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.mapper = mapper;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader(AUTH_HEADER_NAME);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        final String jwt = authHeader.substring(BEARER_PREFIX.length());
        if (jwtService.isTokenValid(jwt)) {
            final String username = jwtService.extractUsername(jwt);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } else {
            // TODO Спросить как лучше сделать обработку невалидного токена, варианты:
            //  1) использовать HandlerExceptionResolver для передачи исключений на уровень контроллеров и перехвата там
            //  2) дополнительно определять фильтр, который будет проверять на выбрасывание исключения
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED, INVALID_TOKEN);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(mapper.writeValueAsString(errorResponse));
            return;
        }
        filterChain.doFilter(request, response);
    }

}
