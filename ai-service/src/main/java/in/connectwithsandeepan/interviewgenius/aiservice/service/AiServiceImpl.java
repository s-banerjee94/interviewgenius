package in.connectwithsandeepan.interviewgenius.aiservice.service;


import in.connectwithsandeepan.interviewgenius.aiservice.dto.InterviewResponse;
import in.connectwithsandeepan.interviewgenius.aiservice.entity.InputTypeQuestion;
import in.connectwithsandeepan.interviewgenius.aiservice.entity.Question;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService{

    private final ChatClient chatClient;
    private final OpenAiAudioTranscriptionModel openAiTranscriptionModel;

    // testing with system prompt
    private final ChatClient inteviewChatClient;
    @Value("${interview.prompt.file-path}")
    private Resource resource;

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
}
