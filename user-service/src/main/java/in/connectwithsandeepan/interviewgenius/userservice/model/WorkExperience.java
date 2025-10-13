package in.connectwithsandeepan.interviewgenius.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents work experience in a user's resume
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkExperience {


    private String jobTitle;

    private String companyName;

    private String location;

    private String startDate; // ISO format YYYY-MM

    private String endDate; // ISO format YYYY-MM or null if current

    private Boolean isCurrentRole;

    private String description;
}
