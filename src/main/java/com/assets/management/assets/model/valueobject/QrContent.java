package com.assets.management.assets.model.valueobject;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import io.quarkus.hibernate.orm.panache.common.ProjectedFieldName;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class QrContent {

	@NotNull
	public final String itemSerialNumber;

	@NotNull
	public final String workId;

	@NotNull
	public final LocalDateTime dateAssigned;

	public QrContent(String itemSerialNumber, LocalDateTime dateAssigned,
			@ProjectedFieldName("employee.workId") String workId) {
		this.itemSerialNumber = itemSerialNumber;
		this.workId = workId;
		this.dateAssigned = dateAssigned;
	}
}
