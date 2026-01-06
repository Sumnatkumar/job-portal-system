package com.jobportal.service;

import com.jobportal.dto.CandidateDTO;
import com.jobportal.entity.Candidate;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.CandidateRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final PasswordEncoder passwordEncoder;

    public CandidateService(CandidateRepository candidateRepository, PasswordEncoder passwordEncoder) {
        this.candidateRepository = candidateRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PreAuthorize("hasRole('CANDIDATE') and #id == authentication.principal.id")
    public CandidateDTO.Response getCandidate(Long id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
        return convertToDTO(candidate);
    }

    @PreAuthorize("hasRole('CANDIDATE') and #id == authentication.principal.id")
    @Transactional
    public CandidateDTO.Response updateCandidate(Long id, CandidateDTO.UpdateRequest request) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

        if (request.getName() != null) {
            candidate.setName(request.getName());
        }
        if (request.getExperience() != null) {
            candidate.setExperience(request.getExperience());
        }
        if (request.getSkills() != null) {
            candidate.setSkills(request.getSkills());
        }
        if (request.getLocation() != null) {
            candidate.setLocation(request.getLocation());
        }

        Candidate updated = candidateRepository.save(candidate);
        return convertToDTO(updated);
    }

    @PreAuthorize("hasRole('CANDIDATE') and #id == authentication.principal.id")
    @Transactional
    public void deleteCandidate(Long id) {
        if (!candidateRepository.existsById(id)) {
            throw new ResourceNotFoundException("Candidate not found");
        }
        candidateRepository.deleteById(id);
    }

    public List<CandidateDTO.Response> getAllCandidates() {
        return candidateRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private CandidateDTO.Response convertToDTO(Candidate candidate) {
        CandidateDTO.Response dto = new CandidateDTO.Response();
        dto.setId(candidate.getId());
        dto.setName(candidate.getName());
        dto.setEmail(candidate.getEmail());
        dto.setExperience(candidate.getExperience());
        dto.setSkills(candidate.getSkills());
        dto.setLocation(candidate.getLocation());
        return dto;
    }
}
