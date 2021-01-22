package io.dynamicus.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

import java.util.List;

@Configuration
@EnableOAuth2Client
public class IdentityProviderConfig {

    @Value("${idp.client.id}")
    private String clientId;

    @Value("${idp.client.secret}")
    private String clientSecret;

    @Value("${idp.access.token.uri}")
    private String accessTokenUri;

    @Value("${idp.user.authorization.uri}")
    private String userAuthorizationUri;

    @Value("${idp.redirect.uri}")
    private String redirectUri;

    @Value("#{'${idp.client.scope}'.split(',')}")
    private List<String> scopes;

    @Bean
    public OAuth2ProtectedResourceDetails identityProviderOpenId() {
        AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();

        details.setUserAuthorizationUri(userAuthorizationUri);
        details.setClientId(clientId);
        details.setClientSecret(clientSecret);
        details.setAccessTokenUri(accessTokenUri);
        details.setScope(scopes);
        details.setPreEstablishedRedirectUri(redirectUri);
        details.setUseCurrentUri(true);
        return details;
    }

    @Bean
    public OAuth2RestTemplate idpOpenIdTemplate(OAuth2ClientContext clientContext) {
        return new OAuth2RestTemplate(identityProviderOpenId(), clientContext);
    }
}
