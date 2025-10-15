package in.connectwithsandeepan.interviewgenius.aiservice.service;


import in.connectwithsandeepan.interviewgenius.aiservice.dto.InterviewResponse;
import in.connectwithsandeepan.interviewgenius.aiservice.entity.InputTypeQuestion;
import in.connectwithsandeepan.interviewgenius.aiservice.entity.Question;
import in.connectwithsandeepan.interviewgenius.aiservice.entity.Resume;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService{

    private final ChatClient chatClient;
    private final OpenAiAudioTranscriptionModel openAiTranscriptionModel;

    // testing with system prompt
    private final ChatClient inteviewChatClient;

    // OpenAI ChatClient for resume parsing (better JSON generation)
    private final ChatClient resumeParserChatClient;

    @Value("${interview.prompt.file-path.for-interview}")
    private Resource resource;

    @Value("${interview.prompt.file-path.for-resume}")
    private Resource resumeParserPromptResource;

    @Override
    public Question genarateQuestion() {
        Question question = chatClient.prompt()
                .user("Give java mcq question with 4 options and answer")
                .call()
                .entity(Question.class);

        assert question != null;
        return question;
    }

    @Override
    public InputTypeQuestion generateShortInputTypeQuestion() {
        InputTypeQuestion question = chatClient.prompt()
                .user("Give java a question that can be answered in one character or in couple of words or sentence")
                .call()
                .entity(InputTypeQuestion.class);

        assert question != null;
        return question;
    }

    @Override
    public InputTypeQuestion generateDescriptiveInputTypeQuestion() {
        InputTypeQuestion question = chatClient.prompt()
                .user("Give Java a question that can be answered in couple of sentences, theory and conceptual questions, and add the last question if you asked previously.")
                .call()
                .entity(InputTypeQuestion.class);

        assert question != null;
        return question;
    }

    @Override
    public InputTypeQuestion genarateInputTypeDsaQuestion() {
        InputTypeQuestion question = chatClient.prompt()
                .user("Give DSA(Data Structure and Algorithm) a question that can be answered in one character or in couple of words or sentence")
                .call()
                .entity(InputTypeQuestion.class);

        assert question != null;
        return question;
    }

    @Override
    public String transcribeAudio(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path is required");
        }

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("File not found: " + filePath);
        }

        if (!Files.isRegularFile(path) || !Files.isReadable(path)) {
            throw new IllegalArgumentException("File is not readable or is not a regular file: " + filePath);
        }

        try {
            FileSystemResource audioFile = new FileSystemResource(filePath);
            OpenAiAudioApi.TranscriptResponseFormat responseFormat = OpenAiAudioApi.TranscriptResponseFormat.JSON;
            OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
                    .responseFormat(responseFormat)
                    .prompt("Transcribe the following audio to text")
                    .build();
            AudioTranscriptionPrompt transcriptionPrompt = new AudioTranscriptionPrompt(audioFile, options);
            AudioTranscriptionResponse response = openAiTranscriptionModel.call(transcriptionPrompt);

            return response.getResult().getOutput();
        } catch (Exception e) {
            throw new RuntimeException("Error processing audio file: " + e.getMessage(), e);
        }
    }

    @Override
    public String startInterview(String conversationId, String experienceLevel, String language) {
        return inteviewChatClient.prompt()
                .system(resource)
                .system(s -> s.param("experience_level", experienceLevel).param("language", language))
                .user("Start the interview by asking the first question.")
                .advisors(a -> a.param("chat_memory_conversation_id", conversationId))
                .call()
                .content();
    }

    @Override
    public InterviewResponse submitAnswerAndGetNextQuestion(String conversationId, String answer) {
        String promptText = String.format(
                "Candidate's answer: %s\n\n" +
                        "Based on this answer, provide:\n" +
                        "1. Ask the next relevant question\n",
                answer
        );

        return inteviewChatClient.prompt()
                .user(promptText)
                .advisors(a -> a.param("chat_memory_conversation_id", conversationId))
                .call()
                .entity(InterviewResponse.class);
    }

    @Override
    public Resume parseResumeText(String resumeText, Long userId) {
        log.info("Parsing resume text using OpenAI GPT for userId: {}", userId);

        if (resumeText == null || resumeText.trim().isEmpty()) {
            throw new IllegalArgumentException("Resume text cannot be null or empty");
        }

        try {
            log.debug("Resume text length: {} characters", resumeText.length());

            // Create user prompt with the full resume text
            String userPrompt = """
                Resume Text to Parse:

                %s
                """.formatted(resumeText);

            log.debug("Sending resume parse request to OpenAI with system prompt from template");

            // Call OpenAI with system prompt from template file
            Resume resume = resumeParserChatClient.prompt()
                    .system(resumeParserPromptResource)  // System prompt from .st file
                    .user(userPrompt)                     // User prompt with full resume text
                    .call()
                    .entity(Resume.class);

            if (resume != null) {

                // Initialize empty lists if null to avoid NPE
                if (resume.getWorkExperiences() == null) {
                    resume.setWorkExperiences(new java.util.ArrayList<>());
                }
                if (resume.getEducations() == null) {
                    resume.setEducations(new java.util.ArrayList<>());
                }
                if (resume.getSkills() == null) {
                    resume.setSkills(new java.util.ArrayList<>());
                }

                log.info("Successfully parsed resume - Work Experiences: {}, Education: {}, Skills: {}",
                        resume.getWorkExperiences().size(),
                        resume.getEducations().size(),
                        resume.getSkills().size());
            }

            return resume;

        } catch (Exception e) {
            log.error("Error parsing resume text: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to parse resume text: " + e.getMessage(), e);
        }
    }
}
