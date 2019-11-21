# Build a Reactive App with Spring Boot and MongoDB

This example is a reactive resource server using Spring WebFlux and Spring Data MongoDB. It also implements group-based authorization using Okta and OAuth 2.0.

Please read [Build a Basic App with Spring Boot and MongoDB using WebFlux](https://developer.okta.com/blog/2019/02/21/reactive-with-spring-boot-mongodb) to see how this app was created.

**Prerequisites:** [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).

> [Okta](https://developer.okta.com/) has Authentication and User Management APIs that reduce development time with instant-on, scalable user infrastructure. Okta's intuitive API and expert support make it easy for developers to authenticate, manage, and secure users and roles in any application.

* [Getting Started](#getting-started)
* [Links](#links)
* [Help](#help)
* [License](#license)

## Getting Started

To install this example application, run the following commands:

```bash
git clone https://github.com/oktadeveloper/okta-spring-boot-mongo-webflux-example.git
cd okta-spring-boot-mongo-webflux-example
```

This will get a copy of the project installed locally. To install all of its dependencies and start the app, run:
 
```bash
./gradlew bootRun
```

### Create an Application in Okta

You will need to create an OpenID Connect Application in Okta to get your values to perform authentication. 

Log in to your Okta Developer account (or [sign up](https://developer.okta.com/signup/) if you don’t have an account) and navigate to **Applications** > **Add Application**. Click **SPA**, click **Next**, and give the app a name you’ll remember. The default application settings are great, except that you need to add a **Login Redirect URI**: `https://oidcdebugger.com/debug`. You can use this site to retrieve a test access token. Click **Done** and copy the `clientId` into `src/main/resources/application.yml`. 

```yaml                            
okta:
  oauth2:
    issuer: https://{yourOktaDomain}/oauth2/default
    groupsClaim: groups
    clientId: {yourClientId}
```

**NOTE:** The value of `{yourOktaDomain}` should be something like `dev-123456.oktapreview`. Make sure you don't include `-admin` in the value!

After modifying this file, restart your app and you'll have a secure API!

### Generate an Access Token

To access your API now, you need a valid access token. You can use **OpenID Connect Debugger** to help you do this. Open [oidcdebugger.com](https://oidcdebugger.com/), and fill it with the following values:

* **Authorize URI**: `https://{yourOktaDomain}/oauth2/default/v1/authorize`
* **Redirect URI**: do not change. This is the value you added to your OIDC application above.
* **Client ID**: from the OIDC application you just created.
* **Scope**: `openid profile email`.
* **State**: any value you want to pass through the OAuth redirect process. I set it to `{}`.
* **Nonce**: can be left alone. Nonce means "number used once" and is a simple security measure used to prevent the same request being used multiple times.
* **Response Type**: `token`.
* **Response mode**: `form_post`.

Click **Send Request**. If you are not logged into developer.okta.com, then you'll be required to log in. If you are (as is likely) already logged in, then the token will be generated for your signed-in identity.

### Use Your Access Token

You use the token by including in an **Authorization** request header of type **Bearer**.

Store the token in a shell variable:

```bash
TOKEN=eyJraWQiOiJldjFpay1DS3UzYjJXS3QzSVl1MlJZc3VJSzBBYUl3NkU4SDJfNVJr...
```

Then make a GET request with HTTPie:

```bash
http :8080/kayaks "Authorization: Bearer $TOKEN"
```

**Note the double quotes above.** Single quotes do not work because the shell variable is not expanded.

## Help

Please post any questions as comments on the [blog post](https://developer.okta.com/blog/2019/02/21/reactive-with-spring-boot-mongodb), or visit our [Okta Developer Forums](https://devforum.okta.com/).

## License

Apache 2.0, see [LICENSE](LICENSE).
