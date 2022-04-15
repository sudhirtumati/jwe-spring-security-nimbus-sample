package com.sample.security.authserver.controller;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.sample.security.authserver.key.KeyManager;
import com.sample.security.authserver.token.TokenGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/authn")
public class AuthenticationController {

	private final KeyManager keyManager;

	private final TokenGenerator tokenGenerator;

	public AuthenticationController(KeyManager keyManager, TokenGenerator tokenGenerator) {
		this.keyManager = keyManager;
		this.tokenGenerator = tokenGenerator;
	}

	@GetMapping("/keys/access-encrypt")
	public String getAccessTokenEncryptionKey() {
		var jwk = keyManager.getKey("access_encrypt");
		var jwkSet = new JWKSet(jwk);
		return jwkSet.toString(false);
	}

	@PostMapping("/validate-credentials")
	public String validateCredentials(String userId, String password) throws JOSEException {
		if (!userId.equals(password)) {
			return tokenGenerator.generateAccessToken(userId);
		}
		else {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}
	}

}
