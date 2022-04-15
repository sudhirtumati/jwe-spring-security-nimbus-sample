package com.sample.security.resourceserver.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWEDecryptionKeySelector;
import com.nimbusds.jose.proc.JWEKeySelector;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.io.IOException;
import java.text.ParseException;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	private final SecurityConfigurationProperties securityConfigurationProperties;

	SecurityConfiguration(SecurityConfigurationProperties securityConfigurationProperties) {
		this.securityConfigurationProperties = securityConfigurationProperties;
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		// @formatter:off
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .oauth2ResourceServer()
				.jwt().jwtAuthenticationConverter(jwtAuthenticationConverter());
        // @formatter:on
	}

	private JwtAuthenticationConverter jwtAuthenticationConverter() {
		// create a custom JWT converter to map the "roles" from the token as granted
		// authorities
		var jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
		jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
		var jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
		return jwtAuthenticationConverter;
	}

	@Bean
	JwtDecoder jwtDecoder() throws ParseException, IOException {
		return new NimbusJwtDecoder(jwtProcessor());
	}

	private JWTProcessor<SecurityContext> jwtProcessor() throws ParseException, IOException {
		ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
		jwtProcessor.setJWSKeySelector(jwsKeySelector());
		jwtProcessor.setJWEKeySelector(jweKeySelector());
		return jwtProcessor;
	}

	private JWSKeySelector<SecurityContext> jwsKeySelector() {
		JWKSource<SecurityContext> jwsJwkSource = new RemoteJWKSet<>(
				securityConfigurationProperties.getJws().getJwksUri());
		return new JWSVerificationKeySelector<>(securityConfigurationProperties.getJws().getAlgorithm(), jwsJwkSource);
	}

	private JWEKeySelector<SecurityContext> jweKeySelector() throws ParseException, IOException {
		JWKSource<SecurityContext> jweJwkSource = new ImmutableJWKSet<>(loadEncryptionKey());
		return new JWEDecryptionKeySelector<>(securityConfigurationProperties.getJwe().getAlgorithm(),
				securityConfigurationProperties.getJwe().getEncryptionMethod(), jweJwkSource);
	}

	private JWKSet loadEncryptionKey() throws ParseException, IOException {
		return JWKSet.load(securityConfigurationProperties.getJwe().getEncryptionKeyUri());
	}

}
