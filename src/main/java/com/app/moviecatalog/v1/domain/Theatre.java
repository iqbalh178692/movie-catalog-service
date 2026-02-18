package com.app.moviecatalog.v1.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("theatres")
@Builder
public class Theatre implements Persistable<UUID> {

    @Id
    private UUID id;

    private String name;
    private String city;

    @Override
    public boolean isNew() {
        return true;
    }
}
