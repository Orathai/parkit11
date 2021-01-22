package io.dynamicus.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dynamicus.model.OIDCUser;
import io.dynamicus.service.RestTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

@RestController
public class CustomAuthorizationController {

    @Value("${idp.userinfo.url}")
    private String userInfoUrl;

    @Value("${idp.user.authorization.uri}")
    private String userAuthorizationUri;

    @Value("${idp.client.id}")
    private String clientId;

    @Value("${idp.client.secret}")
    private String clientSecret;

    @Value("${idp.access.token.uri}")
    private String accessTokenUri;

    private final RestTemplate restTemplate = new RestTemplate();

    private RestTemplateService restTemplateService;

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthorizationController.class);

    @RequestMapping(method = RequestMethod.GET, value = "/code")
    public RedirectView getAuthorizeCode() {

        StringBuilder url = new StringBuilder();
        url.append(userAuthorizationUri)
                .append("?client_id=" + clientId)
                .append("&redirect_uri=" + "http://localhost:9000/authorize")
                .append("&response_type=code")
                .append("&scope=openid")
                .append("&state=" + "abc123");

        return new RedirectView(url.toString());
    }

    @GetMapping(value = "/authorize", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public OIDCUser authorize(HttpServletRequest request) {
        RestTemplate restTemplate = restTemplateService.createHeader(getAccessToken(request));

        OIDCUser user;
        try {
            user = restTemplate.getForObject(userInfoUrl, OIDCUser.class);
        } catch (HttpClientErrorException e) {
            logger.error("Trouble while getting user info ", e);
            throw new ResponseStatusException(e.getStatusCode());
        }
        if (user == null) {
            logger.debug("Invalid user");
            throw new RuntimeException("Invalid user");
        }
        logger.debug("user: " + user.name);
        return user;
    }

    private String getAccessToken(HttpServletRequest request){
        ResponseEntity<String> response = getStringToken(request);
        return extractAccessToken(response);
    }

    String extractAccessToken(ResponseEntity<String> response) {

        String token = "";

        if (!StringUtils.isEmpty(response)) {

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node;
            try {
                node = mapper.readTree(response.getBody());

                if (StringUtils.isEmpty(node.path("access_token").asText())) {
                    logger.warn("Check access_token entity from response");
                    return token;
                }
                token = node.path("access_token").asText();
            } catch (IOException e) {
                logger.error("Trouble extracting access_token ", e);
                throw new RuntimeException("Trouble extracting access_token");
            }

        }
        return token;
    }

    private ResponseEntity<String> getStringToken(HttpServletRequest request) {

        String code = request.getParameter("code");
        String state = request.getParameter("state");

        ResponseEntity<String> response = null;

        if (!StringUtils.isEmpty(code)) {
            URL url = restTemplateService.getStringUrl(accessTokenUri);
            URI uri = restTemplateService.toUri(url);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client_id", clientId);
            map.add("client_secret", clientSecret);
            map.add("redirect_uri", "http://localhost:9000/authorize");
            map.add("grant_type", "authorization_code");
            map.add("code", code);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);

            HttpEntity<MultiValueMap<String, String>> requestString = new HttpEntity<>(map, headers);

            try {
                response = restTemplate.postForEntity(uri, requestString, String.class);

            } catch (HttpClientErrorException e) {
                logger.error("Trouble getting access token", e);
                throw new ResponseStatusException(e.getStatusCode());
            }

        }
        return response;
    }
}
