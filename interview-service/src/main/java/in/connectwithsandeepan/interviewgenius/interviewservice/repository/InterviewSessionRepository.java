package in.connectwithsandeepan.interviewgenius.interviewservice.repository;

import in.connectwithsandeepan.interviewgenius.interviewservice.entity.InterviewSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface InterviewSessionRepository extends MongoRepository<InterviewSession, String> {
    InterviewSession findByUserIdAndStatus(String userId, InterviewSession.Status status);

    // Find all sessions by userId with pagination
    Page<InterviewSession> findByUserId(String userId, Pageable pageable);

    // Find all sessions by userId with sorting (no pagination)
    List<InterviewSession> findByUserId(String userId, Sort sort);
}
