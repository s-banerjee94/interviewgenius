# InterviewGenius 🧠

> An AI-powered microservices platform for comprehensive interview management with voice transcription capabilities

InterviewGenius is a comprehensive Spring Cloud microservices application that leverages Anthropic Claude AI to generate various types of technical interview questions, manage users, conduct interviews, and store user responses with advanced voice transcription features.

## 🚀 Features

- **AI-Powered Question Generation**: Generate MCQ, short answer, and DSA questions using Anthropic Claude AI
- **Voice Interview Support**: Audio transcription capabilities for voice-based interviews
- **Multiple Question Types**:
  - Java MCQ questions with 4 options and answers
  - Short input-type questions (single word/sentence answers)
  - Conceptual questions requiring detailed answers
  - Data Structure & Algorithm (DSA) questions
- **Complete Interview Management**: Full interview lifecycle from creation to completion
- **User Management**: Comprehensive user registration, authentication, and profile management
- **Answer Tracking**: Store and manage user responses to interview questions
- **Microservices Architecture**: Scalable, distributed system with service discovery
- **RESTful APIs**: Well-documented REST endpoints for all operations
- **Multi-Database Support**: MongoDB for questions, MySQL/H2 for user data
- **Service Discovery**: Automatic service registration and discovery with Eureka
- **API Gateway**: Centralized routing with Spring Cloud Gateway
- **Centralized Configuration**: Configuration management across all services
- **Health Monitoring**: Built-in health checks and actuator endpoints

## 🏗️ Architecture

InterviewGenius follows a microservices architecture with the following services:

### Core Infrastructure Services
- **Config Service** (Port 8888): Centralized configuration management using Spring Cloud Config
- **Discovery Service** (Port 8761): Service registry using Netflix Eureka
- **Gateway Service** (Port 8080): API gateway for routing and load balancing

### Business Services
- **AI Service** (Port 8083): AI-powered question generation and voice transcription using Anthropic Claude
- **Question Service** (Port 8082): Question storage and management with MongoDB
- **User Service**: User registration, authentication, and profile management
- **Interview Service**: Interview session management and coordination
- **UserAnswer Service**: Storage and retrieval of user responses to interview questions

## 🛠️ Technology Stack

- **Backend**: Java 17, Spring Boot 3.5.6, Spring Cloud 2025.0.0
- **AI Integration**: Spring AI 1.0.2 with Anthropic Claude API
- **Databases**:
  - MongoDB for question persistence
  - MySQL/H2 for user and interview data
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Build Tool**: Maven with wrapper scripts
- **Inter-service Communication**: OpenFeign
- **Monitoring**: Spring Boot Actuator
- **Code Enhancement**: Lombok for boilerplate reduction
- **Voice Processing**: Audio transcription support

## 📋 Prerequisites

- **Java 17** or higher
- **MongoDB** running on `localhost:27017`
- **MySQL** (or H2 for development) for user service
- **Anthropic API Key** for AI service
- **Maven** (or use included wrapper scripts)

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd interviewgenius
```

### 2. Set Environment Variables
```bash
export ANTHROPIC_API_KEY=your_anthropic_api_key_here
```

### 3. Start Databases
```bash
# Start MongoDB
docker run -d -p 27017:27017 --name mongodb mongo:latest

# Start MySQL (optional - H2 can be used for development)
docker run -d -p 3306:3306 --name mysql -e MYSQL_ROOT_PASSWORD=password mysql:latest
```

### 4. Start Services (In Order)

```bash
# 1. Start Config Service (must be first)
cd config-server
./mvnw spring-boot:run

# 2. Start Discovery Service (wait for config service to be ready)
cd ../discovery-service
./mvnw spring-boot:run

# 3. Start Gateway Service
cd ../gateway-service
./mvnw spring-boot:run

# 4. Start Business Services (can be started in parallel after gateway is up)
cd ../ai-service
./mvnw spring-boot:run

cd ../question-service
./mvnw spring-boot:run

cd ../user-service
./mvnw spring-boot:run

cd ../interview-service
./mvnw spring-boot:run

cd ../useranswer-service
./mvnw spring-boot:run
```

### 5. Verify Services
- Config Service: http://localhost:8888
- Discovery Service: http://localhost:8761
- Gateway Service: http://localhost:8080
- AI Service: http://localhost:8083/api/v1 (or via Gateway: http://localhost:8080/api/v1/ai)
- Question Service: http://localhost:8082/api/v1 (or via Gateway: http://localhost:8080/api/v1/questions)

## 📚 API Documentation

All services can be accessed through the Gateway at `http://localhost:8080` or directly via their individual ports.

### AI Service Endpoints

Base URL: `http://localhost:8083/api/v1` or via Gateway: `http://localhost:8080/api/v1/ai`

| Endpoint | Method | Description | Parameters |
|----------|--------|-------------|------------|
| `/ai/question` | GET | Generate Java MCQ question with 4 options | - |
| `/ai/inputTypeQuestion` | GET | Generate short answer question | - |
| `/ai/inputTypeQuestionSec` | GET | Generate conceptual question | - |
| `/ai/dsa` | GET | Generate DSA question | - |
| `/ai/transcribe` | GET | Transcribe audio file to text | `filePath` |

### Question Service Endpoints

Base URL: `http://localhost:8082/api/v1` or via Gateway: `http://localhost:8080/api/v1/questions`

| Endpoint | Method | Description | Parameters |
|----------|--------|-------------|------------|
| `/questions/generate` | GET | Generate and save new question | - |
| `/questions` | GET | Get all questions | `paginated`, `page`, `size` |
| `/questions/by-language` | GET | Filter by programming language | `language`, `paginated`, `page`, `size` |
| `/questions/by-difficulty` | GET | Filter by difficulty level | `difficulty`, `paginated`, `page`, `size` |
| `/questions/filter` | GET | Filter by language and difficulty | `language`, `difficulty`, `paginated`, `page`, `size` |

### User Service Endpoints

Base URL: via Gateway: `http://localhost:8080/api/v1/users`

| Endpoint | Method | Description | Request Body |
|----------|--------|-------------|--------------|
| `/users` | POST | Create new user | UserRequest JSON |
| `/users` | GET | Get all users | - |
| `/users/{id}` | GET | Get user by ID | - |
| `/users/{id}` | PUT | Update user | UserRequest JSON |
| `/users/{id}` | DELETE | Delete user | - |

### User Answer Service Endpoints

Base URL: via Gateway: `http://localhost:8080/api/v1/user-answers`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/user-answers` | POST | Submit user answer |
| `/user-answers` | GET | Get all user answers |
| `/user-answers/{id}` | GET | Get specific user answer |

### Interview Service Endpoints

Base URL: via Gateway: `http://localhost:8080/api/v1/interviews`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/interviews` | POST | Create new interview |
| `/interviews` | GET | Get all interviews |
| `/interviews/{id}` | GET | Get specific interview |

### Example API Calls

```bash
# Generate a new MCQ question (via Gateway)
curl http://localhost:8080/api/v1/ai/question

# Transcribe audio file
curl "http://localhost:8080/api/v1/ai/transcribe?filePath=/path/to/audio.wav"

# Create a new user
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "password": "password123",
    "role": "USER",
    "phoneNumber": "+1234567890"
  }'

# Generate and save a question
curl http://localhost:8080/api/v1/questions/generate

# Get all questions with pagination
curl "http://localhost:8080/api/v1/questions?paginated=true&page=0&size=5"

# Filter questions by Java language
curl "http://localhost:8080/api/v1/questions/by-language?language=Java"
```

### Postman Collection

A comprehensive Postman collection is available at `InterviewGenius-API.postman_collection.json` with pre-configured requests for all endpoints. Import this collection into Postman for easy API testing.

## 🔧 Configuration

### Environment Variables
- `ANTHROPIC_API_KEY`: Your Anthropic Claude API key (required for AI Service)
- `ANTHROPIC_MODEL`: AI model to use (default: claude-3-haiku-20240307)

### Database Configuration
- **MongoDB**: `mongodb://localhost:27017/interviewgenius_questions` (for Question Service)
- **MySQL/H2**: User Service, Interview Service, and UserAnswer Service databases
- Database schemas are auto-created by JPA/Hibernate

### Service Ports
- **Config Service**: 8888 (Spring Cloud Config Server)
- **Discovery Service**: 8761 (Eureka Server)
- **Gateway Service**: 8080 (Spring Cloud Gateway)
- **AI Service**: 8083 (Direct access - also available via Gateway)
- **Question Service**: 8082 (Direct access - also available via Gateway)
- **User Service**: Dynamic port (via Gateway only)
- **Interview Service**: Dynamic port (via Gateway only)
- **UserAnswer Service**: Dynamic port (via Gateway only)

### Service Communication
- **Inter-service**: Services communicate via Eureka service discovery
- **External API**: All services accessible through Gateway at port 8080
- **Direct Access**: Core services (AI, Question) also available on their direct ports

## 📊 Monitoring

All services include Spring Boot Actuator endpoints:
- Health checks: `/actuator/health`
- Metrics: `/actuator/metrics`
- Info: `/actuator/info`

Discovery Service dashboard: http://localhost:8761

## 🔒 Security Notes

⚠️ **Important Security Considerations:**

1. **API Keys**: Never commit API keys to version control
2. **Environment Variables**: Use environment variables for sensitive configuration
3. **Production Setup**: Consider using Spring Cloud Vault for secret management
4. **Database Security**: Configure MongoDB authentication for production

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Authors

- **Sandeepan** - *Initial work* - [@connectwithsandeepan](https://github.com/connectwithsandeepan)

---

**Built with ❤️ using Spring Boot and Anthropic Claude AI**