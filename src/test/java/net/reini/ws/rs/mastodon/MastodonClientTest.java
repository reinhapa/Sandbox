/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Patrick Reinhart
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.reini.ws.rs.mastodon;

import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class MastodonClientTest {
    static WebTarget baseTarget;
    static ClientApplication clientApplication;
    static String authorization_code;

    @BeforeAll
    static void initBaseTarget() {
        Client client = ClientBuilder.newClient();
        baseTarget = client.target("https://mastodon.social");
        clientApplication = new ClientApplication(
                "1900140",
                "testclient",
                null,
                "urn:ietf:wg:oauth:2.0:oob",
                "ZAvhNTCV09gxlBFxuNU-Ecc5NmUyeTzAkN1KWK9MADE",
                "fmE0Hpmi0zJhKIwH4dJUzaQXCjzH8sr_v2Sbl3yBG28",
                "BCk-QqERU0q-CfYZjcuB6lnyyOYfJ2AifKqfeGIm7Z-HiTU5T9eTG5GxVA0_OH5mMlI4UkkDTpaZwozy0TzdZ2M="
        );
        authorization_code = "6iKr26Es7NDHyUAPyydA7KLUPh9bmeuBnFUgCflQWNg";
    }

    @Test
    @Disabled
    void registerMastodonAppWithJakarta() {
        Response res = baseTarget
                .path("/api/v1/apps")
                .request()
                .post(Entity.form(new Form()
                        .param("client_name", "testclient")
                        .param("redirect_uris", "urn:ietf:wg:oauth:2.0:oob")
                        .param("scope", "read+write+follow+push")));

        if (res.getStatus() == 200) {
            ClientApplication clientApplication = res.readEntity(ClientApplication.class);
            System.out.println(clientApplication);
        } else {
            ErrorState errorState = res.readEntity(ErrorState.class);
            System.out.println(errorState);
        }
    }

    @Test
    @Disabled
    void testAuthorize() {
        Invocation.Builder request = baseTarget.path("/oauth/authorize")
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", "urn:ietf:wg:oauth:2.0:oob")
                .queryParam("client_id", clientApplication.client_id())
                .request();
    }

    @Test
    @Disabled
    void testOauthToken() {
        Response res = baseTarget.path("/oauth/token")
                .request()
                .post(Entity.form(new Form()
                        .param("grant_type", "authorization_code")
                        .param("code", authorization_code)
                        .param("client_id", clientApplication.client_id())
                        .param("client_secret", clientApplication.client_secret())
                        .param("redirect_uri", "urn:ietf:wg:oauth:2.0:oob")
                        .param("scope", "read%20write%20follow%20push")
                ));
        if (res.getStatus() == 200) {
            AccessToken accessToken = res.readEntity(AccessToken.class);
            System.out.println(accessToken);
        } else {
            ErrorState errorState = res.readEntity(ErrorState.class);
            System.out.println(errorState);
        }
    }

    @Test
    @Disabled
    void verifyCredentials() {
        AccessToken accessToken = new AccessToken(
                "Nd3394awHLZZ_REyoRM3KUCwXkpF1tyMmmiP4Aybo5s",
                "Bearer",
                "read",
                "created_at");

        Response res = baseTarget.path("/api/v1/accounts/verify_credentials")
                .request()
                .header("Authorization", accessToken.authorization())
                .get();

        if (res.getStatus() == 200) {
            System.out.println(res.getStatusInfo());
        } else {
            ErrorState errorState = res.readEntity(ErrorState.class);
            System.out.println(errorState);
        }
    }
}
