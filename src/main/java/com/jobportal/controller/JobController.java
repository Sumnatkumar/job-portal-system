package com.jobportal.controller;

import com.jobportal.dto.JobDTO;
import com.jobportal.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@Tag(name = "Jobs", description = "Job management endpoints")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    @Operation(summary = "Create a new job")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<JobDTO.Response> createJob(
            @Valid @RequestBody JobDTO.CreateRequest request,
            Authentication authentication) {
        Long recruiterId = (Long) authentication.getPrincipal();
        JobDTO.Response job = jobService.createJob(request, recruiterId);
        return ResponseEntity.ok(job);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job by ID")
    public ResponseEntity<JobDTO.Response> getJob(@PathVariable Long id) {
        JobDTO.Response job = jobService.getJob(id);
        return ResponseEntity.ok(job);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update job")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<JobDTO.Response> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobDTO.UpdateRequest request,
            Authentication authentication) {
        Long recruiterId = (Long) authentication.getPrincipal();
        JobDTO.Response updated = jobService.updateJob(id, request, recruiterId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete job")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteJob(
            @PathVariable Long id,
            Authentication authentication) {
        Long recruiterId = (Long) authentication.getPrincipal();
        jobService.deleteJob(id, recruiterId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    @Operation(summary = "Search jobs")
    public ResponseEntity<List<JobDTO.Response>> searchJobs(
            @Valid @RequestBody JobDTO.SearchRequest request) {
        List<JobDTO.Response> jobs = jobService.searchJobs(request);
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/my-jobs")
    @Operation(summary = "Get jobs posted by current recruiter")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<JobDTO.Response>> getMyJobs(Authentication authentication) {
        Long recruiterId = (Long) authentication.getPrincipal();
        List<JobDTO.Response> jobs = jobService.getJobsByRecruiter(recruiterId);
        return ResponseEntity.ok(jobs);
    }
}
