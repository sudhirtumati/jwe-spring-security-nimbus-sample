package com.sample.security.authserver.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.sample.security.authserver.key.KeyManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OAuthController {

	private final KeyManager keyManager;

	public OAuthController(KeyManager keyManager) {
		this.keyManager = keyManager;
	}

	@GetMapping("/.well-known/jwks.json")
	public String getJwks() {
		var jwk = keyManager.getPublicKey("access_sign");
		var jwkSet = new JWKSet(jwk);
		return jwkSet.toString();
	}

}
