package com.app.moviecatalog.v1.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("movies")
public class Movie implements Persistable<UUID> {

    @Id
    private UUID id;

    private String title;
    private String language;
    private String genre;
    private Integer durationMinutes;
    private LocalDate releaseDate;
    private Boolean active;

    @Override
    public boolean isNew() {
        return true;
    }
}
