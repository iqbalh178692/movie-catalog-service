package com.app.moviecatalog.v1.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("shows")
public class Show {

    @Id
    private UUID id;

    private UUID movieId;
    private UUID theatreId;
    private LocalDateTime showTime;
    private BigDecimal price;
}
