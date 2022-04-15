package com.sample.security.resourceserver.config;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URL;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "security.token")
public class SecurityConfigurationProperties {

	private Jws jws;

	private Jwe jwe;

	@Getter
	@Setter
	public static class Jws {

		private URL jwksUri;

		private JWSAlgorithm algorithm;

	}

	@Getter
	@Setter
	public static class Jwe {

		private URL encryptionKeyUri;

		private JWEAlgorithm algorithm;

		private EncryptionMethod encryptionMethod;

	}

}
