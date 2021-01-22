package io.dynamicus.config;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dynamicus.model.OpenIdConnectUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OpenIdConnectFilter extends AbstractAuthenticationProcessingFilter {

    @Value("${idp.jwk.url}")
    private String jwkUrl;

    @Value("${idp.claims.issuer}")
    private String issuer;

    @Value("${idp.claims.audience}")
    private String audience;

    private OAuth2RestOperations restOperation;

    OpenIdConnectFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
        setAuthenticationManager(new NoopAuthenticationManager());
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {

        OAuth2AccessToken accessToken;
        try {
            accessToken = restOperation.getAccessToken();
        } catch (OAuth2Exception e) {
            throw new BadCredentialsException("Could not obtain access token", e);
        }

        String idToken = accessToken.getAdditionalInformation().get("id_token").toString();
        if (StringUtils.isEmpty(idToken)) {
            throw new BadCredentialsException("Could not obtain id_token from access_token");
        }

        String kid = JwtHelper.headers(idToken).get("kid");
        if (StringUtils.isEmpty(kid)) {
            throw new BadCredentialsException("Could not obtain kid from access_token");
        }

        try {
            Jwt tokenDecoded = JwtHelper.decodeAndVerify(idToken, verifier(kid));
            Map<String, String> authInfo = new ObjectMapper().readValue(tokenDecoded.getClaims(), Map.class);

            verifyClaims(authInfo);
            logger.debug("Valid claims");

            OpenIdConnectUserDetails user = new OpenIdConnectUserDetails(authInfo, accessToken);

            List<String> roles = Collections.singletonList("ROLE_USER");

            Collection<GrantedAuthority> grantedAuthorities = getAuthorities(roles);

            return new UsernamePasswordAuthenticationToken(
                    user, null, grantedAuthorities);

        } catch (IOException e) {
            throw new BadCredentialsException("Could not deserialize claim", e);
        }
    }

    private Collection<GrantedAuthority> getAuthorities(List<String> roles) {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private RsaVerifier verifier(String kid) {
        try {
            JwkProvider provider = new UrlJwkProvider(new URL(jwkUrl));
            Jwk jwk = provider.get(kid);
            return new RsaVerifier((RSAPublicKey) jwk.getPublicKey());
        } catch (MalformedURLException | JwkException e) {
            throw new BadCredentialsException("Could not obtain user details from token", e);
        }
    }

    private void verifyClaims(Map claims) {
        int exp = (int) claims.get("exp");
        Date expireDate = new Date(exp * 1000L);
        Date now = new Date();
        boolean invalid = expireDate.before(now) ||
                !claims.get("iss").equals(issuer) ||
                !claims.get("aud").equals(audience);
        if (invalid) {
            throw new RuntimeException("Invalid claims");
        }
    }

    void setRestOperation(OAuth2RestTemplate restTemplate) {
        restOperation = restTemplate;
    }

    private static class NoopAuthenticationManager implements AuthenticationManager {
        @Override
        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            throw new UnsupportedOperationException("No authentication should be done with this AuthenticationManager");
        }

    }
}
