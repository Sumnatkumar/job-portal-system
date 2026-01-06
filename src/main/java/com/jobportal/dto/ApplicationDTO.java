package com.jobportal.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApplicationDTO {

    @Data
    public static class CreateRequest {
        private Long jobId;
    }

    @Data
    public static class UpdateRequest {
        private String status;
    }

    @Data
    public static class Response {
        private Long id;
        private JobDTO.Response job;
        private CandidateDTO.Response candidate;
        private LocalDateTime appliedAt;
        private String status;
    }
}
