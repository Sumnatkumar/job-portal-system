package com.jobportal.controller;

import com.jobportal.dto.ApplicationDTO;
import com.jobportal.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@Tag(name = "Applications", description = "Job application endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/apply")
    @Operation(summary = "Apply for a job")
    public ResponseEntity<ApplicationDTO.Response> applyForJob(
            @Valid @RequestBody ApplicationDTO.CreateRequest request,
            Authentication authentication) {
        Long candidateId = (Long) authentication.getPrincipal();
        ApplicationDTO.Response application = applicationService.applyForJob(
                request.getJobId(), candidateId);
        return ResponseEntity.ok(application);
    }

    @GetMapping("/my-applications")
    @Operation(summary = "Get current candidate's applications")
    public ResponseEntity<List<ApplicationDTO.Response>> getMyApplications(
            Authentication authentication) {
        Long candidateId = (Long) authentication.getPrincipal();
        List<ApplicationDTO.Response> applications =
                applicationService.getApplicationsByCandidate(candidateId);
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/job/{jobId}")
    @Operation(summary = "Get applications for a job")
    public ResponseEntity<List<ApplicationDTO.Response>> getApplicationsForJob(
            @PathVariable Long jobId,
            Authentication authentication) {
        Long recruiterId = (Long) authentication.getPrincipal();
        List<ApplicationDTO.Response> applications =
                applicationService.getApplicationsByJob(jobId, recruiterId);
        return ResponseEntity.ok(applications);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update application status")
    public ResponseEntity<ApplicationDTO.Response> updateApplicationStatus(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationDTO.UpdateRequest request,
            Authentication authentication) {
        Long recruiterId = (Long) authentication.getPrincipal();
        ApplicationDTO.Response updated = applicationService.updateApplicationStatus(
                id, request.getStatus(), recruiterId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Withdraw application")
    public ResponseEntity<Void> withdrawApplication(
            @PathVariable Long id,
            Authentication authentication) {
        Long candidateId = (Long) authentication.getPrincipal();
        applicationService.withdrawApplication(id, candidateId);
        return ResponseEntity.noContent().build();
    }
}
