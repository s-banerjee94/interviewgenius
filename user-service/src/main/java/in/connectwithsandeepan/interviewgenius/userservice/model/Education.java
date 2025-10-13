package in.connectwithsandeepan.interviewgenius.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents education in a user's resume
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Education {

    private String degree;

    private String fieldOfStudy;

    private String institution;

    private String startDate; // ISO format YYYY-MM or YYYY

    private String endDate; // ISO format YYYY-MM or YYYY or null if current

    private Boolean isCurrentlyStudying;

    private String grade;

    private String description;
}
