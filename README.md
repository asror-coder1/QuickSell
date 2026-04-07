# QuickSell Engine

Spring Boot 3.3 / Java 21 backend for a multi-tenant Telegram e-commerce engine.

## Common Errors

### `Cannot find HTTP Request to execute`
This comes from IntelliJ HTTP Client run configuration, not from Spring Boot.

Cause:
- You ran `generated-requests | #27` instead of the application.

Fix:
- Run `QuickSellApplication`
- Main class: `com.quicksell.engine.QuickSellApplication`

### `Could not find or load main class asror_uz.ApiChiqarishApplication`
Cause:
- Old IntelliJ run configuration still points to the deleted class.

Fix:
- Use `QuickSellApplication`

### `FATAL: password authentication failed for user "postgres"`
Cause:
- PostgreSQL username/password in config do not match your local database.

Fix:
- Either use the `local` profile
- Or set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`

## Requirements

- Java 21+
- Maven wrapper
- PostgreSQL
- Redis

## Local Run

### Option 1: IntelliJ local profile

Run configuration:
- Main class: `com.quicksell.engine.QuickSellApplication`
- Active profile: `local`

This uses:
- DB: `jdbc:postgresql://localhost:5432/studentApi`
- User: `postgres`
- Password: `root`
- Redis: `localhost:6379`

### Option 2: Environment variables

PowerShell:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/quicksell"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your_password"
$env:REDIS_HOST="localhost"
$env:REDIS_PORT="6379"
$env:JWT_SECRET="super-secret-key-super-secret-key"
.\mvnw.cmd spring-boot:run
```

## Build And Test

Compile:

```powershell
.\mvnw.cmd compile
```

Test:

```powershell
.\mvnw.cmd test
```

Run with local profile:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

## First API Calls

Register seller:

```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "fullName": "Ali Valiyev",
  "email": "ali@example.com",
  "password": "12345678",
  "botToken": "123456:telegram-bot-token",
  "botUsername": "ali_shop_bot",
  "domainName": "ali-shop",
  "themeColor": "#1f7a8c",
  "plan": "FREE"
}
```

Login:

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "ali@example.com",
  "password": "12345678"
}
```

List products:

```http
GET /api/v1/tma/products?shop_id={shopId}
```

List categories:

```http
GET /api/v1/tma/categories?shop_id={shopId}
```

Create order:

```http
POST /api/v1/tma/orders
Content-Type: application/json

{
  "shopId": "00000000-0000-0000-0000-000000000000",
  "customerTgId": 123456789,
  "customerPhone": "+998901234567",
  "paymentType": "CASH",
  "items": [
    {
      "productId": "00000000-0000-0000-0000-000000000000",
      "quantity": 1
    }
  ]
}
```
