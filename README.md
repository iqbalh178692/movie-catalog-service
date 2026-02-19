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
