# InterviewGenius 🧠

> An AI-powered microservices platform for generating and managing technical interview questions

InterviewGenius is a comprehensive Spring Cloud microservices application that leverages Anthropic Claude AI to generate various types of technical interview questions, with robust storage and retrieval capabilities.

## 🚀 Features

- **AI-Powered Question Generation**: Generate MCQ, short answer, and DSA questions using Anthropic Claude AI
- **Multiple Question Types**:
  - Java MCQ questions with 4 options and answers
  - Short input-type questions (single word/sentence answers)
  - Conceptual questions requiring detailed answers
  - Data Structure & Algorithm (DSA) questions
- **Question Management**: Store, retrieve, filter, and paginate questions
- **Microservices Architecture**: Scalable, distributed system with service discovery
- **RESTful APIs**: Well-documented REST endpoints for all operations
- **MongoDB Integration**: Persistent storage with full CRUD operations
- **Service Discovery**: Automatic service registration and discovery with Eureka
- **Centralized Configuration**: Configuration management across all services
- **Health Monitoring**: Built-in health checks and actuator endpoints

## 🏗️ Architecture

InterviewGenius follows a microservices architecture with the following services:

### Core Infrastructure Services
- **Config Service** (Port 8888): Centralized configuration management
- **Discovery Service** (Port 8761): Service registry using Netflix Eureka
- **Gateway Service**: API gateway for routing and load balancing

### Business Services
- **AI Service** (Port 8083): AI-powered question generation using Anthropic Claude
- **Question Service** (Port 8082): Question storage and management with MongoDB

## 🛠️ Technology Stack

- **Backend**: Java 17, Spring Boot 3.5.6, Spring Cloud 2025.0.0
- **AI Integration**: Spring AI 1.0.2 with Anthropic Claude API
- **Database**: MongoDB for question persistence
- **Service Discovery**: Netflix Eureka
- **Build Tool**: Maven with wrapper scripts
- **Inter-service Communication**: OpenFeign
- **Monitoring**: Spring Boot Actuator
- **Code Enhancement**: Lombok for boilerplate reduction

## 📋 Prerequisites

- **Java 17** or higher
- **MongoDB** running on `localhost:27017`
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

### 3. Start MongoDB
```bash
# Using Docker
docker run -d -p 27017:27017 --name mongodb mongo:latest

# Or start your local MongoDB installation
mongod
```

### 4. Start Services (In Order)

```bash
# 1. Start Config Service (must be first)
cd config-service
./mvnw spring-boot:run

# 2. Start Discovery Service (wait for config service to be ready)
cd ../discovery-service
./mvnw spring-boot:run

# 3. Start Gateway Service
cd ../gateway-service
./mvnw spring-boot:run

# 4. Start AI Service
cd ../ai-service
./mvnw spring-boot:run

# 5. Start Question Service
cd ../question-service
./mvnw spring-boot:run
```

### 5. Verify Services
- Config Service: http://localhost:8888
- Discovery Service: http://localhost:8761
- AI Service: http://localhost:8083/api/v1
- Question Service: http://localhost:8082/api/v1

## 📚 API Documentation

### AI Service Endpoints

Base URL: `http://localhost:8083/api/v1`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/ai/question` | GET | Generate Java MCQ question with 4 options |
| `/ai/inputTypeQuestion` | GET | Generate short answer question |
| `/ai/inputTypeQuestionSec` | GET | Generate conceptual question |
| `/ai/dsa` | GET | Generate DSA question |

### Question Service Endpoints

Base URL: `http://localhost:8082/api/v1`

| Endpoint | Method | Description | Parameters |
|----------|--------|-------------|------------|
| `/questions/generate` | GET | Generate and save new question | - |
| `/questions` | GET | Get all questions | `paginated`, `page`, `size` |
| `/questions/by-language` | GET | Filter by programming language | `language`, `paginated`, `page`, `size` |
| `/questions/by-difficulty` | GET | Filter by difficulty level | `difficulty`, `paginated`, `page`, `size` |
| `/questions/filter` | GET | Filter by language and difficulty | `language`, `difficulty`, `paginated`, `page`, `size` |

### Example API Calls

```bash
# Generate a new MCQ question
curl http://localhost:8083/api/v1/ai/question

# Generate and save a question
curl http://localhost:8082/api/v1/questions/generate

# Get all questions with pagination
curl "http://localhost:8082/api/v1/questions?paginated=true&page=0&size=5"

# Filter questions by Java language
curl "http://localhost:8082/api/v1/questions/by-language?language=Java"
```

## 🧪 Testing

### Run Tests for All Services
```bash
# Test individual services
cd config-service && ./mvnw test
cd ../discovery-service && ./mvnw test
cd ../gateway-service && ./mvnw test
cd ../ai-service && ./mvnw test
cd ../question-service && ./mvnw test
```

### Using Postman Collection
Import the included `InterviewGenius-Postman-Collection.json` file into Postman for comprehensive API testing.

## 🔧 Configuration

### Environment Variables
- `ANTHROPIC_API_KEY`: Your Anthropic Claude API key (required for AI Service)
- `ANTHROPIC_MODEL`: AI model to use (default: claude-3-haiku-20240307)

### Database Configuration
Default MongoDB connection: `mongodb://localhost:27017/interviewgenius_questions`

### Service Ports
- Config Service: 8888
- Discovery Service: 8761
- Gateway Service: Not configured
- AI Service: 8083
- Question Service: 8082

## 🐳 Docker Support (Future Enhancement)

Docker configurations are planned for future releases to simplify deployment.

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

## 🙏 Acknowledgments

- Spring Boot and Spring Cloud communities
- Anthropic for Claude AI API
- MongoDB team for excellent documentation
- Netflix for Eureka service discovery

## 📞 Support

For support and questions:
- Open an issue on GitHub
- Contact: [Your contact information]

---

**Built with ❤️ using Spring Boot and Anthropic Claude AI**