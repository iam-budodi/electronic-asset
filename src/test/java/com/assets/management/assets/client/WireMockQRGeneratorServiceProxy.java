package com.assets.management.assets.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.util.Collections;
import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class WireMockQRGeneratorServiceProxy implements QuarkusTestResourceLifecycleManager {

	private WireMockServer wireMockServer;

	@Override
	public Map<String, String> start() {
		wireMockServer = new WireMockServer();
		wireMockServer.start();

		wireMockServer.stubFor(post(urlEqualTo("generates/qrcode"))
				.willReturn(
						aResponse()
						.withHeader("Content-Type", "application/json")
						.withStatus(200)
						.withBody(
							"iVBORw0KGgoAAAANSUhEUgAAAJYAAACWAQAAAAAUekxPAAABHklEQVR4Xu2VOw7DMAxDtfnK3qRNV9ZQw"
							+ "CWVtOhnauixQhIYL4DlkLRj66tu9kn+7FcWZiNGzTmSQ5klrsK4zDnUWYxkhxo93MNAynMj6yXvYdRgBATId10u"
							+ "MhqTR735dpWxYnoMdjszpLBAeBq/eK4wfjnHVPSpqcLKwxeN8pp+rFlhyA787jVTC5mtQr6tN+Gqc80KK2ceSUfr"
							+ "oTIaA76QysRDZ/jwpALciI8eAuPkMSdfdCxVFpjdafl8yanCJjbeMT0U2MDsSBBoGHyXGcnoINnz3BAYCxD5yb5V"
							+ "xoML088+yNbDo+usjaFD0dLqDH53horNfAtDC8QbfwLbwxb7YG/TeZnhQg/zdv7sITB6hJ8ezp1kH5l91p+J7A64n"
							+ "ekulN7SZAAAAABJRU5ErkJggg=="
						)
				));
		
		return Collections.singletonMap("quarkus.rest-client.\"com.assets.management.assets.client.QRGeneratorServiceProxy\".url", wireMockServer.baseUrl());
	}

	@Override
	public void stop() {
		if (null != wireMockServer) {
			wireMockServer.stop();
		}

	}

}
