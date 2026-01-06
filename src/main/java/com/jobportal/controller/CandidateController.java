package com.jobportal.controller;

import com.jobportal.dto.CandidateDTO;
import com.jobportal.service.CandidateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidates")
@Tag(name = "Candidates", description = "Candidates management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class CandidateController {

    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current candidate profile")
    public ResponseEntity<CandidateDTO.Response> getCurrentCandidate(Authentication authentication) {
        Long candidateId = (Long) authentication.getPrincipal();
        CandidateDTO.Response candidate = candidateService.getCandidate(candidateId);
        return ResponseEntity.ok(candidate);
    }

    @PutMapping("/me")
    @Operation(summary = "Update current candidate profile")
    public ResponseEntity<CandidateDTO.Response> updateCurrentCandidate(
            @Valid @RequestBody CandidateDTO.UpdateRequest request,
            Authentication authentication) {
        Long candidateId = (Long) authentication.getPrincipal();
        CandidateDTO.Response updated = candidateService.updateCandidate(candidateId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/me")
    @Operation(summary = "Delete current candidate account")
    public ResponseEntity<Void> deleteCurrentCandidate(Authentication authentication) {
        Long candidateId = (Long) authentication.getPrincipal();
        candidateService.deleteCandidate(candidateId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get all candidates (Admin only)")
    public ResponseEntity<List<CandidateDTO.Response>> getAllCandidates() {
        List<CandidateDTO.Response> candidates = candidateService.getAllCandidates();
        return ResponseEntity.ok(candidates);
    }
}
