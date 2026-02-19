package com.app.moviecatalog.v1.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("screens")
@Data
public class Screen implements Persistable<UUID> {

    @Id
    private UUID id;

    private UUID theatreId;
    private String name;       // Screen 1, IMAX
    private Integer totalSeats;

    @Override
    public boolean isNew() {
        return true;
    }
}
