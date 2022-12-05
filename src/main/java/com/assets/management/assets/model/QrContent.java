package com.assets.management.assets.model;

import java.time.LocalDateTime;

import io.quarkus.hibernate.orm.panache.common.ProjectedFieldName;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class QrContent {
	public final String itemSerialNumber;
	public final String workId;
	public final LocalDateTime dateAssigned;

	public QrContent(String itemSerialNumber, LocalDateTime dateAssigned, @ProjectedFieldName("employee.workId") String workId) {
		this.itemSerialNumber = itemSerialNumber;
		this.workId = workId;
		this.dateAssigned = dateAssigned;
	}
}
