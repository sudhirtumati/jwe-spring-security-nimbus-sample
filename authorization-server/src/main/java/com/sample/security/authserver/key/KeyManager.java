package com.sample.security.authserver.key;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.gen.OctetSequenceKeyGenerator;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class KeyManager {

	private final Map<String, JWK> keyMap = new HashMap<>();

	@PostConstruct
	void initialize() throws JOSEException {
		generateKeyPair("access_sign", KeyUse.SIGNATURE);
		generateSymmetricKey("access_encrypt", KeyUse.ENCRYPTION);
	}

	private void generateKeyPair(String keyId, KeyUse keyUse) throws JOSEException {
		var keyPair = new RSAKeyGenerator(2048).keyID(keyId).keyUse(keyUse).generate();
		keyMap.put(keyId, keyPair);
		log.info("Generated RSA key with id '{}' for '{}'", keyId, keyUse.toString());
	}

	private void generateSymmetricKey(String keyId, KeyUse keyUse) throws JOSEException {
		var key = new OctetSequenceKeyGenerator(256).keyID(keyId).keyUse(keyUse).algorithm(JWEAlgorithm.A256KW)
				.generate();
		keyMap.put(keyId, key);
		log.info("Generated AES key with id '{}' for '{}'", keyId, keyUse.toString());
	}

	public JWK getPublicKey(String id) {
		return keyMap.get(id).toPublicJWK();
	}

	public JWK getKey(String id) {
		return keyMap.get(id);
	}

}
