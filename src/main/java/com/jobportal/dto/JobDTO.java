package com.jobportal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class JobDTO {

    @Data
    public static class CreateRequest {
        @NotBlank(message = "Job title is required")
        private String title;

        private String description;
        private Set<String> requiredSkills;
        private Integer experienceRequired;
        private String location;
    }

    @Data
    public static class UpdateRequest {
        private String title;
        private String description;
        private Set<String> requiredSkills;
        private Integer experienceRequired;
        private String location;
    }

    @Data
    public static class Response {
        private Long id;
        private String title;
        private String description;
        private Set<String> requiredSkills;
        private Integer experienceRequired;
        private String location;
        private LocalDateTime postedDate;
        private RecruiterDTO.Response postedBy;
    }

    @Data
    public static class SearchRequest {
        private String keyword;
        private Set<String> skills;
        private Integer minExperience;
        private Integer maxExperience;
        private String location;
    }
}
