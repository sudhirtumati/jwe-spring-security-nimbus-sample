Sample resource server implementation protected with spring security.

Spring security is configured to download JWE decrypt token (symmetric) and signing token (asymmetric) during
application startup from configured endpoints.

## To run unit tests

Sample token valid until 31/Dec/2199 along with keys used to decrypt and verify signature are pre-configured.

[MockServer](https://www.mock-server.com/) is used to mock authorization server instance while running unit tests.
See [GreetingControllerSecurityTests.java](src/test/java/com/sample/security/resourceserver/controller/GreetingControllerSecurityTests.java)
for details.