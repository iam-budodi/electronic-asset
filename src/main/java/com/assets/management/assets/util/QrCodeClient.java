package com.assets.management.assets.util;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.assets.management.assets.client.QrProxy;
import com.assets.management.assets.model.entity.ItemAssignment;
import com.assets.management.assets.model.valueobject.QrContent;

@ApplicationScoped
public class QrCodeClient {
	@Inject
	@RestClient
	QrProxy qrProxy;

	public byte[] formatQrImgToString(String serialNumber) {
		QrContent qrContent = ItemAssignment.projectQrContents(serialNumber);
//		byte[] qrCode = qrProxy.createQrString(qrContent);
//		return Base64.getEncoder().encodeToString(qrCode);
		return qrProxy.createQrString(qrContent);
	}
}
