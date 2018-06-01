package io.github.yoshikawaa.app.githubmanager;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.util.StringUtils;
import org.thymeleaf.dialect.IDialect;

import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.options.MutableDataSet;

import io.github.yoshikawaa.app.githubmanager.core.thymeleaf.MyDialect;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableWebSecurity
@EnableOAuth2Sso
@Slf4j
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ClientHttpRequestInterceptor requestInterceptor() {

        return (request, body, execution) -> {
            log.info("Req For -> {}:{}", request.getMethod().name(), request.getURI().toString());
            if (body != null && body.length > 0) {
                log.info("Req Body -> {}", new String(body));
            }
            return execution.execute(request, body);
        };
    }

    @Bean
    public ClientHttpRequestFactory requestFactory(@Value("${proxy.host:}") String host,
            @Value("${proxy.port:}") Integer port, @Value("${proxy.user:}") String user,
            @Value("${proxy.password:}") String password) {

        HttpClientBuilder builder = HttpClientBuilder.create();

        if (StringUtils.hasText(host) && Objects.nonNull(port)) {
            builder.setProxy(new HttpHost(host, port));

            if (StringUtils.hasText(user) && StringUtils.hasText(password)) {
                BasicCredentialsProvider provider = new BasicCredentialsProvider();
                provider.setCredentials(new AuthScope(host, port), new UsernamePasswordCredentials(user, password));
                builder.setDefaultCredentialsProvider(provider);
            }
        }

        return new HttpComponentsClientHttpRequestFactory(builder.build());
    }

    @Bean
    @ConditionalOnBean(ClientHttpRequestFactory.class)
    public AccessTokenProvider accessTokenProvider(ClientHttpRequestFactory requestFactory) {

        AuthorizationCodeAccessTokenProvider authorizationCodeAccessTokenProvider = new AuthorizationCodeAccessTokenProvider();
        authorizationCodeAccessTokenProvider.setRequestFactory(requestFactory);
        ImplicitAccessTokenProvider implicitAccessTokenProvider = new ImplicitAccessTokenProvider();
        implicitAccessTokenProvider.setRequestFactory(requestFactory);
        ResourceOwnerPasswordAccessTokenProvider resourceOwnerPasswordAccessTokenProvider = new ResourceOwnerPasswordAccessTokenProvider();
        resourceOwnerPasswordAccessTokenProvider.setRequestFactory(requestFactory);
        ClientCredentialsAccessTokenProvider clientCredentialsAccessTokenProvider = new ClientCredentialsAccessTokenProvider();
        clientCredentialsAccessTokenProvider.setRequestFactory(requestFactory);
        return new AccessTokenProviderChain(
                Arrays.asList(authorizationCodeAccessTokenProvider, implicitAccessTokenProvider,
                        resourceOwnerPasswordAccessTokenProvider, clientCredentialsAccessTokenProvider));
    }

    @Bean
    @ConditionalOnBean({ AccessTokenProvider.class, ClientHttpRequestFactory.class })
    public UserInfoRestTemplateCustomizer userInfoRestTemplateCustomizer(AccessTokenProvider accessTokenProvider,
            ClientHttpRequestFactory requestFactory, ClientHttpRequestInterceptor requestInterceptor) {

        return new UserInfoRestTemplateCustomizer() {
            @Override
            public void customize(OAuth2RestTemplate restTemplate) {
                restTemplate.setAccessTokenProvider(accessTokenProvider);
                restTemplate.setRequestFactory(requestFactory);
                restTemplate.setInterceptors(Collections.singletonList(requestInterceptor));
            }
        };
    }

    @Bean
    public OAuth2RestTemplate oauth2RestTemplate(OAuth2ClientContext oauth2ClientContext,
            OAuth2ProtectedResourceDetails details, ObjectProvider<AccessTokenProvider> accessTokenProvider,
            ObjectProvider<ClientHttpRequestFactory> requestFactory,
            ObjectProvider<ClientHttpRequestInterceptor> requestInterceptor) {

        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(details, oauth2ClientContext);
        accessTokenProvider.ifAvailable(restTemplate::setAccessTokenProvider);
        requestFactory.ifAvailable(restTemplate::setRequestFactory);
        requestInterceptor.ifAvailable(i -> restTemplate.setInterceptors(Collections.singletonList(i)));
        return restTemplate;
    }

    @Bean
    public IDialect myDialect() {
        MutableDataSet options = new MutableDataSet();
        options.setFrom(ParserEmulationProfile.GITHUB);
        return new MyDialect(options);
    }

}
