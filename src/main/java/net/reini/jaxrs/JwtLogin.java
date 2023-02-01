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

package net.reini.jaxrs;

import java.awt.Desktop;
import java.net.URI;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class JwtLogin {

	/**
	 * @param args program arguments
	 */
	public static void main(String[] args) {
		try {
			Form form = new Form();
			form.param("login", "user");
			form.param("password", "mysecret");
			form.param("tenant", "1000");
			form.param("plant", "B01");

			Client client = ClientBuilder.newClient();
			WebTarget userTarget = client.target("http://bisonws1300.infra.local:8080/rest/auth");

			Response response = userTarget.path("login").request(MediaType.APPLICATION_JSON_TYPE)
					.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

			if (200 == response.getStatus()) {
				String authHeader = response.getHeaderString(HttpHeaders.AUTHORIZATION);
				if (authHeader.toLowerCase().startsWith("bearer ")) {
					URI uri = userTarget.path("goto").queryParam("token", authHeader.substring("bearer ".length()))
							.queryParam("uri", "http://bisonws1300.infra.local:8080/info").getUri();
					Desktop.getDesktop().browse(uri);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
