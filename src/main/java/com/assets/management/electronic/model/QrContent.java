package com.assets.management.electronic.model;

import java.time.Instant;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class QrContent {
	public final Long    id;
	public final String  serialNumber;
	public final String  brand;
	public final Instant generatedAt;

	public QrContent(Long id, String serialNumber, String brand,
			Instant generatedAt) {
		this.id           = id;
		this.serialNumber = serialNumber;
		this.brand        = brand;
		this.generatedAt  = generatedAt;
	}

	@Override
	public String toString() {
		return "QrContent [id=" + id + ", serialNumber=" + serialNumber
				+ ", brand=" + brand + ", generatedAt=" + generatedAt + "]";
	} 
}
