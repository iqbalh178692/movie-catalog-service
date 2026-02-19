User → API Gateway
      ↓
movie-catalog-service (search)
      ↓
seat-reservation-service (lock)
      ↓
shopping-cart-service
      ↓
order-service (publish ORDER_CREATED) light orchestrated
      ↓
payment-Service
      ↓
order-Service (update status)
      ↓
notification-service




                        ┌──────────────────────────┐
                        │        API Client        │
                        │ (Web / Mobile / Admin)   │
                        └─────────────┬────────────┘
                                      │
                                      ▼
                        ┌──────────────────────────┐
                        │   movie-catalog-service  │
                        │  (WebFlux + R2DBC + H2)  │
                        └─────────────┬────────────┘
                                      │
          ┌───────────────────────────┼───────────────────────────┐
          │                           │                           │
          ▼                           ▼                           ▼
   ┌───────────────┐        ┌────────────────┐         ┌────────────────┐
   │   Movies      │        │    Screens     │         │     Shows      │
   │               │        │  (theatre_id)  │         │  (screen_id)   │
   └───────────────┘        └────────────────┘         └────────────────┘
                                  │                              │
                                  ▼                              │
                         ┌────────────────┐                      │
                         │  Theatres      │                      │
                         └────────────────┘                      │
                                                                 │
                                                                 ▼
                                                   Publishes "show.created"
                                                                 │
                                                                 ▼
                         ┌────────────────────────────────────────────┐
                         │                  Kafka                     │
                         │              Topic: show.created           │
                         └────────────────────────────────────────────┘
                                                                 │
                                                                 ▼
                        ┌─────────────────────────────────────────┐
                        │     seat-reservation-service            │
                        │   (Consumes show.created event)         │
                        └─────────────────────────────────────────┘
                                      │
                                      ▼
                          Initializes Seat Availability

