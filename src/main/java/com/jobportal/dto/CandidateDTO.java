package com.jobportal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

@Data
public class CandidateDTO {

    @Data
    public static class CreateRequest {
        @NotBlank(message = "Name is required")
        private String name;

        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;

        private Integer experience;
        private Set<String> skills;
        private String location;
    }

    @Data
    public static class UpdateRequest {
        private String name;
        private Integer experience;
        private Set<String> skills;
        private String location;
    }

    @Data
    public static class Response {
        private Long id;
        private String name;
        private String email;
        private Integer experience;
        private Set<String> skills;
        private String location;
    }
}
