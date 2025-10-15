package in.connectwithsandeepan.interviewgenius.userservice.client;

import in.connectwithsandeepan.interviewgenius.userservice.dto.AiResumeResponse;
import in.connectwithsandeepan.interviewgenius.userservice.dto.ResumeParseRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client for communicating with AI Service
 */
@FeignClient(name = "ai-service", path = "/api/v1/ai")
public interface AiServiceClient {

    /**
     * Parse resume text and convert to Resume entity
     * @param request Resume parse request containing text and userId
     * @return Parsed Resume object with skills as List<String>
     */
    @PostMapping("/parse-resume")
    ResponseEntity<AiResumeResponse> parseResume(@RequestBody ResumeParseRequest request);
}
