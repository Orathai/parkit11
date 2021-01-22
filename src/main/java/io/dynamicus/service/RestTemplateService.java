package io.dynamicus.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;

@Service
public class RestTemplateService {
    private static final Logger logger = LoggerFactory.getLogger(RestTemplateService.class);

    public RestTemplate createHeaderForAccessToken() {

        RestTemplate template = new RestTemplate();
        template.setInterceptors(Collections.singletonList((request, body, execution) -> {
            HttpHeaders headers = request.getHeaders();
            headers.add("Authorization", "Basic " + "xx");
            headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
            return execution.execute(request, body);
        }));
        return template;

    }

    public RestTemplate createHeader(String token) {
        logger.debug("Create headers for REST template");

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList((request, body, execution) -> {
            HttpHeaders headers = request.getHeaders();
            headers.add("Authorization", "Bearer " + token);
            headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            return execution.execute(request, body);
        }));
        return restTemplate;
    }

    public HttpHeaders createHeaderForAccessToken(String clientCredential) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        headers.add("Authorization", "Basic " + clientCredential);
        return headers;
    }

    public URI toUri(URL url) {
        URI uri;
        try {
            uri = url.toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Trouble with url", e);
        }
        return uri;
    }

    public URL getStringUrl(String spec) {
        try {
            return new URL(spec);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Trouble creating URL", e);
        }

    }
}
