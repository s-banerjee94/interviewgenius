package in.connectwithsandeepan.interviewgenius.aiservice.service;


import in.connectwithsandeepan.interviewgenius.aiservice.entity.InputTypeQuestion;
import in.connectwithsandeepan.interviewgenius.aiservice.entity.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService{

    private final ChatClient chatClient;

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
    public InputTypeQuestion genarateInputTypeQuestion() {
        InputTypeQuestion question = chatClient.prompt()
                .user("Give java a question that can be answered in one character or in couple of words or sentence")
                .call()
                .entity(InputTypeQuestion.class);

        assert question != null;
        return question;
    }

    @Override
    public InputTypeQuestion genarateInputTypeQuestionSen() {
        InputTypeQuestion question = chatClient.prompt()
                .user("Give java a question that can be answered in couple of sentences, theory and conceptual questions")
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
}
