package com.app.moviecatalog.v1.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("shows")
@Builder
public class Show implements Persistable<UUID> {

    @Id
    private UUID id;

    private UUID movieId;
    private UUID theatreId;
    private LocalDateTime showTime;
    private BigDecimal price;

    @Override
    public boolean isNew() {
        return true;
    }
}
