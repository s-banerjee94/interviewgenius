# PostgreSQL Docker Setup Guide

## Quick Setup with Docker Compose

### 1. Create docker-compose.yml

Create a `docker-compose.yml` file in your project root:

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
    container_name: postgres-interviewgenius
    environment:
      POSTGRES_DB: interviewgenius_users
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: password123
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  pgadmin:
    image: dpage/pgadmin4
    container_name: pgadmin-interviewgenius
    environment:
      PGADMIN_DEFAULT_EMAIL: admin@example.com
      PGADMIN_DEFAULT_PASSWORD: admin123
    ports:
      - "8080:80"
    depends_on:
      - postgres

volumes:
  postgres_data:
```

### 2. Start Services

```bash
# Start PostgreSQL and pgAdmin
docker-compose up -d

# Check if containers are running
docker ps

# View logs
docker-compose logs
```

### 3. Access pgAdmin

1. Open browser: http://localhost:8080
2. Login with:
   - Email: admin@example.com
   - Password: admin123

### 4. Connect to PostgreSQL in pgAdmin

1. Right-click "Servers" → "Register" → "Server"
2. General tab:
   - Name: InterviewGenius
3. Connection tab:
   - Host: postgres
   - Port: 5432
   - Database: interviewgenius_users
   - Username: admin
   - Password: password123

## Alternative: Individual Docker Commands

### PostgreSQL Only

```bash
# Run PostgreSQL container
docker run -d \
  --name postgres-interviewgenius \
  -e POSTGRES_DB=interviewgenius_users \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=password123 \
  -p 5432:5432 \
  -v postgres_data:/var/lib/postgresql/data \
  postgres:15
```

### pgAdmin Only

```bash
# Run pgAdmin container
docker run -d \
  --name pgadmin-interviewgenius \
  -e PGADMIN_DEFAULT_EMAIL=admin@example.com \
  -e PGADMIN_DEFAULT_PASSWORD=admin123 \
  -p 8080:80 \
  dpage/pgadmin4
```

## Spring Boot Configuration

Add to your `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/interviewgenius_users
    username: admin
    password: password123
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    database-platform: org.hibernate.dialect.PostgreSQLDialect
```

## Useful Commands

```bash
# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# View PostgreSQL logs
docker logs postgres-interviewgenius

# Connect to PostgreSQL via command line
docker exec -it postgres-interviewgenius psql -U admin -d interviewgenius_users
```