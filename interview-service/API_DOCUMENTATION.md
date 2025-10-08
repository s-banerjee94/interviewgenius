# Interview Service - API Documentation

**Last Updated:** 2025-10-06

**Note:** All DTO (Data Transfer Object) names follow consistent naming conventions with "Dto" suffix for clarity.

**Important:** After getting the first question, all subsequent questions are provided in the `nextQuestion` field of the answer submission response. Do NOT call GET `/question` repeatedly.

---

## Endpoints

### 1. Start Interview Session

**Endpoint:** `POST /interviews/start/{userId}`

**Description:** Creates a new interview session for a user with specified experience level and programming language.

**Path Parameters:**
- `userId` (String) - The ID of the user starting the interview

**Query Parameters:**
- `experienceLevel` (String) - User's experience level (e.g., "fresher", "junior", "mid", "senior", "lead")
- `language` (String) - Programming language for interview (e.g., "Java", "Python", "JavaScript")

**Example:**
```
POST /interviews/start/user123?experienceLevel=mid&language=Java
```

**Response:** `200 OK`
```json
{
  "id": "65f8a3b2c1234567890abcde",
  "userId": "user123",
  "startTime": "2025-10-06T10:30:00",
  "endTime": null,
  "status": "ACTIVE",
  "questionAnswers": [],
  "experienceLevel": "mid",
  "language": "Java"
}
```

**Error Responses:**

**400 BAD REQUEST** - Validation failed (empty userId)
```json
{
  "status": 400,
  "error": "Validation Failed",
  "message": "User ID cannot be empty",
  "path": "/api/v1/interviews/start/",
  "timestamp": "2025-10-03 10:30:00"
}
```

**409 CONFLICT** - If user already has an active session
```json
{
  "status": 409,
  "error": "Session Already Exists",
  "message": "User user123 already has an active interview session",
  "path": "/api/v1/interviews/start/user123",
  "timestamp": "2025-10-03 10:30:00"
}
```

---

### 2. Get First Question

**Endpoint:** `GET /interviews/{sessionId}/question`

**Description:** Retrieves the FIRST interview question for the session. This endpoint should ONLY be called ONCE per session to get the first question. All subsequent questions are provided in the `nextQuestion` field of the answer submission response.

**Path Parameters:**
- `sessionId` (String) - The ID of the interview session

**Response:** `200 OK`
```json
{
  "question": "Can you explain the difference between abstract classes and interfaces in Java?"
}
```

**Error Responses:**

**400 BAD REQUEST** - Validation failed (empty sessionId)
```json
{
  "status": 400,
  "error": "Validation Failed",
  "message": "Session ID cannot be empty",
  "path": "/api/v1/interviews//question",
  "timestamp": "2025-10-03 10:35:00"
}
```

**404 NOT FOUND** - Session not found
```json
{
  "status": 404,
  "error": "Session Not Found",
  "message": "Session not found with id: xyz. Cannot get next question",
  "path": "/api/v1/interviews/xyz/question",
  "timestamp": "2025-10-03 10:35:00"
}
```

**400 BAD REQUEST** - Session is not active
```json
{
  "status": 400,
  "error": "Invalid Session State",
  "message": "Session xyz is in COMPLETED state, expected ACTIVE",
  "path": "/api/v1/interviews/xyz/question",
  "timestamp": "2025-10-03 10:35:00"
}
```

**400 BAD REQUEST** - Previous question not answered
```json
{
  "status": 400,
  "error": "Invalid Session State",
  "message": "Please answer the current question before requesting a new one",
  "path": "/api/v1/interviews/xyz/question",
  "timestamp": "2025-10-06 10:35:00"
}
```

**400 BAD REQUEST** - Called after first question
```json
{
  "status": 400,
  "error": "Invalid Session State",
  "message": "Use the nextQuestion from the previous answer response. This endpoint is only for the first question.",
  "path": "/api/v1/interviews/xyz/question",
  "timestamp": "2025-10-06 10:35:00"
}
```

---

### 3. Submit Answer

**Endpoint:** `POST /interviews/{sessionId}/answer`

**Description:** Submits an audio answer for the current question.

**Path Parameters:**
- `sessionId` (String) - The ID of the interview session

**Request:** `multipart/form-data`
- `file` (File) - Audio file (max 10MB)
- `userId` (String) - User ID
- `userName` (String) - User name

**Example using JavaScript/Fetch:**
```javascript
const formData = new FormData();
formData.append('file', audioBlob, 'answer.webm');
formData.append('userId', 'user123');
formData.append('userName', 'John Doe');

fetch('http://localhost:8085/api/v1/interviews/sessionId/answer', {
  method: 'POST',
  body: formData
})
```

**Response:** `200 OK`
```json
{
  "questionIndex": 1,
  "question": "Can you explain the difference between abstract classes and interfaces in Java?",
  "answer": "Abstract classes can have both abstract and concrete methods while interfaces only define method signatures. Abstract classes support constructors but interfaces don't.",
  "totalQuestionsAnswered": 1,
  "sessionStatus": "ACTIVE",
  "feedback": {
    "feedback": "Good answer! You covered the basic differences. However, note that since Java 8, interfaces can also have default methods.",
    "score": 7
  },
  "nextQuestion": "What are the four principles of Object-Oriented Programming?"
}
```

**Response Fields:**
- `questionIndex` (number) - Which question was answered (1, 2, 3...)
- `question` (string) - The question that was answered
- `answer` (string) - The transcribed answer text from audio
- `totalQuestionsAnswered` (number) - How many questions have been answered so far
- `sessionStatus` (string) - Current session status (ACTIVE or COMPLETED)
- `feedback` (object) - AI-generated feedback on the answer
  - `feedback` (string) - Detailed feedback text
  - `score` (number) - Score 0-10
- `nextQuestion` (string) - The next question to display (use this instead of calling GET /question)

**Error Responses:**

**400 BAD REQUEST** - Validation failed (empty sessionId, userId, or userName)
```json
{
  "status": 400,
  "error": "Validation Failed",
  "message": "Session ID cannot be empty",
  "path": "/api/v1/interviews//answer",
  "timestamp": "2025-10-03 10:40:00"
}
```

**400 BAD REQUEST** - Invalid file type
```json
{
  "status": 500,
  "error": "File Upload Failed",
  "message": "Invalid file type. Allowed audio formats: mp3, wav, m4a, webm, ogg, aac, flac, wma. Received: pdf",
  "path": "/api/v1/interviews/xyz/answer",
  "timestamp": "2025-10-03 10:40:00"
}
```

**404 NOT FOUND** - Session not found
```json
{
  "status": 404,
  "error": "Session Not Found",
  "message": "Session not found with id: xyz. Cannot submit answer",
  "path": "/api/v1/interviews/xyz/answer",
  "timestamp": "2025-10-03 10:40:00"
}
```

**400 BAD REQUEST** - Answer already submitted
```json
{
  "status": 400,
  "error": "Answer Already Submitted",
  "message": "Answer already submitted for current question in session xyz",
  "path": "/api/v1/interviews/xyz/answer",
  "timestamp": "2025-10-03 10:40:00"
}
```

**500 INTERNAL SERVER ERROR** - File upload failed
```json
{
  "status": 500,
  "error": "File Upload Failed",
  "message": "Failed to upload file: Cannot upload empty file",
  "path": "/api/v1/interviews/xyz/answer",
  "timestamp": "2025-10-03 10:40:00"
}
```

**413 PAYLOAD TOO LARGE** - File size exceeds limit
```json
{
  "status": 413,
  "error": "File Too Large",
  "message": "File size exceeds maximum allowed size of 10MB",
  "path": "/api/v1/interviews/xyz/answer",
  "timestamp": "2025-10-03 10:40:00"
}
```

---

### 4. End Interview Session

**Endpoint:** `POST /interviews/{sessionId}/end`

**Description:** Ends an interview session.

**Path Parameters:**
- `sessionId` (String) - The ID of the interview session

**Query Parameters:**
- `force` (boolean) - If `true`, force end regardless of time. If `false`, only end if 30+ minutes have passed.

**Example:**
```
POST /interviews/sessionId/end?force=true
POST /interviews/sessionId/end?force=false
```

**Response:** `200 OK`
```json
{
  "id": "65f8a3b2c1234567890abcde",
  "userId": "user123",
  "startTime": "2025-10-03T10:30:00",
  "endTime": "2025-10-03T11:00:00",
  "status": "COMPLETED",
  "questionAnswers": [...],
  "recordingUrl": null
}
```

**Error Responses:**

**400 BAD REQUEST** - Validation failed (empty sessionId)
```json
{
  "status": 400,
  "error": "Validation Failed",
  "message": "Session ID cannot be empty",
  "path": "/api/v1/interviews//end",
  "timestamp": "2025-10-03 10:45:00"
}
```

**400 BAD REQUEST** - When force=false and time not completed
```json
{
  "status": 400,
  "error": "Session Time Not Completed",
  "message": "Interview session time not completed yet. Remaining time: 15 minutes",
  "path": "/api/v1/interviews/xyz/end",
  "timestamp": "2025-10-03 10:45:00"
}
```

---

### 5. Get Session Details

**Endpoint:** `GET /interviews/{sessionId}/details`

**Description:** Retrieves complete details of an interview session.

**Path Parameters:**
- `sessionId` (String) - The ID of the interview session

**Response:** `200 OK`
```json
{
  "sessionId": "65f8a3b2c1234567890abcde",
  "userId": "user123",
  "startTime": "2025-10-03T10:30:00",
  "endTime": "2025-10-03T11:00:00",
  "status": "COMPLETED",
  "questionAnswers": [
    {
      "questionIndex": 1,
      "question": "What is polymorphism in object-oriented programming?",
      "answer": "Polymorphism is a fundamental concept...",
      "audioFileUrl": "C:\\work\\java\\interviewgenius\\uploads\\user123_JohnDoe\\sessionId\\answer1.webm",
      "questionTimestamp": "2025-10-03T10:31:00",
      "answerTimestamp": "2025-10-03T10:32:30"
    },
    {
      "questionIndex": 2,
      "question": "Explain the difference between ArrayList and LinkedList in Java.",
      "answer": "ArrayList uses a dynamic array...",
      "audioFileUrl": "C:\\work\\java\\interviewgenius\\uploads\\user123_JohnDoe\\sessionId\\answer2.webm",
      "questionTimestamp": "2025-10-03T10:33:00",
      "answerTimestamp": "2025-10-03T10:35:00"
    }
  ]
}
```

**Error Responses:**

**400 BAD REQUEST** - Validation failed (empty sessionId)
```json
{
  "status": 400,
  "error": "Validation Failed",
  "message": "Session ID cannot be empty",
  "path": "/api/v1/interviews//details",
  "timestamp": "2025-10-03 11:00:00"
}
```

**404 NOT FOUND** - Session not found
```json
{
  "status": 404,
  "error": "Session Not Found",
  "message": "Session not found with id: xyz. Cannot retrieve session details",
  "path": "/api/v1/interviews/xyz/details",
  "timestamp": "2025-10-03 11:00:00"
}
```

---

## Common Data Models

### InterviewSession (Entity)

```typescript
{
  id: string;                    // Session ID
  userId: string;                // User ID
  startTime: string;             // ISO 8601 datetime
  endTime: string | null;        // ISO 8601 datetime or null if active
  status: "ACTIVE" | "COMPLETED";
  questionAnswers: QuestionAnswer[];
  recordingUrl: string | null;   // Not currently used
}
```

### SessionDetailsDto (Response DTO)

```typescript
{
  sessionId: string;             // Session ID (renamed from 'id' for clarity)
  userId: string;                // User ID
  startTime: string;             // ISO 8601 datetime
  endTime: string | null;        // ISO 8601 datetime or null if active
  status: "ACTIVE" | "COMPLETED";
  questionAnswers: QuestionAnswerDto[];
}
```

### QuestionDto (Response DTO)

```typescript
{
  question: string;              // Question text
}
```

### QuestionAnswer (Embedded Entity)

```typescript
{
  questionIndex: number;         // Question number (1, 2, 3, ...)
  question: string;              // Question text
  answer: string | null;         // Answer text (null if not answered yet)
  audioFileUrl: string | null;   // Audio file path/URL (null if not answered yet)
  questionTimestamp: string;     // ISO 8601 datetime
  answerTimestamp: string | null; // ISO 8601 datetime or null
}
```

### QuestionAnswerDto (Response DTO)

```typescript
{
  questionIndex: number;         // Question number (1, 2, 3, ...)
  question: string;              // Question text
  answer: string | null;         // Answer text (null if not answered yet)
  audioFileUrl: string | null;   // Audio file path/URL (null if not answered yet)
  questionTimestamp: string;     // ISO 8601 datetime
  answerTimestamp: string | null; // ISO 8601 datetime or null
}
```

### AnswerSubmissionResponseDto (Response DTO)

```typescript
{
  questionIndex: number;          // Which question was answered (1, 2, 3...)
  question: string;               // The question that was answered
  answer: string;                 // The transcribed answer text
  totalQuestionsAnswered: number; // How many questions answered so far
  sessionStatus: string;          // "ACTIVE" or "COMPLETED"
  feedback: FeedbackDto;          // AI-generated feedback
  nextQuestion: string;           // Next question to display
}
```

### FeedbackDto (Response DTO)

```typescript
{
  feedback: string;               // Detailed feedback text
  score: number;                  // Score 0-10
}
```

### ErrorResponseDto

All error responses follow this structure:

```typescript
{
  status: number;                // HTTP status code
  error: string;                 // Error category/type
  message: string;               // Detailed error message
  path: string;                  // Request path
  timestamp: string;             // "yyyy-MM-dd HH:mm:ss"
}
```

---

## Typical Interview Flow

1. **Start Session**
   ```
   POST /interviews/start/user123?experienceLevel=mid&language=Java
   → Returns session with id "xyz"
   ```

2. **Get First Question** (call ONCE)
   ```
   GET /interviews/xyz/question
   → Returns first question
   ```

3. **Submit Answer**
   ```
   POST /interviews/xyz/answer
   → Upload audio file
   → Returns: questionIndex, question, answer, feedback, nextQuestion, totalQuestionsAnswered, sessionStatus
   ```

4. **Display nextQuestion from response and submit answer**
   ```
   POST /interviews/xyz/answer
   → Upload audio file for nextQuestion
   → Returns: feedback and another nextQuestion
   ```

5. **Repeat step 4** for all subsequent questions (do NOT call GET /question again)

6. **End Session**
   ```
   POST /interviews/xyz/end?force=true
   → Returns completed session
   ```

7. **Get Details** (optional, for review)
   ```
   GET /interviews/xyz/details
   → Returns full session with all Q&A
   ```

---

## HTTP Status Codes

| Code | Description |
|------|-------------|
| 200 | Success |
| 400 | Bad Request (invalid state, already answered, etc.) |
| 404 | Not Found (session not found) |
| 409 | Conflict (session already exists) |
| 413 | Payload Too Large (file > 10MB) |
| 500 | Internal Server Error (file upload failed, unexpected errors) |

---

## File Upload Constraints

- **Maximum file size:** 10 MB
- **Supported formats:** Audio files (mp3, wav, m4a, webm, ogg, aac, flac, wma)
- **Form field name:** `file`
- **Content-Type:** `multipart/form-data`
- **Validation:** File extension and content type are validated server-side

---

## Notes for Frontend Developers

1. **DTO Naming Conventions:**
   - All response DTOs follow consistent naming with "Dto" suffix
   - `QuestionDto` - Contains question text
   - `SessionDetailsDto` - Contains full session details with `sessionId` field
   - `QuestionAnswerDto` - Contains question/answer pair details
   - `AnswerSubmissionResponseDto` - Response after submitting an answer
   - `ErrorResponseDto` - Standardized error response format

2. **Session Management:**
   - Only one active session per user is allowed
   - Always check for existing active sessions before starting a new one
   - Store `sessionId` after starting a session
   - `SessionDetailsDto` uses `sessionId` field (not `id`) for clarity

3. **Question Flow - IMPORTANT:**
   - Call GET `/question` ONLY ONCE to get the first question
   - After submitting an answer, use the `nextQuestion` field from the response
   - Do NOT call GET `/question` repeatedly - this will result in errors
   - The new flow eliminates redundant API calls and provides better performance

4. **Question-Answer Synchronization:**
   - Questions are numbered sequentially (questionIndex: 1, 2, 3, ...)
   - You MUST answer the current question before requesting the next one
   - Each question can only be answered once
   - This ensures perfect 1:1 sync between questions and answers

5. **AI-Powered Feedback:**
   - Every answer submission includes feedback from AI
   - Feedback includes detailed text analysis and a score (0-10)
   - Use feedback to provide real-time insights to users
   - Display score visually (e.g., progress bar, rating stars)
   - Consider color coding: Red (0-4), Yellow (5-6), Light Green (7-8), Dark Green (9-10)

6. **Audio Recording:**
   - Record audio in browser using MediaRecorder API
   - Convert to Blob and send as form-data
   - Maximum 10MB file size
   - Audio file URL/path is stored and returned in session details for each answer
   - Answer submission returns rich response with progress tracking (questionIndex, totalQuestionsAnswered)
   - Use `totalQuestionsAnswered` to show progress: "Question 3 of 10 answered"

7. **Error Handling:**
   - All errors return structured JSON with `status`, `error`, `message`, `path`, `timestamp`
   - Check `status` field for HTTP status code
   - Display `message` field to users
   - Validation errors (400 BAD REQUEST) indicate missing or invalid input parameters

8. **Input Validation:**
   - All required path parameters (userId, sessionId) are validated as non-empty
   - All required request parameters (userId, userName, experienceLevel, language) are validated as non-empty
   - File type is validated to ensure only audio files are accepted
   - File size is limited to 10MB

9. **Timestamps:**
   - All timestamps are in ISO 8601 format
   - Convert to local timezone in frontend if needed

10. **Interview Duration:**
    - Default duration is 30 minutes 30 seconds
    - Use `force=true` to end session immediately
    - Use `force=false` to enforce time limit

11. **CORS:**
   - CORS is configured in the service
   - If facing CORS issues, verify frontend origin is allowed

---

## Example Frontend Integration (React)

```javascript
// Start interview
const startInterview = async (userId, experienceLevel, language) => {
  const response = await fetch(
    `http://localhost:8085/api/v1/interviews/start/${userId}?experienceLevel=${experienceLevel}&language=${language}`,
    { method: 'POST' }
  );
  const session = await response.json();
  return session.id; // Store this sessionId
};

// Get FIRST question (call ONCE per session)
const getFirstQuestion = async (sessionId) => {
  const response = await fetch(`http://localhost:8085/api/v1/interviews/${sessionId}/question`);
  const data = await response.json();
  return data.question;
};

// Submit answer
const submitAnswer = async (sessionId, audioBlob, userId, userName) => {
  const formData = new FormData();
  formData.append('file', audioBlob, 'answer.webm');
  formData.append('userId', userId);
  formData.append('userName', userName);

  const response = await fetch(`http://localhost:8085/api/v1/interviews/${sessionId}/answer`, {
    method: 'POST',
    body: formData
  });
  const result = await response.json();

  // result.questionIndex - which question was answered
  // result.question - the question that was answered
  // result.answer - the transcribed answer
  // result.feedback.feedback - detailed feedback text
  // result.feedback.score - score 0-10
  // result.nextQuestion - NEXT question to display (use this, don't call GET /question)
  // result.totalQuestionsAnswered - progress tracking
  // result.sessionStatus - ACTIVE or COMPLETED

  return result;
};

// End interview
const endInterview = async (sessionId, force = true) => {
  const response = await fetch(`http://localhost:8085/api/v1/interviews/${sessionId}/end?force=${force}`, {
    method: 'POST'
  });
  const session = await response.json();
  return session;
};

// Get session details
const getSessionDetails = async (sessionId) => {
  const response = await fetch(`http://localhost:8085/api/v1/interviews/${sessionId}/details`);
  const details = await response.json();

  // details.sessionId - Session ID (not 'id')
  // details.userId - User ID
  // details.status - ACTIVE or COMPLETED
  // details.questionAnswers - Array of QuestionAnswerDto objects

  return details;
};

// Complete interview flow example
const runInterview = async () => {
  const userId = 'user123';
  const userName = 'John Doe';

  // 1. Start session
  const sessionId = await startInterview(userId, 'mid', 'Java');

  // 2. Get first question
  let currentQuestion = await getFirstQuestion(sessionId);
  console.log('Q1:', currentQuestion);

  // 3. Loop through questions
  while (true) {
    // Record audio for current question
    const audioBlob = await recordAudio();

    // Submit answer
    const result = await submitAnswer(sessionId, audioBlob, userId, userName);

    // Display feedback
    console.log('Feedback:', result.feedback.feedback);
    console.log('Score:', result.feedback.score);
    console.log(`Progress: ${result.totalQuestionsAnswered} questions answered`);

    // Check if more questions available
    if (result.nextQuestion) {
      currentQuestion = result.nextQuestion; // Use nextQuestion from response
      console.log('Next Q:', currentQuestion);
    } else {
      break; // No more questions
    }
  }

  // 4. End session
  await endInterview(sessionId, true);
};
```

---

## API Change Log

### 2025-10-06 - Interview Flow Optimization & AI Integration
- **BREAKING CHANGE**: Updated interview question flow
  - GET `/question` endpoint now only returns the FIRST question
  - Subsequent questions come from `nextQuestion` field in answer response
  - Frontend should NOT call GET `/question` repeatedly
- Added AI-powered feedback system
  - `AnswerSubmissionResponseDto` now includes `feedback` object (FeedbackDto)
  - Feedback includes detailed text and score (0-10)
- Added `nextQuestion` field to answer response
- Added `question` and `answer` fields to answer response for context
- Added `experienceLevel` and `language` parameters to start endpoint
- Created `FeedbackDto` response DTO
- Updated flow to eliminate redundant API calls
- Renamed endpoint: `Get Next Question` → `Get First Question`

### 2025-10-04 - DTO Naming Convention Updates
- Renamed all response DTOs to include "Dto" suffix for consistency:
  - `InputTypeQuestion` → `QuestionDto`
  - `ErrorResponse` → `ErrorResponseDto`
  - `AnswerSubmissionResponse` → `AnswerSubmissionResponseDto`
- Updated `SessionDetailsDto` field name: `id` → `sessionId` for clarity
- All DTOs now follow consistent naming conventions

### 2025-10-03 - Initial Documentation
- Created comprehensive API documentation
- Documented all 5 endpoints
- Added error response examples
- Included frontend integration examples

---

## Contact

For questions or issues, contact the backend team.

**Last Updated:** 2025-10-06
