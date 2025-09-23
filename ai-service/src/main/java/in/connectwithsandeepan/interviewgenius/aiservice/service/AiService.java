package in.connectwithsandeepan.interviewgenius.aiservice.service;

import in.connectwithsandeepan.interviewgenius.aiservice.entity.InputTypeQuestion;
import in.connectwithsandeepan.interviewgenius.aiservice.entity.Question;

public interface AiService {
    Question genarateQuestion();

    InputTypeQuestion genarateInputTypeQuestion();

    InputTypeQuestion genarateInputTypeQuestionSen();

    InputTypeQuestion genarateInputTypeDsaQuestion();
}
