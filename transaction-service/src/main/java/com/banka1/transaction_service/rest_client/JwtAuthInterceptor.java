package com.banka1.transaction_service.rest_client;

import com.banka1.transaction_service.security.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;


/**
 * HTTP interceptor koji automatski dodaje JWT token u Authorization header-e.
 * Koristi se za autentifikaciju pri pozivu drugih REST servisa.
 */
@Configuration
@RequiredArgsConstructor
public class JwtAuthInterceptor implements ClientHttpRequestInterceptor {

    /** Servis za generisanje JWT tokena */
    private final JWTService jwtProvider;

    /**
     * Intercept metoda koja se poziva za svaki HTTP zahtev.
     * Generiše novi JWT token i dodaje ga u Authorization header.
     *
     * @param request HTTP zahtev koji se šalje
     * @param body telo zahteva
     * @param execution executor koji nastavlja sa slanjem zahteva
     * @return HTTP odgovor sa autorima
     * @throws IOException ako dodje do greške pri komunikaciji
     */
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {

        String token = jwtProvider.generateJwtToken();
        request.getHeaders().set("Authorization", "Bearer " + token);
        return execution.execute(request, body);
    }
}
