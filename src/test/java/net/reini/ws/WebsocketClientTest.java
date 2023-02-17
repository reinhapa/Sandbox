/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2020 Patrick Reinhart
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

package net.reini.ws;


import static java.net.http.HttpClient.Redirect.NORMAL;
import static java.net.http.HttpClient.Version.HTTP_1_1;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static net.reini.ws.rs.mastodon.Subscription.subscribeHashtag;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.websocket.*;

import net.reini.ws.rs.mastodon.AccessToken;
import net.reini.ws.rs.mastodon.Event;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;


/**
 * Websocket integration test.
 *
 * @author Patrick Reinhart
 */
@Disabled
@DisabledIfEnvironmentVariable(named = "GITHUB_ACTION", matches = ".+")
@DisabledIfEnvironmentVariable(named = "TRAVIS", matches = "true")
class WebsocketClientTest {

    //  @Test
    void registerTestAppWithHttpClient() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .version(HTTP_1_1)
                .followRedirects(NORMAL)
                .connectTimeout(Duration.ofSeconds(5))
//            .proxy(ProxySelector.of(new InetSocketAddress("proxy.example.com", 80)))
//            .authenticator(Authenticator.getDefault())
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://mastodon.social/api/v1/apps"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("client_name=testclient&redirect_uris=urn:ietf:wg:oauth:2.0:oob"))
                .build();

        final HttpResponse<String> response = client.send(request, ofString());
        System.out.println(response.statusCode());
        System.out.println(response.body());
    }


    /**
     * Basic tests for Mastodon event queue...
     * https://docs.joinmastodon.org/methods/streaming/#websocket
     */
    @Test
    void testMastodonStream() throws Exception {
        final AccessToken accessToken = new AccessToken(
                "Nd3394awHLZZ_REyoRM3KUCwXkpF1tyMmmiP4Aybo5s",
                "Bearer",
                "read",
                "created_at");
        final URI path = URI.create("wss://streaming.mastodon.social/api/v1/streaming");
        try (Jsonb jsonb = JsonbBuilder.create()) {
            final OutputMessageEndPoint client = new OutputMessageEndPoint(jsonb::fromJson);
            try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(client, path)) {
                assertTrue(session.isOpen());

                final RemoteEndpoint.Basic basicRemote = session.getBasicRemote();
//                basicRemote.sendText(jsonb.toJson(subscribeStream(accessToken, "public")));
                basicRemote.sendText(jsonb.toJson(subscribeHashtag(accessToken, "java")));

                while (client.running()) {
                    TimeUnit.SECONDS.sleep(1);
                }
            }
        }
    }

    @FunctionalInterface
    interface JsonProcessor {
        <T> T fromJson(String str, Class<T> type) throws JsonbException;
    }

    static class OutputMessageEndPoint extends Endpoint {
        private final JsonProcessor jsonProcessor;
        private boolean stop;

        OutputMessageEndPoint(JsonProcessor jsonProcessor) {

            this.jsonProcessor = jsonProcessor;
        }

        boolean running() {
            return !stop;
        }

        @Override
        public void onOpen(Session session, EndpointConfig config) {
            session.addMessageHandler(String.class, text -> {
                System.out.println("onMessage: " + jsonProcessor.fromJson(text, Event.class));
            });
        }

        @Override
        public void onClose(Session session, CloseReason closeReason) {
            stop = true;
        }

        @Override
        public void onError(Session session, Throwable thr) {
            stop = true;
        }
    }
}
