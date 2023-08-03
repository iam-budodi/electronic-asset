package com.assets.management.assets.model.valueobject;

import io.quarkus.runtime.annotations.RegisterForReflection;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@RegisterForReflection
public class SelectOptions {

    @Min(1)
    @NotNull
    public final Long value;

    @NotNull
    @Size(min = 2, max = 64)
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters ' and - special characters")
    public final String label;

    public SelectOptions(Long value, String label) {
        this.value = value;
        this.label = label;
    }
}
