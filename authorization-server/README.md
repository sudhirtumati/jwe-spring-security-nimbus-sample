Verified user credentials and generate an access token (JWE).

Access token is 
 * Signed with asymmetric key
 * Encrypted with symmetric key

Once application is started, below endpoints will be available
- **POST /authn/validate-credentials** (Generate JWE after authenticating user)
- **GET /authn/keys/access-encrypt** (Download symmetric to decrypt token)
- **GET /.well-known/jwks.json** (Download public key to verify token signature)
