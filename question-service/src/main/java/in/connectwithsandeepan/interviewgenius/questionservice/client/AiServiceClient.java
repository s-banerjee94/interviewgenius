package in.connectwithsandeepan.interviewgenius.questionservice.client;

import in.connectwithsandeepan.interviewgenius.questionservice.entity.Question;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "ai-service", path = "api/v1/ai")
public interface AiServiceClient {

    @GetMapping("/question")
    Question getQuestion();
}