package com.danis0n.service.auth;

import com.danis0n.client.AuthClient;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.danis0n.enums.Role.USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationServiceRedis extends AuthorizationService {

    private final AuthClient authClient;
    private final static String URI_USER = "http://localhost:8081/api/v1/auth/user";

    @Override
    public Optional<AuthorizationResponse> authorize(@NonNull ServerHttpRequest request) {
        return extractBearerTokenHeader(request).flatMap(this::lookup);
    }

    private Optional<AuthorizationResponse> lookup(final String token) {

        try {

            Optional<Boolean> isAuthorized =
                    authClient.sendRequest(URI_USER, token, BEARER_PREFIX);

            return isAuthorized.isPresent() && isAuthorized.get() == Boolean.TRUE ?
                    Optional.of(new AuthorizationResponse(USER.toString())) : Optional.empty();

        } catch (Exception e) {
            log.error(String.valueOf(e));
            return Optional.empty();
        }

    }

}
