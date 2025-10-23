# InterviewGenius 🧠

> An AI-powered microservices platform for comprehensive interview management with voice transcription, resume parsing, and OAuth2/JWT authentication

InterviewGenius is a production-ready Spring Cloud microservices application that leverages Anthropic Claude and OpenAI to generate technical interview questions, parse resumes, manage interviews with file uploads, and provide audio transcription capabilities - all secured with OAuth2 social login and JWT token authentication.

## ✨ Key Features

### 🤖 AI-Powered Capabilities
- **Question Generation**: Generate MCQ, short answer, and DSA questions using Anthropic Claude
- **Resume Parsing**: AI-driven resume parsing with structured extraction of work experience, education, and skills
- **Audio Transcription**: Voice-to-text transcription using OpenAI Whisper API
- **Interview Chat Memory**: Persistent conversation memory using Spring AI JDBC repository

### 🔐 Security & Authentication
- **OAuth2 Social Login**: Integrated authentication with Google and GitHub
- **JWT Token Management**: Secure API access with JWT token generation and validation
- **CORS Support**: Properly configured CORS for browser-based applications
- **Role-Based Access**: User roles and authorization support

### 🎯 Interview Management
- **Complete Lifecycle**: Full interview session management from start to completion
- **File Upload Support**: Audio/video file uploads with 10MB limit
- **Session Tracking**: Monitor interview progress and store user responses
- **Question Assignment**: Automated question delivery during interview sessions

### 🏗️ Enterprise Architecture
- **Microservices**: 8 services with clear separation of concerns
- **Service Discovery**: Netflix Eureka for automatic service registration
- **API Gateway**: Centralized routing with Spring Cloud Gateway
- **Multi-Database**: MongoDB for documents, MySQL for relational data
- **Docker Support**: Complete containerization with Docker Compose orchestration
- **Health Monitoring**: Spring Boot Actuator endpoints for all services

## 🏗️ Architecture

### System Overview

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         Client Applications                             │
│                    (Web App, Mobile App, Postman)                      │
└────────────────────────────────┬────────────────────────────────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │   Gateway Service        │
                    │     (Port 8080)         │
                    │                         │
                    │ • OAuth2 Login          │
                    │ • JWT Validation        │
                    │ • Request Routing       │
                    │ • CORS Handling         │
                    └───┬──────────────────┬──┘
                        │                  │
            ┌───────────▼─────┐    ┌──────▼───────────┐
            │ Discovery Service│    │  Config Server   │
            │   (Eureka 8761)  │    │   (Port 8888)    │
            └───────────┬───────┘    └──────────────────┘
                        │
        ┌───────────────┴────────────────────────┐
        │        Service Registry                 │
        │                                         │
┌───────▼─────┐  ┌──────────┐  ┌─────────┐  ┌───────────┐  ┌──────────────┐
│ AI Service  │  │ Question │  │  User   │  │ Interview │  │ UserAnswer   │
│ (Port 8083) │  │ Service  │  │ Service │  │  Service  │  │   Service    │
│             │  │ (8082)   │  │ (8081)  │  │  (8085)   │  │   (8084)     │
│ • Claude AI │  │          │  │         │  │           │  │              │
│ • OpenAI    │  │ MongoDB  │  │  MySQL  │  │  MongoDB  │  │  MongoDB     │
│ • Resume    │  │          │  │         │  │  Uploads  │  │              │
│   Parsing   │  │          │  │         │  │           │  │              │
│ • Transcribe│  │          │  │         │  │           │  │              │
│ • MySQL     │  │          │  │         │  │           │  │              │
└─────────────┘  └──────────┘  └─────────┘  └───────────┘  └──────────────┘
```

### Microservices Breakdown

#### Core Infrastructure Services

| Service | Port | Technology | Purpose |
|---------|------|------------|---------|
| **Config Server** | 8888 | Spring Cloud Config | Centralized configuration management (native file system) |
| **Discovery Service** | 8761 | Netflix Eureka | Service registry and discovery dashboard |
| **Gateway Service** | 8080 | Spring Cloud Gateway + WebFlux | API gateway with OAuth2/JWT authentication |

#### Business Services

| Service | Port | Database | Key Features |
|---------|------|----------|--------------|
| **AI Service** | 8083 | MySQL | • Anthropic Claude integration<br>• OpenAI Whisper transcription<br>• Resume parsing (work exp, education, skills)<br>• Interview chat memory<br>• Template-based prompts |
| **Question Service** | 8082 | MongoDB | • Question CRUD operations<br>• Filter by language/difficulty<br>• Pagination support<br>• AI Service integration |
| **User Service** | 8081 | MySQL | • User registration & authentication<br>• Profile management<br>• Swagger UI documentation<br>• Spring Security integration |
| **Interview Service** | 8085 | MongoDB | • Interview session management<br>• File uploads (10MB limit)<br>• Multipart form data support<br>• Session coordination |
| **UserAnswer Service** | 8084 | MongoDB Atlas | • User response tracking<br>• Answer submission<br>• Progress monitoring<br>• Auto-indexing |

## 🛠️ Technology Stack

### Core Technologies
- **Java**: 17 (LTS)
- **Spring Boot**: 3.5.6
- **Spring Cloud**: 2025.0.0
- **Spring AI**: 1.0.2
- **Build Tool**: Maven with wrapper scripts (`mvnw` / `mvnw.cmd`)

### AI & External Integrations
- **Anthropic Claude API**: Primary AI model for question generation and resume parsing
- **OpenAI API**: Audio transcription (Whisper) and specialized tasks
- **Spring AI**: Unified AI integration framework with JDBC chat memory

### Security & Authentication
- **Spring Security**: WebFlux-based reactive security
- **OAuth2 Client**: Google and GitHub integration
- **JWT**: `io.jsonwebtoken:jjwt` 0.12.6 for token management
- **CORS**: Configured for browser compatibility

### Data Storage
- **MongoDB 7.0**: Questions, Interviews, UserAnswers (document storage)
- **MySQL 8.0**: Users, AI chat memory (relational storage)
- **Spring Data JPA**: MySQL repositories with Hibernate
- **Spring Data MongoDB**: NoSQL repositories with auto-indexing

### Infrastructure & Operations
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway (reactive/WebFlux)
- **Inter-service Communication**: OpenFeign declarative REST clients
- **Monitoring**: Spring Boot Actuator (health, metrics, info)
- **Documentation**: SpringDoc OpenAPI 2.8.9 (Swagger UI)
- **Containerization**: Docker with multi-stage builds
- **Orchestration**: Docker Compose with health checks

### Development Tools
- **Lombok**: Boilerplate code reduction
- **Maven Wrapper**: Platform-independent builds

## 📋 Prerequisites

### Required
- **Java 17 or higher** ([Download](https://adoptium.net/))
- **Docker Desktop** ([Download](https://www.docker.com/products/docker-desktop/))
- **Anthropic API Key** ([Get API Key](https://console.anthropic.com/))
- **OpenAI API Key** ([Get API Key](https://platform.openai.com/api-keys))

### Optional (for OAuth2)
- **Google OAuth2 Client** ([Setup Guide](https://console.cloud.google.com/apis/credentials))
- **GitHub OAuth App** ([Setup Guide](https://github.com/settings/developers))

### For Local Development (without Docker)
- **MongoDB 7.0+** ([Download](https://www.mongodb.com/try/download/community))
- **MySQL 8.0+** ([Download](https://dev.mysql.com/downloads/))
- **Maven 3.8+** (or use included wrapper)

## 🚀 Quick Start

### Option 1: Docker Compose (Recommended - Fastest)

```bash
# 1. Clone the repository
git clone https://github.com/connectwithsandeepan/interviewgenius.git
cd interviewgenius

# 2. Create .env file from template
cp .env.example .env

# 3. Edit .env and configure your API keys
# Required: ANTHROPIC_API_KEY, OPENAI_API_KEY
# Optional: GOOGLE_CLIENT_ID, GOOGLE_CLIENT_SECRET, GITHUB_CLIENT_ID, GITHUB_CLIENT_SECRET, JWT_SECRET

# 4. Start all services (databases + 8 microservices)
docker-compose up -d

# 5. Wait for services to start (2-3 minutes)
# Watch logs to monitor startup progress
docker-compose logs -f

# 6. Verify all services are healthy
docker-compose ps

# 7. Access the application
# Gateway API: http://localhost:8080
# Eureka Dashboard: http://localhost:8761
# User Service Swagger: http://localhost:8081/api/v1/swagger-ui.html (requires port exposure)
```

### Option 2: Local Development

```bash
# 1. Start databases (using Docker)
docker run -d -p 27017:27017 --name mongodb mongo:7.0
docker run -d -p 3306:3306 --name mysql -e MYSQL_ROOT_PASSWORD=root mysql:8.0

# 2. Set required environment variables
export ANTHROPIC_API_KEY=your_anthropic_api_key_here
export OPENAI_API_KEY=your_openai_api_key_here

# Optional OAuth2 credentials
export GOOGLE_CLIENT_ID=your_google_client_id
export GOOGLE_CLIENT_SECRET=your_google_client_secret
export GITHUB_CLIENT_ID=your_github_client_id
export GITHUB_CLIENT_SECRET=your_github_client_secret

# 3. Start services in order (use mvnw.cmd on Windows)

# Config Server (optional - services run standalone)
cd config-server
./mvnw spring-boot:run &

# Discovery Service (required - must be running)
cd ../discovery-service
./mvnw spring-boot:run &

# Gateway Service (required - authentication layer)
cd ../gateway-service
./mvnw spring-boot:run &

# Wait 30 seconds for infrastructure to start, then start business services

# AI Service
cd ../ai-service
./mvnw spring-boot:run &

# Question Service
cd ../question-service
./mvnw spring-boot:run &

# User Service
cd ../user-service
./mvnw spring-boot:run &

# Interview Service
cd ../interview-service
./mvnw spring-boot:run &

# UserAnswer Service
cd ../useranswer-service
./mvnw spring-boot:run &

# 4. Verify services are registered
# Open Eureka Dashboard: http://localhost:8761
```

### Verify Installation

```bash
# Check Gateway health
curl http://localhost:8080/actuator/health

# Test AI question generation (requires JWT token - see Authentication section)
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" http://localhost:8080/api/v1/ai/question

# View all registered services
curl http://localhost:8761/eureka/apps
```

## 🔐 Authentication

### OAuth2 Social Login (Browser-based)

InterviewGenius supports Google and GitHub OAuth2 authentication:

```
1. Navigate to OAuth2 login:
   - Google: http://localhost:8080/oauth2/authorization/google
   - GitHub: http://localhost:8080/oauth2/authorization/github

2. User authenticates with provider (Google/GitHub)

3. Gateway receives callback and generates JWT token

4. User receives JWT token for API access
```

### JWT Token Authentication (API-based)

```bash
# 1. Register a new user
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "password": "securePassword123",
    "role": "USER"
  }'

# 2. Login to get JWT token
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "securePassword123"
  }'

# Response:
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "userId": 1,
  "email": "user@example.com",
  "role": "USER",
  "expiresIn": 86400000
}

# 3. Use token for authenticated API requests
export TOKEN="eyJhbGciOiJIUzUxMiJ9..."

curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/ai/question
```

### JWT Token Structure

**Standard RFC 7519 Claims:**
- `iss` (issuer): "interviewgenius-gateway"
- `aud` (audience): "interviewgenius-app"
- `sub` (subject): User email
- `jti` (JWT ID): Unique token identifier
- `iat` (issued at): Token creation timestamp
- `exp` (expiration): Token expiration (24 hours default)
- `nbf` (not before): Token validity start time

**Custom Claims:**
- `userId`: User database ID
- `email`: User email address
- `role`: User role (USER, ADMIN, etc.)
- `auth_providers`: Authentication providers (LOCAL, GOOGLE, GITHUB)
- `first_name`, `last_name`, `profile_picture`: User profile data

## 📚 API Documentation

### Base URLs
- **Gateway (Recommended)**: `http://localhost:8080/api/v1`
- **Direct Service Access**: `http://localhost:<service-port>/api/v1`

### Authentication Endpoints (Public - No JWT Required)

| Endpoint | Method | Description | Request Body |
|----------|--------|-------------|--------------|
| `/auth/signup` | POST | Register new user | `{email, firstName, lastName, password, role}` |
| `/auth/login` | POST | Login with credentials, returns JWT | `{email, password}` |
| `/auth/validate` | GET | Validate JWT token | Header: `Authorization: Bearer <token>` |
| `/oauth2/authorization/google` | GET | Initiate Google OAuth2 login | - |
| `/oauth2/authorization/github` | GET | Initiate GitHub OAuth2 login | - |

### AI Service Endpoints (Requires JWT)

Base URL: `/api/v1/ai`

| Endpoint | Method | Description | Parameters / Body |
|----------|--------|-------------|-------------------|
| `/ai/question` | GET | Generate Java MCQ with 4 options | - |
| `/ai/shortInputTypeQuestion` | GET | Generate short answer question | - |
| `/ai/descriptiveInputTypeQuestion` | GET | Generate conceptual question | - |
| `/ai/dsa` | GET | Generate DSA question | - |
| `/ai/transcribe` | GET | Transcribe audio file to text | Query: `filePath` |
| `/ai/parse-resume` | POST | Parse resume and extract structured data | `{userId: number, resumeText: string}` |
| `/ai/interview/start` | POST | Start interview with conversation memory | `{userId: number}` |
| `/ai/interview/answer` | POST | Submit answer and get next question | `{sessionId: string, answer: string}` |

### Question Service Endpoints (Requires JWT)

Base URL: `/api/v1/questions`

| Endpoint | Method | Description | Parameters |
|----------|--------|-------------|------------|
| `/questions/generate` | GET | Generate and save new question via AI | - |
| `/questions` | GET | Get all questions | `paginated=true/false`, `page=0`, `size=10` |
| `/questions/by-language` | GET | Filter by programming language | `language=Java`, `paginated`, `page`, `size` |
| `/questions/by-difficulty` | GET | Filter by difficulty | `difficulty=EASY`, `paginated`, `page`, `size` |
| `/questions/filter` | GET | Filter by language AND difficulty | `language`, `difficulty`, `paginated`, `page`, `size` |

### User Service Endpoints (Requires JWT)

Base URL: `/api/v1/users`

| Endpoint | Method | Description | Request Body |
|----------|--------|-------------|--------------|
| `/users` | POST | Create new user | `{email, firstName, lastName, password, role, phoneNumber}` |
| `/users` | GET | Get all users | - |
| `/users/{id}` | GET | Get user by ID | - |
| `/users/{id}` | PUT | Update user profile | `{email, firstName, lastName, phoneNumber}` |
| `/users/{id}` | DELETE | Delete user | - |

### Interview Service Endpoints (Requires JWT)

Base URL: `/api/v1/interviews`

| Endpoint | Method | Description | Request Body / Parameters |
|----------|--------|-------------|---------------------------|
| `/interviews/{userId}/start` | POST | Create new interview session | - |
| `/interviews/session/{sessionId}/question` | GET | Get first question for session | - |
| `/interviews/session/{sessionId}/answer` | POST | Submit answer with optional file upload | Multipart: `answer` (text), `file` (audio/video, max 10MB) |
| `/interviews/session/{sessionId}` | GET | Get interview session details | - |
| `/interviews/sessions/{userId}` | GET | Get all sessions for user | Query: `page=0`, `size=10` |
| `/interviews/session/{sessionId}/end` | PUT | End interview session | Query: `force=true/false` |

### UserAnswer Service Endpoints (Requires JWT)

Base URL: `/api/v1/user-answers`

| Endpoint | Method | Description | Request Body |
|----------|--------|-------------|--------------|
| `/user-answers` | POST | Submit user answer | `{userId, questionId, answer, interviewId}` |
| `/user-answers` | GET | Get all user answers | - |
| `/user-answers/{id}` | GET | Get specific answer by ID | - |

### Example API Usage

```bash
# Set your JWT token
export TOKEN="eyJhbGciOiJIUzUxMiJ9..."

# 1. Generate a new MCQ question
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/ai/question

# 2. Parse a resume
curl -X POST http://localhost:8080/api/v1/ai/parse-resume \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "resumeText": "John Doe\nSenior Software Engineer\n\nExperience:\nTech Corp (2020-2024) - Full Stack Developer\nBuilt microservices architecture...\n\nEducation:\nBS Computer Science, MIT (2016-2020)\n\nSkills:\nJava, Spring Boot, React, Docker, Kubernetes"
  }'

# 3. Start an interview session
curl -X POST http://localhost:8080/api/v1/interviews/1/start \
  -H "Authorization: Bearer $TOKEN"

# Response: {"sessionId": "abc123", "status": "STARTED", ...}

# 4. Submit answer with file upload
curl -X POST http://localhost:8080/api/v1/interviews/session/abc123/answer \
  -H "Authorization: Bearer $TOKEN" \
  -F "answer=My answer is that microservices provide better scalability" \
  -F "file=@interview-response.wav"

# 5. Transcribe audio file
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/v1/ai/transcribe?filePath=/path/to/audio.wav"

# 6. Get questions with pagination
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/v1/questions?paginated=true&page=0&size=10"

# 7. Filter questions by Java language and MEDIUM difficulty
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/v1/questions/filter?language=Java&difficulty=MEDIUM&paginated=true&page=0&size=5"

# 8. Get all interview sessions for a user
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/v1/interviews/sessions/1?page=0&size=20"

# 9. End interview session
curl -X PUT -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/v1/interviews/session/abc123/end?force=false"
```

### Swagger UI Documentation

Access interactive API documentation:
- **User Service**: `http://localhost:8081/api/v1/swagger-ui.html` (requires direct port access)
- **Interview Service**: OpenAPI annotations available in code

## 🔧 Configuration

### Environment Variables

Create a `.env` file in the project root:

```bash
# ==========================================
# Required - AI Service
# ==========================================
ANTHROPIC_API_KEY=your_anthropic_api_key_here
OPENAI_API_KEY=your_openai_api_key_here

# ==========================================
# Required - Database
# ==========================================
MYSQL_USERNAME=root
MYSQL_PASSWORD=your_mysql_password_here

# ==========================================
# Required - JWT Authentication
# ==========================================
JWT_SECRET=your_jwt_secret_key_at_least_256_bits_long
JWT_ISSUER=interviewgenius
JWT_AUDIENCE=interviewgenius-users

# ==========================================
# Optional - OAuth2 Social Login
# ==========================================
# Google OAuth2 (https://console.cloud.google.com/apis/credentials)
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret

# GitHub OAuth2 (https://github.com/settings/developers)
GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret
```

### OAuth2 Provider Setup

#### Google OAuth2
1. Go to [Google Cloud Console](https://console.cloud.google.com/apis/credentials)
2. Create project → "Credentials" → "Create Credentials" → "OAuth 2.0 Client ID"
3. Application type: "Web application"
4. Authorized redirect URIs: `http://localhost:8080/login/oauth2/code/google`
5. Copy Client ID and Client Secret to `.env`

#### GitHub OAuth2
1. Go to [GitHub Developer Settings](https://github.com/settings/developers)
2. "New OAuth App"
3. Authorization callback URL: `http://localhost:8080/login/oauth2/code/github`
4. Copy Client ID and Client Secret to `.env`

### Service Ports

| Service | Development | Docker | Access |
|---------|-------------|--------|--------|
| Gateway | 8080 | 8080 | ✅ Public (only exposed port) |
| Discovery (Eureka) | 8761 | - | ❌ Internal (via Gateway) |
| Config Server | 8888 | - | ❌ Internal |
| AI Service | 8083 | - | ❌ Via Gateway: `/api/v1/ai/**` |
| Question Service | 8082 | - | ❌ Via Gateway: `/api/v1/questions/**` |
| User Service | 8081 | - | ❌ Via Gateway: `/api/v1/users/**` |
| Interview Service | 8085 | - | ❌ Via Gateway: `/api/v1/interviews/**` |
| UserAnswer Service | 8084 | - | ❌ Via Gateway: `/api/v1/user-answers/**` |
| MySQL | 3306 | - | ❌ Internal |
| MongoDB | 27017 | - | ❌ Internal |

**Note**: In production, only port 8080 (Gateway) should be exposed. All other services communicate internally.

### Database Configuration

#### MongoDB

```yaml
# Local Development
spring.data.mongodb.uri: mongodb://localhost:27017/interviewgenius

# Docker
spring.data.mongodb.uri: mongodb://mongodb:27017/interviewgenius

# MongoDB Atlas (UserAnswer Service)
spring.data.mongodb.uri: mongodb+srv://username:password@cluster.mongodb.net/interviewgenius
```

**Databases Created:**
- `interviewgenius` - Questions, Interviews

#### MySQL

```yaml
# Local Development
spring.datasource.url: jdbc:mysql://localhost:3306/interviewgenius_users?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username: ${MYSQL_USERNAME:root}
spring.datasource.password: ${MYSQL_PASSWORD:root}

# Docker
spring.datasource.url: jdbc:mysql://mysql:3306/interviewgenius_users?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
```

**Databases Created:**
- `interviewgenius_users` - User Service
- `interviewgenius_ai` - AI Service (chat memory)

**Auto-Creation**: All databases are created automatically via `createDatabaseIfNotExist=true` or JPA DDL auto-update.

## 🐳 Docker Deployment

### Docker Compose Overview

The `docker-compose.yml` provides complete orchestration:

**Services:**
- 2 Databases: MySQL 8.0, MongoDB 7.0
- 3 Infrastructure: Config, Discovery, Gateway
- 5 Business Services: AI, Question, User, Interview, UserAnswer

**Features:**
- Health checks for all services
- Persistent volumes for data
- Bridge networking for service communication
- Restart policy: `unless-stopped`
- Environment variable configuration

### Docker Commands

```bash
# Start all services
docker-compose up -d

# View logs (all services)
docker-compose logs -f

# View logs (specific service)
docker-compose logs -f gateway-service

# Check service status
docker-compose ps

# Stop all services
docker-compose stop

# Stop and remove containers
docker-compose down

# Stop and remove everything (including volumes - DELETES DATA!)
docker-compose down -v

# Restart specific service
docker-compose restart ai-service

# Rebuild and restart
docker-compose up -d --build gateway-service

# Pull latest images
docker-compose pull

# Scale a service (if configured)
docker-compose up -d --scale ai-service=3

# View resource usage
docker stats
```

### Building Docker Images

Each service has a multi-stage Dockerfile:

```dockerfile
# Stage 1: Build with Maven
FROM maven:3.9-eclipse-temurin-17-alpine AS build
# Download dependencies (cached layer)
# Build JAR file

# Stage 2: Runtime with JRE only
FROM eclipse-temurin:17-jre-alpine
# Copy JAR from build stage
# Minimal image size (~200-280MB per service)
```

```bash
# Build all services
docker-compose build

# Build specific service
docker-compose build gateway-service

# Build without cache
docker-compose build --no-cache

# Build with parallel execution
docker-compose build --parallel
```

### Docker Volumes

Persistent data storage:

```yaml
volumes:
  mysql-data:              # MySQL database files
  mongodb-data:            # MongoDB database files
  interview-uploads:       # Interview audio/video files
```

## 📊 Monitoring & Health

### Spring Boot Actuator

All services expose health and metrics endpoints:

```bash
# Gateway health
curl http://localhost:8080/actuator/health

# Application info
curl http://localhost:8080/actuator/info

# Metrics
curl http://localhost:8080/actuator/metrics

# Available actuator endpoints
curl http://localhost:8080/actuator
```

### Eureka Dashboard

Monitor service registration and health:

```
http://localhost:8761
```

View:
- All registered services
- Instance status (UP/DOWN)
- Service metadata
- Health check status

### Docker Health Checks

```bash
# Check all container health
docker-compose ps

# Inspect specific container health
docker inspect --format='{{json .State.Health}}' interviewgenius-gateway

# View health logs
docker inspect interviewgenius-mysql | grep -A 10 Health
```

## 🔒 Security

### Security Features

✅ **Implemented:**
- JWT token-based authentication (RFC 7519 compliant)
- OAuth2 social login (Google, GitHub)
- CORS configuration with proper precedence
- Stateless authentication (no server-side sessions)
- Password hashing and validation
- User context propagation (`X-User-Id`, `X-User-Email`, `X-User-Role` headers)
- Custom authentication entry point (JSON errors, no redirects)
- Request/response validation

### Authentication Flow

```
User Request
    ↓
CORS Filter (HIGHEST_PRECEDENCE)
    ↓
JWT Authentication Filter
    ↓
Validate Token (signature, expiration, issuer, audience)
    ↓
Extract User Context
    ↓
Add Headers (X-User-Id, X-User-Email, X-User-Role)
    ↓
Security Check (authorize access)
    ↓
Forward to Downstream Service
```

### Production Security Checklist

⚠️ **Critical - Before Production Deployment:**

1. **Secrets Management**
   - [ ] Move all API keys to environment variables
   - [ ] Use Spring Cloud Vault or AWS Secrets Manager
   - [ ] Implement secret rotation policies
   - [ ] Never commit secrets to Git

2. **JWT Configuration**
   - [ ] Generate strong JWT secret (64+ characters)
   - [ ] Set appropriate token expiration (1-4 hours)
   - [ ] Implement refresh token mechanism
   - [ ] Add token revocation/blacklist

3. **HTTPS/TLS**
   - [ ] Enable HTTPS for all endpoints
   - [ ] Use SSL certificates (Let's Encrypt, etc.)
   - [ ] Enforce HTTPS redirects
   - [ ] Configure secure cookie attributes

4. **Database Security**
   - [ ] Enable MongoDB authentication
   - [ ] Use strong database passwords
   - [ ] Restrict network access to databases
   - [ ] Enable encryption at rest
   - [ ] Implement regular backup strategy

5. **API Security**
   - [ ] Implement rate limiting (Spring Cloud Resilience4j)
   - [ ] Add request validation and sanitization
   - [ ] Implement proper RBAC (Role-Based Access Control)
   - [ ] Add comprehensive audit logging
   - [ ] Monitor for suspicious activity

6. **OAuth2 Configuration**
   - [ ] Use separate OAuth2 apps for production/development
   - [ ] Whitelist specific redirect URIs
   - [ ] Implement PKCE for enhanced security
   - [ ] Review and minimize OAuth2 scopes

7. **Infrastructure Security**
   - [ ] Use Docker secrets instead of environment variables
   - [ ] Implement network segmentation
   - [ ] Configure firewall rules
   - [ ] Regular security updates and patching

## 🔍 Troubleshooting

### Services Not Starting

```bash
# Check if ports are in use
# Windows
netstat -ano | findstr "8080"

# Mac/Linux
lsof -i :8080

# View service logs
docker-compose logs -f gateway-service

# Restart specific service
docker-compose restart gateway-service

# Check service dependencies
docker-compose ps
```

### Database Connection Issues

```bash
# Verify databases are healthy
docker-compose ps

# Connect to MySQL
docker exec -it interviewgenius-mysql mysql -u root -p

# Connect to MongoDB
docker exec -it interviewgenius-mongodb mongosh

# Check database logs
docker-compose logs mysql
docker-compose logs mongodb
```

### JWT Token Issues

```bash
# Validate token
curl -X GET http://localhost:8080/auth/validate \
  -H "Authorization: Bearer YOUR_TOKEN"

# Check JWT configuration
docker-compose exec gateway-service env | grep JWT

# View authentication logs
docker-compose logs -f gateway-service | grep -i "jwt\|auth"
```

### Service Discovery Issues

```bash
# Check Eureka dashboard
open http://localhost:8761

# Verify service registration
curl http://localhost:8761/eureka/apps

# Check discovery service logs
docker-compose logs -f discovery-service
```

### AI Service Issues

```bash
# Verify API keys are configured
docker-compose exec ai-service env | grep API_KEY

# Test AI service directly (requires JWT)
curl -H "Authorization: Bearer TOKEN" http://localhost:8083/api/v1/ai/question

# Check AI service logs
docker-compose logs -f ai-service
```

### Common Issues & Solutions

| Issue | Possible Cause | Solution |
|-------|---------------|----------|
| Port already in use | Another service using port | Stop conflicting service or change port |
| JWT validation fails | Invalid/expired token | Refresh token or login again |
| Service not registered | Eureka not running | Start discovery-service first |
| Database connection refused | Database not started | Ensure MySQL/MongoDB are healthy |
| AI service errors | Missing API keys | Set ANTHROPIC_API_KEY and OPENAI_API_KEY |
| CORS errors | Incorrect CORS config | Verify CORS configuration in Gateway |

## 🤝 Contributing

We welcome contributions! Please follow these steps:

### Getting Started

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Make your changes with clear commit messages
4. Write or update tests for new functionality
5. Ensure all tests pass: `./mvnw test`
6. Update documentation (README, API docs, inline comments)
7. Commit your changes: `git commit -m 'Add amazing feature'`
8. Push to the branch: `git push origin feature/amazing-feature`
9. Open a Pull Request with detailed description

### Code Standards

- Follow Java naming conventions and best practices
- Use Lombok to reduce boilerplate code
- Add JavaDoc comments for public APIs
- Write unit tests (minimum 80% coverage)
- Follow existing code style and formatting
- Keep services independent and loosely coupled

### Pull Request Guidelines

- Provide clear description of changes
- Reference related issues
- Include screenshots for UI changes
- Ensure Docker build succeeds
- Update API documentation if endpoints changed
- Add/update tests for new features
- Request review from maintainers

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Sandeepan** - *Initial work* - [@connectwithsandeepan](https://github.com/connectwithsandeepan)

## 🙏 Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot) - Application framework
- [Spring Cloud](https://spring.io/projects/spring-cloud) - Microservices infrastructure
- [Spring AI](https://spring.io/projects/spring-ai) - AI integration framework
- [Anthropic Claude](https://www.anthropic.com/) - AI model for question generation and resume parsing
- [OpenAI](https://openai.com/) - Whisper API for audio transcription
- [Netflix Eureka](https://github.com/Netflix/eureka) - Service discovery
- [MongoDB](https://www.mongodb.com/) - NoSQL database
- [MySQL](https://www.mysql.com/) - Relational database

## 📞 Support

For questions, issues, or feature requests:

- **GitHub Issues**: [Create an issue](https://github.com/connectwithsandeepan/interviewgenius/issues)
- **Documentation**: Review this README and inline code documentation
- **Email**: [connectwithsandeepan@gmail.com](mailto:connectwithsandeepan@gmail.com)

## 🗺️ Roadmap

### Planned Features
- [ ] Refresh token mechanism for JWT
- [ ] Role-based access control (RBAC) with fine-grained permissions
- [ ] Rate limiting and circuit breakers (Resilience4j)
- [ ] Comprehensive audit logging for security events
- [ ] Real-time notifications (WebSocket) for interview events
- [ ] Advanced analytics dashboard for interview metrics
- [ ] Multi-language support (i18n)
- [ ] Kubernetes deployment manifests (Helm charts)
- [ ] GraphQL API alongside REST
- [ ] Interview recording playback
- [ ] Mobile app API support
- [ ] Multi-tenancy support for organizations

### In Progress
- ✅ Resume parsing with AI-driven extraction
- ✅ OAuth2 social login (Google, GitHub)
- ✅ JWT token authentication
- ✅ Docker Compose orchestration

### Completed
- ✅ Microservices architecture (8 services)
- ✅ Service discovery with Eureka
- ✅ API Gateway with Spring Cloud Gateway
- ✅ AI-powered question generation (Anthropic Claude)
- ✅ Audio transcription (OpenAI Whisper)
- ✅ Interview session management
- ✅ File upload support (10MB limit)
- ✅ User management with authentication
- ✅ MongoDB and MySQL integration
- ✅ Health monitoring with Actuator
- ✅ Docker support with multi-stage builds
- ✅ Swagger UI documentation

---

**Built with ❤️ using Spring Boot, Spring Cloud, and Spring AI**

*InterviewGenius - Transforming technical interviews with AI-powered intelligence*
