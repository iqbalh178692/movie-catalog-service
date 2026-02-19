package com.app.moviecatalog.v1.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("screen_seats")
@Data
public class ScreenSeat implements Persistable<UUID> {

    @Id
    private UUID id;

    private UUID screenId;

    private String seatNumber;   // A1, A2
    private String seatType;     // REGULAR, PREMIUM
    private Integer rowNumber;

    @Override
    public boolean isNew() {
        return true;
    }
}
