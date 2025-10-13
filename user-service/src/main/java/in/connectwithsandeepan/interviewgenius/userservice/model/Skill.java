package in.connectwithsandeepan.interviewgenius.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a skill in a user's resume
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {


    private String name;

    private String level; // Beginner, Intermediate, Advanced, Expert
}
