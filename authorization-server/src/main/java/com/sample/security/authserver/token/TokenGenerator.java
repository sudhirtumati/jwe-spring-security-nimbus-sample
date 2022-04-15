package com.sample.security.authserver.token;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.AESEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sample.security.authserver.key.KeyManager;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class TokenGenerator {

	private final KeyManager keyManager;

	public TokenGenerator(KeyManager keyManager) {
		this.keyManager = keyManager;
	}

	public String generateAccessToken(String userId) throws JOSEException {
		Date now = new Date();
		// Create JWT
		var jwtClaimsSet = new JWTClaimsSet.Builder().issuer("https://my-auth-server").subject(userId)
				.expirationTime(new Date(now.getTime() + (1000 * 60 * 10))).notBeforeTime(now).issueTime(now)
				.jwtID(UUID.randomUUID().toString()).claim("roles", "EMPLOYEE MANAGER").build();
		// Sign the token
		var signedJWT = sign(jwtClaimsSet);
		// Encrypt the token
		return encrypt(signedJWT);
	}

	private SignedJWT sign(JWTClaimsSet jwtClaimsSet) throws JOSEException {
		var jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("access_sign").type(JOSEObjectType.JWT)
				.contentType("JWT").build();
		var jwsSigner = new RSASSASigner((RSAKey) keyManager.getKey("access_sign"));
		var signedJWT = new SignedJWT(jwsHeader, jwtClaimsSet);
		signedJWT.sign(jwsSigner);
		return signedJWT;
	}

	private String encrypt(SignedJWT signedJWT) throws JOSEException {
		var jweHeader = new JWEHeader.Builder(JWEAlgorithm.A256KW, EncryptionMethod.A256GCM).type(JOSEObjectType.JWT)
				.contentType("JWT").build();
		var jweObject = new JWEObject(jweHeader, new Payload(signedJWT));
		var jweEncrypter = new AESEncrypter((OctetSequenceKey) keyManager.getKey("access_encrypt"));
		jweObject.encrypt(jweEncrypter);
		return jweObject.serialize();
	}

}
