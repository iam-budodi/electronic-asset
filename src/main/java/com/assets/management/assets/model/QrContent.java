package com.assets.management.assets.model;

import java.time.Instant;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class QrContent {
	public final Long    id;
	public final String  serialNumber;
	public final String  modelName;
	public final Instant stockedAt;

	public QrContent(Long id, String serialNumber, String modelName,
			Instant stockedAt) {
		this.id           = id;
		this.serialNumber = serialNumber;
		this.modelName        = modelName;
		this.stockedAt  = stockedAt;
	}
}
