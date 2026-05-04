# AliyevGrocery

AliyevGrocery is a Spring Boot REST API for a grocery ordering flow. Users can register, manage their profile and address, add products to their cart, place an order, and cancel orders while they are still pending. Admins and couriers can view orders and update order statuses.

There is no payment integration in this project. Orders are managed only through statuses.

## Tech Stack

- Java 21
- Spring Boot 4.0.5
- Spring Web MVC
- Spring Security
- JWT authentication
- Spring Data JPA
- PostgreSQL
- Gradle
- Docker Compose
- Lombok

## Main Features

- User registration and login with JWT access and refresh tokens
- Profile update endpoints for username, email, and phone number
- Address create, read, update, and admin delete
- User cart and order flow through `user_products`
- Automatic line total calculation with `unitPrice * quantity`
- Order status management without a payment system
- Role based access control for `USER`, `ADMIN`, and `COURIER`

## Order Flow

The `user_products` table is used for cart and order items.

1. User adds a product.
2. Product is saved with status `CART`.
3. User places an order.
4. Cart items become `PENDING`.
5. User can cancel only while status is `PENDING`.
6. Only `ADMIN` and `COURIER` can update order status after checkout.

Available statuses:

```text
CART
PENDING
PREPARING
ON_THE_WAY
DELIVERED
CANCELLED
```

## Requirements

- Java 21
- Docker
- Docker Compose

## Run Locally

Start PostgreSQL:

```bash
docker compose up -d
```

Run the application:

```bash
./gradlew bootRun
```

The API will run on:

```text
http://localhost:9095
```

Database settings are defined in `src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:8081/aliyev_grocery
    username: postgres
    password: postgres
server:
  port: 9095
```

## Build and Test

Compile:

```bash
./gradlew compileJava
```

Run tests:

```bash
./gradlew test
```

PostgreSQL must be running before application context tests can pass.

## Authentication

Register or login returns an access token and refresh token.

Use the access token in protected requests:

```http
Authorization: Bearer <access_token>
```

## API Endpoints

### Auth

| Method | Endpoint | Access | Description |
| --- | --- | --- | --- |
| POST | `/api/auth/register` | Public | Register a new user |
| POST | `/api/auth/login` | Public | Login with username, email, or phone number |
| POST | `/api/auth/refresh` | Public | Generate new tokens from refresh token |

Register request:

```json
{
  "username": "user123",
  "password": "Passw0rd1",
  "email": "user@example.com",
  "number": "+994501234567"
}
```

Login request:

```json
{
  "identifier": "user123",
  "password": "Passw0rd1"
}
```

### Users

| Method | Endpoint | Access | Description |
| --- | --- | --- | --- |
| GET | `/api/users` | ADMIN | Get all users |
| GET | `/api/users/{id}` | ADMIN | Get user by id |
| PATCH | `/api/users/me/username` | Authenticated | Update current username |
| PATCH | `/api/users/me/email` | Authenticated | Update current email |
| PATCH | `/api/users/me/number` | Authenticated | Update current phone number |
| DELETE | `/api/users/{id}` | ADMIN | Delete user |

### Addresses

| Method | Endpoint | Access | Description |
| --- | --- | --- | --- |
| GET | `/api/addresses/me` | Authenticated | Get current user's address |
| POST | `/api/addresses` | Authenticated | Create current user's address |
| PUT | `/api/addresses/me` | Authenticated | Update current user's address |
| DELETE | `/api/addresses/{id}` | ADMIN | Delete address |

Address request:

```json
{
  "city": "Baku",
  "street": "Nizami",
  "building": "10",
  "apartment": "5",
  "note": "Call before delivery"
}
```

### User Products and Orders

| Method | Endpoint | Access | Description |
| --- | --- | --- | --- |
| GET | `/api/user-products/me` | Authenticated | Get current user's cart and order items |
| GET | `/api/user-products/me/cart` | Authenticated | Get current user's cart with total price |
| POST | `/api/user-products` | Authenticated | Add product to cart |
| PATCH | `/api/user-products/{id}/quantity` | Authenticated | Update cart item quantity |
| POST | `/api/user-products/order` | Authenticated | Place order from cart |
| PATCH | `/api/user-products/{id}/cancel` | Authenticated | Cancel own pending order |
| GET | `/api/user-products` | ADMIN, COURIER | Get all user product records |
| GET | `/api/user-products/status/{status}` | ADMIN, COURIER | Get records by status |
| PATCH | `/api/user-products/{id}/status` | ADMIN, COURIER | Update order status |

Add product request:

```json
{
  "productId": 1,
  "quantity": 2
}
```

Update quantity request:

```json
{
  "quantity": 3
}
```

Update status request:

```json
{
  "status": "PREPARING"
}
```

## Important Notes

- Product and category REST controllers are not currently exposed.
- Product data must exist before using `/api/user-products`.
- User role is `USER` by default after registration.
- To test admin or courier endpoints locally, update the user's role in the database.
- If you already had an older local database before the new order statuses were added, update the `user_products_status_check` constraint or recreate the database volume.

Example role update:

```bash
docker exec aliyev-grocery-postgres psql -U postgres -d aliyev_grocery \
  -c "UPDATE users SET role = 'ADMIN' WHERE username = 'adminuser';"
```

Example status constraint fix for old local databases:

```bash
docker exec aliyev-grocery-postgres psql -U postgres -d aliyev_grocery -c "
ALTER TABLE user_products DROP CONSTRAINT IF EXISTS user_products_status_check;
ALTER TABLE user_products ADD CONSTRAINT user_products_status_check
CHECK (status IN ('CART', 'PENDING', 'PREPARING', 'ON_THE_WAY', 'DELIVERED', 'CANCELLED'));
"
```

## Project Structure

```text
src/main/java/com/example/aliyevgrocery
├── config
├── controller
├── Enums
├── exception
├── mapper
├── model
│   ├── entity
│   ├── request
│   └── response
├── repository
├── security
└── service
```

## License

This project is for learning and portfolio purposes.
