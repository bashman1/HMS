# HMS Authentication Service

A production-ready Spring Boot 3.2+ microservice providing comprehensive authentication and authorization capabilities.

## Features

- **JWT-based Authentication**: Secure token-based authentication with access and refresh tokens
- **User Registration**: Email verification and account activation flow
- **Login Protection**: Rate limiting and brute force attack prevention
- **Password Reset**: Secure password recovery with time-limited tokens
- **Role-Based Access Control (RBAC)**: Flexible permission system with roles and permissions
- **Token Management**: Token rotation, invalidation, and blacklisting
- **Comprehensive Logging**: Structured logging with correlation IDs for request tracing
- **API Documentation**: OpenAPI 3 / Swagger documentation

## Technology Stack

- **Java 21**: Virtual threads, pattern matching, record patterns
- **Spring Boot 3.2.2**: Latest Spring Boot with Jakarta EE
- **Spring Security 6**: Modern SecurityFilterChain configuration
- **Spring Data JPA**: ORM with PostgreSQL
- **JWT (JJWT)**: JSON Web Token library
- **PostgreSQL**: Primary database
- **Redis**: Token blacklist cache (optional)
- **Docker**: Containerization support

## Project Structure

```
src/
├── main/
│   ├── java/com/hms/auth/
│   │   ├── application/          # Application layer (use cases, DTOs, ports)
│   │   │   ├── dto/             # Request/Response DTOs
│   │   │   ├── port/            # Input ports (use case interfaces)
│   │   │   └── service/          # Application services
│   │   ├── domain/              # Domain layer (entities, value objects)
│   │   │   ├── exception/       # Domain exceptions
│   │   │   ├── model/           # Domain models (entities)
│   │   │   └── port/            # Output ports (repository interfaces)
│   │   └── infrastructure/       # Infrastructure layer
│   │       ├── config/          # Spring configurations
│   │       ├── persistence/      # JPA repositories and adapters
│   │       ├── security/         # Security components
│   │       └── web/              # REST controllers
│   └── resources/
│       ├── application.yml       # Main configuration
│       ├── application-dev.yml   # Development profile
│       ├── application-prod.yml   # Production profile
│       └── db/migration/         # Flyway migrations
└── test/
    ├── java/                     # Unit and integration tests
    └── resources/
        └── application-test.yml   # Test configuration
```

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+
- PostgreSQL 16+
- Docker & Docker Compose (optional)

### Quick Start with Docker

```bash
# Start all services
docker-compose up -d

# Run migrations
docker-compose exec auth-service java -jar target/*.jar migrations apply

# Access API documentation
http://localhost:8080/api/v1/swagger-ui.html
```

### Local Development

```bash
# Build the application
./mvnw clean package -DskipTests

# Run tests
./mvnw test

# Run application
java -jar target/hms-auth-service-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DATABASE_URL` | PostgreSQL connection URL | jdbc:postgresql://localhost:5432/hms_auth |
| `DATABASE_USERNAME` | Database username | hms_user |
| `DATABASE_PASSWORD` | Database password | hms_password |
| `JWT_SECRET` | JWT signing secret | (required) |
| `JWT_ACCESS_TOKEN_EXPIRATION` | Access token TTL (ms) | 900000 (15 min) |
| `JWT_REFRESH_TOKEN_EXPIRATION` | Refresh token TTL (ms) | 604800000 (7 days) |

## API Endpoints

### Authentication

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/register` | Register new user | No |
| POST | `/auth/login` | Authenticate user | No |
| POST | `/auth/refresh-token` | Refresh access token | No |
| POST | `/auth/logout` | Logout current session | Yes |
| POST | `/auth/logout-all` | Logout from all devices | Yes |
| GET | `/auth/verify-email` | Verify email | No |
| POST | `/auth/resend-verification` | Resend verification email | No |
| POST | `/auth/password-reset/request` | Request password reset | No |
| POST | `/auth/password-reset/confirm` | Confirm password reset | No |
| POST | `/auth/change-password` | Change password | Yes |
| GET | `/auth/me` | Get current user | Yes |

## Security Features

- **Password Policy**: Minimum 8 characters, uppercase, lowercase, digit, special character
- **Rate Limiting**: Configurable per-endpoint rate limits
- **Brute Force Protection**: Account lockout after failed attempts
- **Token Blacklisting**: Immediate token revocation
- **CORS Configuration**: Configurable cross-origin policies
- **CSRF Protection**: Disabled for stateless API
- **Input Validation**: Jakarta Bean Validation on all inputs

## Configuration Profiles

### Development (dev)
- H2 in-memory database for testing
- Verbose logging
- Relaxed security settings
- Swagger UI enabled

### Production (prod)
- PostgreSQL database
- Restricted logging
- Full security enforcement
- Swagger disabled

## Building for Production

```bash
# Build Docker image
./mvnw spring-boot:build-image -Pprod

# Or build native image with GraalVM
./mvnw spring-boot:build-image -Pnative -Pprod
```

## Monitoring

- Health endpoint: `/actuator/health`
- Metrics: `/actuator/metrics`
- Prometheus: `/actuator/prometheus`

## License

MIT License
