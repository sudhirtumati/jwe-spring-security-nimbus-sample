# How to run the sample

- Start [authorization-server](authorization-server)
    - Authorization server uses port `8080` by default
- Start [resource-server](resource-server)
    - Resource server uses port `8081` by default

Execute below `curl` commands to verify that resource server is properly protected

- `curl -v localhost:8081/greet/John`
  - `401` response is returned as authorization token is not provided
- `curl -d "userId=john&password=doe" -X POST localhost:8080/authn/validate-credentials`
  - A token is returned as response body
  - Copy token content and use it to replace `<token>` placeholder in next curl command
- curl -v -H 'Authorization: Bearer `<token>`' localhost:8081/greet/John
  - `200` error is returned as valid authorization token is provided