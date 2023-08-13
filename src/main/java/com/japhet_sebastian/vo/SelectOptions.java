package com.japhet_sebastian.vo;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.*;

import java.util.UUID;

@RegisterForReflection
public class SelectOptions {

    @Min(value = 1, message = "{Select-option.value.min}")
    @NotNull(message = "{Select-option.value.required}")
    public final UUID value;


    @NotEmpty(message = "{Select-option.label.required}")
    @Size(min = 2, max = 64, message = "{Sixty-four.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-/]+$", message = "{String.special.character}")
    public final String label;

    public SelectOptions(UUID value, String label) {
        this.value = value;
        this.label = label;
    }
}
