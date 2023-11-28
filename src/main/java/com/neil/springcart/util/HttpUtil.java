package com.neil.springcart.util;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

/**
 * Contains utility methods related to HTTP requests. Should only be used in
 * controllers.
 */
@Component
public class HttpUtil {

    /**
     * Creates an HTTP headers object with an Authorization header set with the
     * Bearer authentication scheme.
     * @param token The JWT token to be in the Authorization header.
     * @return An HTTP headers object with the Authorization header.
     */
    public HttpHeaders generateAuthorizationHeader(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }
}
