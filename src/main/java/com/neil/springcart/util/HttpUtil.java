package com.neil.springcart.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

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
    public static HttpHeaders generateAuthorizationHeader(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    public static String getCurrentRequestPath() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromPath(request.getRequestURI())
                    .query(request.getQueryString());
            return builder.toUriString();
        }
        return "Unknown path";
    }
}
