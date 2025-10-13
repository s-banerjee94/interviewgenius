package in.connectwithsandeepan.interviewgenius.userservice.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.connectwithsandeepan.interviewgenius.userservice.model.Resume;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

/**
 * JPA converter for Resume object to JSON string conversion
 */
@Converter
@Slf4j
public class ResumeJsonConverter implements AttributeConverter<Resume, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // Configure ObjectMapper to handle Java 8 date/time types
        objectMapper.findAndRegisterModules();
    }

    @Override
    public String convertToDatabaseColumn(Resume resume) {
        if (resume == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(resume);
        } catch (JsonProcessingException e) {
            log.error("Error converting Resume to JSON", e);
            throw new IllegalArgumentException("Error converting Resume to JSON", e);
        }
    }

    @Override
    public Resume convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, Resume.class);
        } catch (JsonProcessingException e) {
            log.error("Error converting JSON to Resume", e);
            throw new IllegalArgumentException("Error converting JSON to Resume", e);
        }
    }
}
