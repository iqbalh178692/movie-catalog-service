package com.app.moviecatalog.v1.eventsream.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowCreatedEvent {

    private UUID showId;
    private UUID movieId;
    private UUID screenId;
    private LocalDateTime showTime;
}
