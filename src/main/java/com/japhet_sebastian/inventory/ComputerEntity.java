package com.japhet_sebastian.inventory;


import com.japhet_sebastian.vo.Peripheral;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity(name = "Computer")
@DiscriminatorValue("COMPUTER")
@Schema(description = "Computer representation")
public class ComputerEntity extends AssetEntity {

    @NotEmpty(message = "{Asset.processor-info.required}")
    @Schema(required = true)
    @Column(name = "computer_processor", nullable = false)
    private String processor;

    @NotNull(message = "{Asset.memory-size.required}")
    @Schema(required = true)
    @Column(name = "computer_memory", nullable = false)
    private Integer memory;

    @NotNull(message = "{Asset.storage-size.required}")
    @Schema(required = true)
    @Column(name = "computer_storage", nullable = false)
    private Integer storage;

    @Column(name = "operating_system")
    private String operatingSystem;

    @NotNull(message = "{Asset.display-size.required}")
    @Schema(required = true)
    @Column(name = "display_size", nullable = false)
    private Double displaySize;

    @Column(name = "graphics_card")
    private Integer graphicsCard;

    @NotNull(message = "{Asset.peripheral.required}")
    @Schema(required = true)
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "peripherals", nullable = false, foreignKey = @ForeignKey(name = "device_peripherals_constraint"))
    private Set<Peripheral> peripherals;

}
