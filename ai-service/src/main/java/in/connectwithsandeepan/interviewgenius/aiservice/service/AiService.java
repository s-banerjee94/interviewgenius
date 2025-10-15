package in.connectwithsandeepan.interviewgenius.aiservice.service;

import in.connectwithsandeepan.interviewgenius.aiservice.dto.InterviewResponse;
import in.connectwithsandeepan.interviewgenius.aiservice.entity.InputTypeQuestion;
import in.connectwithsandeepan.interviewgenius.aiservice.entity.Question;
import in.connectwithsandeepan.interviewgenius.aiservice.entity.Resume;

public interface AiService {
    Question genarateQuestion();

    InputTypeQuestion generateShortInputTypeQuestion();

    InputTypeQuestion generateDescriptiveInputTypeQuestion();

    InputTypeQuestion genarateInputTypeDsaQuestion();

    String transcribeAudio(String filePath);

    String startInterview(String conversationId, String experienceLevel, String language);

    InterviewResponse submitAnswerAndGetNextQuestion(String conversationId, String answer);

    Resume parseResumeText(String resumeText, Long userId);
}
