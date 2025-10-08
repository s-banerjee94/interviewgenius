# Interview Service - Improvement Recommendations

## Progress Summary

### ✅ Completed
- **Exception & Error Handling** (Section 2)
  - Created 6 custom exception classes
  - Implemented global exception handler (`GlobalExceptionHandler`)
  - Created structured error response DTO
  - Updated all service and controller methods
  - Removed unused `SttClient` class
- **Input Validation** (Section 3)
  - Added Bean Validation with @NotBlank constraints
  - Implemented file type validation for audio uploads
  - Added validation exception handlers
  - Updated API documentation with validation examples

### ⚠️ In Progress / TODO
- Configuration & Infrastructure improvements
- Performance optimizations

### ❌ Skipped (Not Required)
- Security (Authentication/Authorization)
- Unit & Integration Tests
- API Documentation (OpenAPI/Swagger)
- Notification Service
- Rate Limiting

---

## 1. Configuration & Infrastructure

### Enable Spring Cloud Config
- Currently disabled in `application.yml`
- Should integrate with config-server for centralized configuration
- Move environment-specific properties to config-server

### Enable WebSocket
- Dependency is commented out in `pom.xml` (lines 42-45)
- Needed for real-time interview features
- Enables bi-directional communication for live interviews

### Add Circuit Breaker (Resilience4j)
- Protect against failures in AiClient Feign calls
- Add fallback mechanisms
- Prevent cascading failures

**Dependencies to Add:**
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
</dependency>
```

---

## 2. Exception & Error Handling ✅ **COMPLETED**

### Custom Exception Classes ✅
Created domain-specific exceptions to replace generic `RuntimeException`:
- ✅ `SessionNotFoundException` - Created
- ✅ `InvalidSessionStateException` - Created
- ✅ `SessionAlreadyExistsException` - Created
- ✅ `AnswerAlreadySubmittedException` - Created
- ✅ `FileUploadException` - Created
- ✅ `SessionTimeNotCompletedException` - Created

### Global Exception Handler ✅
- ✅ Created `GlobalExceptionHandler` with `@RestControllerAdvice`
- ✅ Standardized error responses across all endpoints
- ✅ Proper HTTP status codes (404, 400, 409, 500, 413)
- ✅ Handles all custom exceptions + `MaxUploadSizeExceededException`
- ✅ Generic `Exception` handler for unexpected errors
- ✅ Includes logging for all exceptions

### Error Response DTOs ✅
- ✅ Created `ErrorResponse` DTO with:
  - `status` (HTTP status code)
  - `error` (Error type/category)
  - `message` (Detailed error message)
  - `path` (Request path)
  - `timestamp` (ISO formatted timestamp)

### Service & Controller Updates ✅
- ✅ `InterviewService` - All methods now throw custom exceptions
- ✅ `FileUploadService` - Throws `FileUploadException` with proper validation
- ✅ `InterviewController` - Removed try-catch blocks, exceptions handled globally
- ✅ Removed unused `SttClient` class and references

---

## 3. Security ❌ **SKIPPED - Not Required**

### Authentication/Authorization - SKIPPED
- Not implementing security layer at this time
- To be considered in future if needed

### Input Validation ✅ **COMPLETED**
- ✅ Added `@Validated` annotation on controller class
- ✅ Added validation constraints on controller parameters (@NotBlank)
- ✅ Added validation exception handlers (ConstraintViolationException, MethodArgumentNotValidException)
- ⚠️ Validate sessionId format (UUID/ObjectId validation) - **TODO** (optional enhancement)

### File Upload Security ✅ **COMPLETED**
- ✅ Validate file types (only allow audio formats: mp3, wav, m4a, webm, ogg, aac, flac, wma)
- ✅ Maximum file size validation (10MB, enforced by Spring Boot)
- ✅ Validate file extensions and content types
- ⚠️ Sanitize file names - **TODO** (optional enhancement)
- ⚠️ Prevent path traversal attacks - **TODO** (optional enhancement)

**Dependency Added:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

---

## 4. API Improvements

### OpenAPI/Swagger Documentation ❌ **SKIPPED - Not Required**
- Not implementing API documentation at this time
- To be considered in future if needed

### Pagination
- Add pagination for session history queries
- GET `/interviews/users/{userId}` - list all sessions with pagination
- Use Spring Data's `Pageable` and `Page`

### Search/Filter Endpoints
- Query sessions by date range
- Filter by status (ACTIVE, COMPLETED)
- Search by user

### Request/Response DTOs
- Separate DTOs for requests vs responses
- Create `StartSessionRequest`, `StartSessionResponse`
- Create `SubmitAnswerRequest`, `SubmitAnswerResponse`
- Don't expose entity classes directly

---

## 5. Data & Performance

### Database Transactions
- Add `@Transactional` annotations for data consistency
- Ensure atomicity in multi-step operations
- Handle rollback scenarios

### Audit Fields
- Add to `InterviewSession` entity:
  - `createdAt` (auto-populated)
  - `updatedAt` (auto-updated)
  - `createdBy`
  - `lastModifiedBy`
- Use Spring Data MongoDB auditing

### Caching
- Cache session data with Redis or Caffeine
- Cache active sessions to reduce DB queries
- Cache user interview history
- Set appropriate TTL

### Async Processing
- Make file upload asynchronous
- Make transcription asynchronous (currently blocking)
- Use `CompletableFuture` or `@Async`
- Return status endpoint for tracking progress

**Dependencies:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

---

## 6. Observability

### Structured Logging
- Add SLF4J with JSON formatting
- Log important business events
- Add correlation IDs for request tracking
- Log levels: INFO for business events, DEBUG for technical details

### Custom Metrics
- Track interview duration (time between start and end)
- Count questions asked per session
- Track completion rates
- Monitor file upload sizes and durations
- Track transcription success/failure rates

### Distributed Tracing
- Add Spring Cloud Sleuth or Micrometer Tracing
- Trace requests across microservices
- Integrate with Zipkin or Jaeger

### Expand Actuator Endpoints
- Currently only exposes: health, info, metrics
- Add: prometheus, env, loggers, httptrace
- Create custom health indicators

**Dependencies:**
```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-brave</artifactId>
</dependency>
<dependency>
    <groupId>io.zipkin.reporter2</groupId>
    <artifactId>zipkin-reporter-brave</artifactId>
</dependency>
```

---

## 7. Business Logic Enhancements

### Session Timeout Handling
- Auto-expire abandoned sessions
- Scheduled job to mark ACTIVE sessions as COMPLETED after timeout
- Configurable timeout period

### Resume Session
- Allow users to resume interrupted interviews
- Store last question asked
- Handle network disconnections gracefully

### Question Pool Management
- Track which questions were asked in a session
- Avoid asking duplicate questions
- Question difficulty progression
- Category-based question selection

### Scoring/Evaluation
- Add answer evaluation logic
- Score calculation based on answer quality
- Store scores in `QuestionAnswer` entity
- Overall session score

### Interview Templates
- Different interview types (technical, behavioral, system design)
- Configurable question count
- Configurable time limits
- Different evaluation criteria

### Multi-language Support
- Support for different programming languages in technical interviews
- Support for different spoken languages in voice interviews

---

## 8. Testing ❌ **SKIPPED - Not Required**

### Unit Tests - SKIPPED
- Not implementing unit tests at this time
- Current test file exists but is empty: `InterviewServiceApplicationTests.java`

### Integration Tests - SKIPPED
- Not implementing integration tests at this time

### Contract Tests - SKIPPED
- Not implementing contract tests at this time

### TestContainers - SKIPPED
- Not implementing TestContainers at this time

---

## 9. File Management

### Cloud Storage Integration
- Move from local file storage to cloud (S3, Azure Blob, Google Cloud Storage)
- Better scalability and reliability
- Easier to handle in distributed environments
- Generate pre-signed URLs for secure access

### File Cleanup
- Scheduled jobs to clean up old audio files
- Retention policy (e.g., delete after 90 days)
- Archive important recordings
- Reduce storage costs

### Streaming
- Stream large audio files instead of loading in memory
- Use Spring's `StreamingResponseBody`
- Better memory management
- Faster response times

**Dependencies for AWS S3:**
```xml
<dependency>
    <groupId>io.awspring.cloud</groupId>
    <artifactId>spring-cloud-aws-starter-s3</artifactId>
</dependency>
```

---

## 10. Missing Features to Consider

### Interview Feedback
- Store interviewer/system feedback
- AI-generated feedback on answers
- Store in `QuestionAnswer` or separate `Feedback` entity

### Analytics API
- Interview performance analytics
- User progress tracking over time
- Comparative analytics (vs other candidates)
- Aggregated statistics

### Notification Service ❌ **SKIPPED - Not Required**
- Not implementing notification service at this time

### Rate Limiting ❌ **SKIPPED - Not Required**
- Not implementing rate limiting at this time

### Interview Scheduling ❌ **SKIPPED - Not Required**
- Not implementing interview scheduling at this time
- Status.SCHEDULED has been removed from the entity

### Interview Recording
- Full session recording (not just per-question audio)
- Store recording URL in `InterviewSession.recordingUrl` (field exists but not used)
- Playback functionality

### Collaborative Interviews
- Multiple interviewers
- Real-time collaboration features
- Shared feedback and notes

### Interview Reports
- Generate PDF reports of interview sessions
- Include questions, answers, scores, feedback
- Export to different formats

---

## Implementation Priority

### High Priority (Critical for Production)
1. ✅ ~~Exception & Error Handling~~ **COMPLETED**
2. ❌ ~~Security (Authentication/Authorization)~~ **SKIPPED**
3. ✅ ~~Input Validation~~ **COMPLETED**
4. ❌ ~~Unit & Integration Tests~~ **SKIPPED**
5. Structured Logging
6. Enable Spring Cloud Config

### Medium Priority (Important for Scalability)
1. Database Transactions
2. Async Processing
3. Caching
4. Cloud Storage Integration
5. Circuit Breaker
6. ❌ ~~OpenAPI Documentation~~ **SKIPPED**

### Low Priority (Nice to Have)
1. Analytics API
2. Interview Templates
3. ❌ ~~Notification Service~~ **SKIPPED**
4. ❌ ~~Rate Limiting~~ **SKIPPED**
5. Advanced Features (Resume Session, Multi-language, etc.)

---

## Current Issues Status

### In `InterviewService.java`
- ✅ **Line 29-32**: Fixed - Uncommented duplicate session check, now throws `SessionAlreadyExistsException`
- ✅ **Line 109**: Fixed - Added proper exception handling with `SessionNotFoundException`
- ⚠️ **Line 127**: Remaining time calculation could be more user-friendly (show minutes and seconds) - **TODO**
- ⚠️ No transaction management for multi-step operations - **TODO**
- ✅ **Removed unused `SttClient`** - Deleted class and all references

### In `InterviewController.java`
- ✅ **Line 38-40**: Fixed - Exceptions now handled globally by `GlobalExceptionHandler`
- ✅ **Line 48-63**: Fixed - Removed try-catch blocks, all exceptions handled by `@RestControllerAdvice`
- ✅ **Line 59**: Fixed - Now returns structured `ErrorResponse` DTO
- ⚠️ No request/response DTOs, exposing entities directly - **TODO**

### In `FileUploadService.java`
- ✅ Fixed - Now throws `FileUploadException` instead of propagating `IOException`
- ✅ Added validation for empty files and null file names
- ✅ Added file type validation for audio formats (mp3, wav, m4a, webm, ogg, aac, flac, wma)

### In `application.yml`
- ⚠️ **Line 8**: Config server is disabled - needs to be enabled - **TODO**
- ⚠️ **Line 16**: MongoDB URI should come from config server - **TODO**
- ⚠️ **Line 36**: Limited actuator endpoints exposed - **TODO**

### In `pom.xml`
- ⚠️ **Line 42-49**: WebSocket and Config dependencies commented out - **TODO**
- ✅ **Fixed**: Added validation dependency (`spring-boot-starter-validation`)

---

## Database Schema Improvements

### InterviewSession Collection
```javascript
{
  "_id": ObjectId,
  "userId": String (indexed),
  "startTime": ISODate (indexed),
  "endTime": ISODate,
  "status": String (indexed), // ACTIVE, COMPLETED
  "questionAnswers": [
    {
      "questionId": String,  // ADD: reference to question pool
      "question": String,
      "answer": String,
      "questionTimestamp": ISODate,
      "answerTimestamp": ISODate,
      "audioFileUrl": String,  // ADD: URL to audio file
      "transcriptionConfidence": Number,  // ADD: confidence score
      "score": Number,  // ADD: answer score
      "feedback": String  // ADD: AI feedback
    }
  ],
  "recordingUrl": String,  // Full session recording
  "overallScore": Number,  // ADD: total score
  "interviewType": String,  // ADD: technical, behavioral, etc.
  "durationMinutes": Number,  // ADD: actual duration
  "createdAt": ISODate,  // ADD: audit field
  "updatedAt": ISODate,  // ADD: audit field
  "createdBy": String,  // ADD: audit field
  "lastModifiedBy": String  // ADD: audit field
}
```

### Indexes ❌ **SKIPPED - Not Required**
Additional indexes not needed at this time. Using existing indexes from entity annotations:
- `userId_status_idx` - Via `@CompoundIndex` annotation on `InterviewSession` entity
- `userId_idx` - Via `@Indexed` annotation on `userId` field
- `startTime_idx` - Via `@Indexed` annotation on `startTime` field
- `status_idx` - Via `@Indexed` annotation on `status` field

---

## Configuration Recommendations

### application.yml Improvements
```yaml
spring:
  application:
    name: interview-service
  config:
    import: configserver:http://localhost:8888  # Enable config server
  cloud:
    config:
      enabled: true  # Enable
      fail-fast: true  # Fail fast in production
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
      enabled: true
  data:
    mongodb:
      uri: ${MONGODB_URI:mongodb://localhost:27017/interviewgenius}  # Use env variable
  cache:
    type: redis  # Add caching
    redis:
      time-to-live: 3600000  # 1 hour

server:
  port: 8085
  servlet:
    context-path: /api/v1
  error:
    include-message: always
    include-binding-errors: always

eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_URL:http://localhost:8761/eureka/}
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true
    lease-renewal-interval-in-seconds: 30
    lease-expiration-duration-in-seconds: 90

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,env,loggers  # Expand
  endpoint:
    health:
      show-details: always
  metrics:
    export:
      prometheus:
        enabled: true

# Custom application properties
interview:
  session:
    default-duration-minutes: 30
    max-questions: 10
    auto-expire-enabled: true
    auto-expire-after-hours: 2
  file:
    upload-dir: ${FILE_UPLOAD_DIR:./uploads}
    allowed-extensions: mp3,wav,m4a,webm
    max-size-mb: 10
  storage:
    type: local  # local, s3, azure
    s3:
      bucket: ${S3_BUCKET:interview-recordings}
      region: ${AWS_REGION:us-east-1}
```

---

## API Endpoint Improvements

### Suggested New Endpoints

```
POST   /api/v1/interviews/start/{userId}           # Exists
GET    /api/v1/interviews/{sessionId}/question     # Exists
POST   /api/v1/interviews/{sessionId}/answer       # Exists
POST   /api/v1/interviews/{sessionId}/end          # Exists
GET    /api/v1/interviews/{sessionId}/details      # Exists

# New endpoints to add:
GET    /api/v1/interviews/users/{userId}                    # List all sessions for user (paginated)
GET    /api/v1/interviews/users/{userId}/active             # Get active session
DELETE /api/v1/interviews/{sessionId}                       # Delete session (soft delete)
PATCH  /api/v1/interviews/{sessionId}/resume                # Resume interrupted session
GET    /api/v1/interviews/{sessionId}/report                # Generate session report
POST   /api/v1/interviews/{sessionId}/feedback              # Submit feedback
GET    /api/v1/interviews/{sessionId}/analytics             # Session analytics
POST   /api/v1/interviews/{sessionId}/questions/{qId}/skip  # Skip question
GET    /api/v1/interviews/{sessionId}/progress              # Get session progress
PATCH  /api/v1/interviews/{sessionId}/extend                # Extend session time
```

---

## Notes

- This document provides a comprehensive list of improvements
- Prioritize based on business needs and timeline
- Implement incrementally to avoid breaking changes
- Add feature flags for gradual rollout
- Update this document as improvements are implemented

---

## Change Log

### 2025-10-03 - Exception & Error Handling Implementation
**Completed:**
- Created 6 custom exception classes in `exception` package:
  - `SessionNotFoundException`
  - `InvalidSessionStateException`
  - `SessionAlreadyExistsException`
  - `AnswerAlreadySubmittedException`
  - `FileUploadException`
  - `SessionTimeNotCompletedException`
- Created `GlobalExceptionHandler` with `@RestControllerAdvice`
  - Handles all custom exceptions with proper HTTP status codes
  - Handles `MaxUploadSizeExceededException` (413 Payload Too Large)
  - Generic exception handler for unexpected errors
  - Includes structured logging with SLF4J
- Created `ErrorResponse` DTO for standardized error responses
- Updated `InterviewService`:
  - All methods now throw appropriate custom exceptions
  - Fixed commented-out duplicate session check
  - All `orElseThrow()` calls now use custom exceptions with context
- Updated `FileUploadService`:
  - Throws `FileUploadException` instead of `IOException`
  - Added validation for empty files and null file names
- Updated `InterviewController`:
  - Removed all try-catch blocks
  - Exceptions now handled globally
- **Cleanup:** Removed unused `SttClient` class and all references

**Files Created:**
- `exception/SessionNotFoundException.java`
- `exception/InvalidSessionStateException.java`
- `exception/SessionAlreadyExistsException.java`
- `exception/AnswerAlreadySubmittedException.java`
- `exception/FileUploadException.java`
- `exception/SessionTimeNotCompletedException.java`
- `exception/GlobalExceptionHandler.java`
- `dto/ErrorResponse.java`

**Files Modified:**
- `service/InterviewService.java`
- `service/FileUploadService.java`
- `controller/InterviewController.java`

**Files Deleted:**
- `client/SttClient.java`

### 2025-10-03 - Audio File URL Storage
**Completed:**
- Added `audioFileUrl` field to `QuestionAnswer` entity to store the audio file path for each answer
- Updated `QuestionAnswerDto` to include `audioFileUrl` field
- Modified `InterviewService.submitAnswer()` to store audio file path when answer is submitted
- Updated `InterviewService.getSessionDetails()` mapping to include `audioFileUrl`
- Updated API documentation to reflect new field in responses

**Files Modified:**
- `entity/QuestionAnswer.java` - Added `audioFileUrl` field
- `dto/QuestionAnswerDto.java` - Added `audioFileUrl` field
- `service/InterviewService.java` - Store and map `audioFileUrl`
- `API_DOCUMENTATION.md` - Updated QuestionAnswer model and examples

**Benefits:**
- Frontend can now access the audio file URL for playback/download
- Complete audit trail of audio recordings per question
- Enables audio file management and cleanup

### 2025-10-03 - DTO Cleanup & Simplification
**Completed:**
- Simplified `UploadResponseDto` by removing unused fields (`question`, `answer`)
- DTO now only contains relevant fields for file upload response: `userId`, `sessionId`, `uploadFileLocation`
- Updated `FileUploadService` to use simplified constructor
- Follows Single Responsibility Principle - DTO focused on file upload concerns only

**Files Modified:**
- `dto/UploadResponseDto.java` - Removed unused question and answer fields
- `service/FileUploadService.java` - Updated constructor call to match simplified DTO

**Benefits:**
- Cleaner, more maintainable code
- Follows DTO best practices (single purpose)
- Reduces confusion about DTO purpose

### 2025-10-03 - Question-Answer Synchronization Implementation
**Completed:**
- Added `questionIndex` field to `QuestionAnswer` entity to track question order (1, 2, 3, ...)
- Added `questionIndex` field to `QuestionAnswerDto` for API responses
- Implemented validation in `getNextQuestion()`:
  - Prevents getting next question if current question is not answered
  - Throws `InvalidSessionStateException` with clear message
  - Automatically assigns sequential question index
- Enhanced `submitAnswer()` with better comments explaining synchronization logic
- Updated `getSessionDetails()` to include `questionIndex` in response mapping
- Updated API documentation with:
  - New `questionIndex` field in QuestionAnswer model
  - New error response for unanswered question validation
  - Notes section explaining question-answer synchronization rules

**Files Modified:**
- `entity/QuestionAnswer.java` - Added `questionIndex` field
- `dto/QuestionAnswerDto.java` - Added `questionIndex` field
- `service/InterviewService.java` - Added validation and index assignment logic
- `API_DOCUMENTATION.md` - Updated models, examples, and documentation

**Benefits:**
- **Perfect 1:1 sync** between questions and answers
- **Prevents duplicate questions** - can't get new question until current is answered
- **Sequential tracking** - easy to identify which question (Q1, Q2, Q3...)
- **Better UX** - frontend can display "Question 3 of 10" progress
- **Data integrity** - prevents orphaned questions without answers
- **Clear error messages** - frontend knows exactly what went wrong

### 2025-10-03 - Enhanced Answer Submission Response
**Completed:**
- Created `AnswerSubmissionResponse` DTO to provide rich response data when submitting answers
- Updated `InterviewService.submitAnswer()` to return DTO instead of plain String
- Updated `InterviewController.submitAnswer()` to return structured JSON response
- Response now includes:
  - `questionIndex` - Which question was answered (1, 2, 3...)
  - `transcription` - The transcribed answer text
  - `totalQuestionsAnswered` - How many questions answered so far (progress tracking)
  - `sessionStatus` - Current session status (ACTIVE/COMPLETED)
- Updated API documentation:
  - Added `AnswerSubmissionResponse` model to data models
  - Updated Submit Answer endpoint with new response format
  - Updated example code to handle JSON response
  - Added notes about progress tracking

**Files Created:**
- `dto/AnswerSubmissionResponse.java`

**Files Modified:**
- `service/InterviewService.java` - Changed return type and added progress counting logic
- `controller/InterviewController.java` - Changed return type to DTO
- `API_DOCUMENTATION.md` - Updated response format, examples, and notes

**Benefits:**
- **Better frontend UX** - Can show "Question 3 of 5 answered" progress
- **Rich response** - Frontend gets all relevant info in one call
- **No need for extra API calls** - Progress tracking included in response
- **Confirmation feedback** - Frontend knows exactly which question was answered
- **Session awareness** - Frontend knows if session is still active or completed
- **Structured data** - JSON response instead of plain text (easier to parse)

### 2025-10-03 - Input Validation Implementation
**Completed:**
- Added `spring-boot-starter-validation` dependency to `pom.xml`
- Added `@Validated` annotation to `InterviewController` class to enable method-level validation
- Added `@NotBlank` validation constraints to all controller parameters:
  - `start()` - `userId` parameter validated
  - `getNextQuestion()` - `sessionId` parameter validated
  - `end()` - `sessionId` parameter validated
  - `getSessionDetails()` - `sessionId` parameter validated
  - `submitAnswer()` - `sessionId`, `userId`, `userName` parameters validated
- Implemented file type validation in `FileUploadService`:
  - Added allowed audio file extensions list: mp3, wav, m4a, webm, ogg, aac, flac, wma
  - Added allowed content types list for validation
  - Created `validateAudioFile()` method to check file extension
  - Created `getFileExtension()` helper method
  - Throws `FileUploadException` with clear message for invalid file types
- Enhanced `GlobalExceptionHandler` with validation exception handlers:
  - `ConstraintViolationException` - handles @NotBlank and other constraint violations (400 BAD REQUEST)
  - `MethodArgumentNotValidException` - handles @Valid annotation on request bodies (400 BAD REQUEST)
  - Collects all validation error messages and returns in structured format
- Updated API documentation:
  - Added validation error examples for all endpoints
  - Updated File Upload Constraints section with specific allowed formats
  - Added Input Validation notes for frontend developers
  - Updated HTTP status codes section

**Files Modified:**
- `pom.xml` - Added validation dependency
- `controller/InterviewController.java` - Added @Validated and @NotBlank annotations
- `service/FileUploadService.java` - Added file type validation logic
- `exception/GlobalExceptionHandler.java` - Added validation exception handlers
- `API_DOCUMENTATION.md` - Added validation error examples and notes

**Benefits:**
- **Input sanitization** - Prevents empty/null values from reaching business logic
- **Better error messages** - Clear validation errors returned to frontend (e.g., "User ID cannot be empty")
- **File type security** - Only audio files accepted, prevents uploading malicious files
- **Early validation** - Fails fast at controller level before hitting service layer
- **Consistent error format** - Validation errors follow same ErrorResponseDto structure
- **Frontend-friendly** - Validation messages can be displayed directly to users
- **Reduced DB queries** - Invalid requests rejected before database access
- **API contract enforcement** - Ensures all required parameters are provided

### 2025-10-03 - Naming Convention Improvements
**Completed:**
- Renamed `InputTypeQuestion` → `QuestionDto` for consistency with other DTOs
- Renamed `ErrorResponse` → `ErrorResponseDto` for consistency
- Renamed `AnswerSubmissionResponse` → `AnswerSubmissionResponseDto` for consistency
- Renamed `SessionDetailsDto.id` → `sessionId` for clarity
- Updated all imports and references across:
  - `AiClient.java` - Updated return type and import
  - `InterviewService.java` - Updated method signatures and imports
  - `InterviewController.java` - Updated return types and imports
  - `GlobalExceptionHandler.java` - Updated all exception handler return types
- Deleted old DTO files after migration

**Files Renamed:**
- `dto/InputTypeQuestion.java` → `dto/QuestionDto.java`
- `dto/ErrorResponse.java` → `dto/ErrorResponseDto.java`
- `dto/AnswerSubmissionResponse.java` → `dto/AnswerSubmissionResponseDto.java`

**Files Modified:**
- `client/AiClient.java` - Updated return type
- `service/InterviewService.java` - Updated method signatures and field mapping
- `controller/InterviewController.java` - Updated return types
- `exception/GlobalExceptionHandler.java` - Updated all exception handlers
- `dto/SessionDetailsDto.java` - Renamed id field to sessionId
- `API_DOCUMENTATION.md` - Updated all DTO names, added DTO naming conventions section, added API change log

**Benefits:**
- **Consistent DTO naming** - All DTOs now have "Dto" suffix
- **Clearer field names** - `sessionId` instead of generic `id` in SessionDetailsDto
- **Better code readability** - `QuestionDto` is more clear than `InputTypeQuestion`
- **Standard Java conventions** - Follows common DTO naming patterns
- **Easier maintenance** - Consistent naming makes code easier to navigate
- **Self-documenting code** - DTO suffix clearly indicates data transfer objects