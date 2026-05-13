package com.hireconnect.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RequestResponseLoggingFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        long start = System.currentTimeMillis();
        String method = exchange.getRequest().getMethod() == null ? "" : exchange.getRequest().getMethod().name();
        String path = exchange.getRequest().getURI().getPath();

        return chain.filter(exchange)
                .doFinally(signal -> {
                    long ms = System.currentTimeMillis() - start;
                    Integer status = exchange.getResponse().getStatusCode() == null ? null : exchange.getResponse().getStatusCode().value();
                    log.info("[GATEWAY] {} {} -> {} ({}ms)", method, path, status, ms);
                });
    }

    @Override
    public int getOrder() {
        return -200;
    }
}

