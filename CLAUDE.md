# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Architecture

This is a Spring Cloud microservices architecture project called "InterviewGenius" built with Java 17 and Spring Boot 3.5.6. The project follows a microservices pattern with six main services:

### Core Services

1. **Config Server** (port 8888)
   - Spring Cloud Config Server using native file system configuration
   - Manages centralized configuration for all services
   - Configurations stored in `config-server/config/` directory
   - Package: `in.connectwithsandeepan.interviewgenius.configserver`

2. **Discovery Service** (port 8761)
   - Netflix Eureka Server for service discovery and registration
   - Configured as standalone server (not registering with itself)
   - Package: `in.connectwithsandeepan.interviewgenius.discoveryservice`

3. **Gateway Service**
   - Spring Boot web application for routing
   - Acts as entry point for client requests
   - Package: `in.connectwithsandeepan.interviewgenius.gatewayservice`

### Business Services

4. **AI Service** (port 8083)
   - Integrates with Anthropic Claude AI API using Spring AI
   - Uses Spring AI Anthropic starter for AI functionality
   - Context path: `/api/v1`
   - Package: `in.connectwithsandeepan.interviewgenius.aiservice`
   - Configuration includes Anthropic API key and model settings

5. **Question Service** (port 8082)
   - Manages interview questions using MongoDB
   - Uses Spring Data MongoDB for persistence
   - Context path: `/api/v1`
   - Package: `in.connectwithsandeepan.interviewgenius.questionservice`
   - Includes OpenFeign for inter-service communication

6. **User Service**
   - Manages user-related functionality
   - Package: `in.connectwithsandeepan.interviewgenius.userservice`

### Package Structure
All services follow the package naming convention: `in.connectwithsandeepan.interviewgenius.<servicename>`

## Development Commands

### Building and Running Services

Each service is a separate Maven project with its own `pom.xml`. Use these commands from the root directory:

```bash
# Build all services
cd config-server && ./mvnw clean install
cd ../discovery-service && ./mvnw clean install
cd ../gateway-service && ./mvnw clean install
cd ../ai-service && ./mvnw clean install
cd ../question-service && ./mvnw clean install
cd ../user-service && ./mvnw clean install

# Run individual services (start in this order for proper dependency resolution)
cd config-server && ./mvnw spring-boot:run     # Start first (port 8888)
cd ../discovery-service && ./mvnw spring-boot:run  # Start second (port 8761)
cd ../gateway-service && ./mvnw spring-boot:run    # Start third
cd ../ai-service && ./mvnw spring-boot:run         # Start fourth (port 8083)
cd ../question-service && ./mvnw spring-boot:run   # Start fifth (port 8082)
cd ../user-service && ./mvnw spring-boot:run       # Start sixth

# Run tests for individual services
cd <service-name> && ./mvnw test

# Clean build artifacts
cd <service-name> && ./mvnw clean
```

### Service Startup Order
1. **Config Service** must start first (provides configuration)
2. **Discovery Service** should start second (service registry)
3. **Gateway Service** should start third (routing)
4. **AI Service** and **Question Service** can start after discovery service is up

## Configuration Management

- All service configurations are managed centrally through the Config Service
- Configuration files are stored in `config-service/config/` directory
- Services retrieve their configuration from the Config Service at startup
- The Config Service uses the `native` profile to read from local filesystem instead of Git

### External Dependencies
- **MongoDB**: Required for Question Service (default: `mongodb://localhost:27017/interviewgenius_questions`)
- **Anthropic API**: Required for AI Service (API key must be provided via environment variable or configuration)

## Service Communication

- Services register themselves with the Discovery Service (Eureka)
- Inter-service communication happens through service names registered in Eureka
- All services include Spring Boot Actuator for health checks and monitoring endpoints

## Technology Stack

- **Java**: 17
- **Spring Boot**: 3.5.6
- **Spring Cloud**: 2025.0.0
- **Spring AI**: 1.0.2 (for AI Service)
- **Build Tool**: Maven with wrapper scripts
- **Service Discovery**: Netflix Eureka
- **Configuration**: Spring Cloud Config
- **Database**: MongoDB (for Question Service)
- **AI Integration**: Anthropic Claude API via Spring AI
- **Inter-service Communication**: OpenFeign
- **Monitoring**: Spring Boot Actuator
- **Code Enhancement**: Lombok for reducing boilerplate code

## Important Notes

### Security Considerations
⚠️ **WARNING**: The AI Service configuration file contains hardcoded API keys. In production environments:
- Use environment variables for sensitive configuration
- Never commit API keys to version control
- Consider using Spring Cloud Vault or similar secret management solutions

### Platform Compatibility
- Maven wrapper scripts are provided for both Unix/Linux (`mvnw`) and Windows (`mvnw.cmd`)
- Use the appropriate wrapper script for your platform