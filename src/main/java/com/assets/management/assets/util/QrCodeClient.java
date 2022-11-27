package com.assets.management.assets.util;

import java.util.Base64;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.assets.management.assets.client.QrProxy;
import com.assets.management.assets.model.Item;
import com.assets.management.assets.model.QrContent;

import io.quarkus.hibernate.orm.panache.PanacheQuery;

@ApplicationScoped
public class QrCodeClient {

	@Inject
	@RestClient
	QrProxy qrProxy;

	public String formatQrImgToString(Item item) {
		// Query projection
		PanacheQuery<QrContent> query = Item
				.find("id", item.id)
				.project(QrContent.class);

		QrContent qrContent = query.singleResult();
		byte[] code = qrProxy.createQrString(qrContent);
		return Base64.getEncoder().encodeToString(code);
	}
}
