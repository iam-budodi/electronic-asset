package com.japhet_sebastian.vo;

import io.quarkus.hibernate.orm.panache.common.ProjectedFieldName;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@RegisterForReflection
public class QrContent {

    @NotNull
    private final String itemSerialNumber;

    @NotNull
    private final String workId;

    @NotNull
    private final LocalDateTime dateAssigned;

    public QrContent(
            String itemSerialNumber, LocalDateTime dateAssigned, @ProjectedFieldName("employee.workId") String workId) {
        this.itemSerialNumber = itemSerialNumber;
        this.workId = workId;
        this.dateAssigned = dateAssigned;
    }
}
