package msa.gateway.common.filter;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import msa.gateway.common.jwt.JwtTokenProvider;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

/**
 * packageName : msa.gateway.common.filter
 * fileName : JwtFilter
 * author : BH
 * date : 2023-07-11
 * description :
 * ================================================
 * DATE                AUTHOR              NOTE
 * ================================================
 * 2023-07-11       JwtFilter       최초 생성
 */
@Component
@Log4j2
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {
    private final JwtTokenProvider jwtTokenProvider;
    public GlobalFilter(JwtTokenProvider jwtTokenProvider) {
        super(Config.class);
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public GatewayFilter apply(Config config) {
        // Custom Pre Filter
        return ((exchange, chain) -> {
            log.info("Global filter baseMessage : {}", config.getBaseMessage());

            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            String requestPath = request.getPath().toString();
            String requestHeader = request.getHeaders().getFirst("Authorization");

            // 예외 처리: 로그인 요청은 필터를 거치지 않고 직접 백엔드 서비스로 전달
            if ("/v1/member/login".equals(requestPath)) {
                log.info("로그인 요청은 필터 예외 처리");
                return chain.filter(exchange);
            }
            // 예외 처리: 회원가입 요청은 필터를 거치지 않고 직접 백엔드 서비스로 전달
            if ("/v1/member/signUp".equals(requestPath)) {
                log.info("회원가입 요청은 필터 예외 처리");
                return chain.filter(exchange);
            }

            // Authorization(accessToken) 검증
            if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
                String accessToken = requestHeader.substring(7);

                if (jwtTokenProvider.validateToken(accessToken)) {
                    return chain.filter(exchange);
                }
            }

            // 검증 실패하거나 Authorization 헤더가 없는 경우, 401 Unauthorized 에러를 반환합니다.
            log.info("유효하지 않은 AccessToken 또는 Authorization 헤더 없음");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        });
    }

    @Data
    public static class Config{
        // Put the configuration properties... application.yml에 작성
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }
}
