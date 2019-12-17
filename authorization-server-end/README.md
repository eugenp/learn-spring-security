# Learn Spring Security OAuth - Module 4 - The Basics of OAuth2

## The Authorization Server

In this lesson we have three services:

* the Authorization Server
* the Resource Server
* a Client

In order to test the Authorization Server functionality, start the three services and browse the client main page:

http://localhost:8082/um-webapp-client

Then login with the following credentials:

user: john@test.com
password: 123

If you were successfully authenticated, you'll be redirected to the client listing page.

The Client sends the Access Token as a Bearer token when requesting secured resources from the Resource Server, as you can see if you set a breakpoint in the AuthorizationHeaderInterceptor class.

