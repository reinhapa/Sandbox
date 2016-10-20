/**
 * File Name: JwtLogin.java
 * 
 * Copyright (c) 2016 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.jaxrs;

import java.awt.Desktop;
import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class JwtLogin {

	/**
	 * @param args
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
