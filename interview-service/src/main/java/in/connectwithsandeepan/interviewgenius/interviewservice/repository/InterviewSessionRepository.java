package in.connectwithsandeepan.interviewgenius.interviewservice.repository;

import in.connectwithsandeepan.interviewgenius.interviewservice.entity.InterviewSession;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InterviewSessionRepository extends MongoRepository<InterviewSession, String> {
    InterviewSession findByUserIdAndStatus(String userId, InterviewSession.Status status);
}
