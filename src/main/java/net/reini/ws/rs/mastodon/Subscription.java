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

/**
 * @param access_token the access token
 * @param type the subscription type
 * @param stream the stream name
 * @param list the optional list id
 * @param tag the optional hash stream tag
 */
public record Subscription(String access_token, String type, String stream, String list, String tag) {
    public static Subscription subscribeStream(AccessToken accessToken, String stream) {
        return new Subscription(accessToken.access_token(), "subscribe", stream, null, null);
    }

    public static Subscription subscribeHashtag(AccessToken accessToken, String tag) {
        return new Subscription(accessToken.access_token(), "subscribe", "hashtag", null, tag);
    }

    public static Subscription unsubscribe(AccessToken accessToken, String stream) {
        return new Subscription(accessToken.access_token(), "unsubscribe", stream, null, null);
    }
}
