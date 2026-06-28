package es.tk3.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver tenantKeyResolver(){
        return exchange -> {
          String tenantId = exchange.getRequest().getHeaders().getFirst("X-Tenant-ID");
            System.out.println(">>> [RATE-LIMITER] Evaluando clave: " + (tenantId != null ? tenantId : "anonymous"));
          return Mono.just(tenantId != null ? tenantId : "anonymous");
        };
    }
}
