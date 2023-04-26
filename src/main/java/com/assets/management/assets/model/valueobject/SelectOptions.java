package com.assets.management.assets.model.valueobject;

import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

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
