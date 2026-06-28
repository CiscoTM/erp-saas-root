package es.tk3.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;

@Component
public class JwtAuthFilter extends  AbstractGatewayFilterFactory<JwtAuthFilter.Config> implements Ordered {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    public JwtAuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return  (exchange, chain) -> {
            if(!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                return onError(exchange, "Falta la cabecera Authorization", HttpStatus.UNAUTHORIZED);
            }
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if(authHeader == null || !authHeader.startsWith("Bearer ")){
                return onError(exchange, "Cabecera Authorization inválida", HttpStatus.UNAUTHORIZED);
            }
            String token = authHeader.substring(7);

            try {
                byte[] keyBytes = Decoders.BASE64.decode(secretKey);
                SecretKey key = Keys.hmacShaKeyFor(keyBytes);

                Claims claims = Jwts.parser().verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                String username = claims.getSubject();
                String tenantId = claims.get("tenantId", String.class);
                String role = claims.get("role", String.class);

                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header("X-Tenant-ID", tenantId)
                        .header("X-User-Name", username)
                        .header("X-User-Role", role)
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (Exception e) {
                return onError(exchange, "Token inválido o expirado", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }


    public static class Config {}
}
