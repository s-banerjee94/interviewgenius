package in.connectwithsandeepan.interviewgenius.interviewservice.controller;

import in.connectwithsandeepan.interviewgenius.interviewservice.dto.AnswerSubmissionResponseDto;
import in.connectwithsandeepan.interviewgenius.interviewservice.dto.QuestionDto;
import in.connectwithsandeepan.interviewgenius.interviewservice.dto.SessionDetailsDto;
import in.connectwithsandeepan.interviewgenius.interviewservice.dto.SessionListDto;
import in.connectwithsandeepan.interviewgenius.interviewservice.entity.InterviewSession;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Interview Management", description = "APIs for managing interview sessions, questions, and answer submissions")
public interface InterviewControllerApi {

    @Operation(
            summary = "Start a new interview session",
            description = "Initiates a new interview session for a user with specified experience level and programming language"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Interview session started successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = InterviewSession.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input parameters",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    ResponseEntity<InterviewSession> start(
            @Parameter(description = "Unique identifier of the user", required = true, example = "user123")
            @PathVariable @NotBlank(message = "User ID cannot be empty") String userId,

            @Parameter(description = "Experience level of the candidate", required = true, example = "INTERMEDIATE")
            @RequestParam @NotBlank(message = "Experience level cannot be empty") String experienceLevel,

            @Parameter(description = "Programming language for the interview", required = true, example = "Java")
            @RequestParam @NotBlank(message = "Language cannot be empty") String language
    );

    @Operation(
            summary = "Get the first question",
            description = "Retrieves the first question for the specified interview session"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Question retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = QuestionDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Session not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid session ID",
                    content = @Content
            )
    })
    ResponseEntity<QuestionDto> getFirstQuestion(
            @Parameter(description = "Unique identifier of the interview session", required = true, example = "session123")
            @PathVariable @NotBlank(message = "Session ID cannot be empty") String sessionId,

            @Parameter(description = "User ID from authentication token", required = true, hidden = true)
            @RequestHeader("X-User-Id") String userId
    );

    @Operation(
            summary = "End an interview session",
            description = "Terminates an active interview session. Can be forced to end regardless of completion status"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Interview session ended successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = InterviewSession.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Session not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid session ID or session already ended",
                    content = @Content
            )
    })
    ResponseEntity<InterviewSession> end(
            @Parameter(description = "Unique identifier of the interview session", required = true, example = "session123")
            @PathVariable @NotBlank(message = "Session ID cannot be empty") String sessionId,

            @Parameter(description = "Force end the session regardless of completion status", required = true, example = "false")
            @RequestParam boolean force
    );

    @Operation(
            summary = "Get session details",
            description = "Retrieves comprehensive details of an interview session including questions, answers, and status"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Session details retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SessionDetailsDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Session not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid session ID",
                    content = @Content
            )
    })
    ResponseEntity<SessionDetailsDto> getSessionDetails(
            @Parameter(description = "Unique identifier of the interview session", required = true, example = "session123")
            @PathVariable @NotBlank(message = "Session ID cannot be empty") String sessionId
    );

    @Operation(
            summary = "Submit answer to a question",
            description = "Uploads and submits an answer file (audio/video) for the current question in the interview session"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Answer submitted successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AnswerSubmissionResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid file or parameters",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Session not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "413",
                    description = "File size exceeds maximum limit (10MB)",
                    content = @Content
            )
    })
    ResponseEntity<AnswerSubmissionResponseDto> submitAnswer(
            @Parameter(description = "Unique identifier of the interview session", required = true, example = "session123")
            @PathVariable @NotBlank(message = "Session ID cannot be empty") String sessionId,

            @Parameter(
                    description = "Audio or video file containing the answer (max 10MB)",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestParam("file") MultipartFile file,

            @Parameter(description = "User ID from authentication token", required = true, hidden = true)
            @RequestHeader("X-User-Id") String userId,

            @Parameter(description = "User email from authentication token", required = true, hidden = true)
            @RequestHeader("X-User-Email") String email
    );

    @Operation(
            summary = "Get all interview sessions for a user",
            description = "Retrieves a list of all interview sessions for a specific user with basic information (sessionId, language, startTime, experienceLevel). Supports optional pagination."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sessions retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = SessionListDto.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid user ID",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    ResponseEntity<List<SessionListDto>> getAllSessions(
            @Parameter(description = "Unique identifier of the user", required = true, example = "user123")
            @PathVariable @NotBlank(message = "User ID cannot be empty") String userId,

            @Parameter(description = "Enable pagination for the results", required = false, example = "false")
            @RequestParam(defaultValue = "false") boolean pagination,

            @Parameter(description = "Page number (0-indexed, used when pagination=true)", required = false, example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page (used when pagination=true)", required = false, example = "10")
            @RequestParam(defaultValue = "10") int size
    );
}
