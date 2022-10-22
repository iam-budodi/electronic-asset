package com.assets.management.assets.util;

import java.util.Base64;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.assets.management.assets.client.QrProxy;
import com.assets.management.assets.model.Asset;
import com.assets.management.assets.model.QrContent;

import io.quarkus.hibernate.orm.panache.PanacheQuery;

@ApplicationScoped
public class QrCodeString {

	@Inject
	@RestClient
	QrProxy qrProxy;

	public  String formatCodeImgToStr(Asset asset) {
		PanacheQuery<QrContent> query = Asset.find("id", asset.id)
		        .project(QrContent.class);

		QrContent qrContent = query.singleResult();
		byte[]    code      = qrProxy.createQrString(qrContent);
		return Base64.getEncoder().encodeToString(code);
	}
}
