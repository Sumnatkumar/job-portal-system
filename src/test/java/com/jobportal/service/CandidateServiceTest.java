package com.jobportal.service;

import com.jobportal.dto.CandidateDTO;
import com.jobportal.entity.Candidate;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.CandidateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CandidateService candidateService;

    private Candidate candidate;
    private Long candidateId = 1L;

    @BeforeEach
    void setUp() {
        Set<String> skills = new HashSet<>();
        skills.add("Java");
        skills.add("Spring Boot");

        candidate = new Candidate();
        candidate.setId(candidateId);
        candidate.setName("John Doe");
        candidate.setEmail("john@example.com");
        candidate.setPassword("encodedPassword");
        candidate.setExperience(5);
        candidate.setSkills(skills);
        candidate.setLocation("New York");
    }

    @Test
    void getCandidate_ShouldReturnCandidate_WhenCandidateExists() {
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));

        CandidateDTO.Response result = candidateService.getCandidate(candidateId);

        assertNotNull(result);
        assertEquals(candidateId, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        verify(candidateRepository, times(1)).findById(candidateId);
    }

    @Test
    void getCandidate_ShouldThrowException_WhenCandidateNotFound() {
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> candidateService.getCandidate(candidateId));

        verify(candidateRepository, times(1)).findById(candidateId);
    }

    @Test
    void updateCandidate_ShouldUpdateAndReturnCandidate() {
        CandidateDTO.UpdateRequest updateRequest = new CandidateDTO.UpdateRequest();
        updateRequest.setName("Jane Doe");
        updateRequest.setExperience(7);

        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
        when(candidateRepository.save(any(Candidate.class))).thenReturn(candidate);

        CandidateDTO.Response result = candidateService.updateCandidate(candidateId, updateRequest);

        assertNotNull(result);
        verify(candidateRepository, times(1)).findById(candidateId);
        verify(candidateRepository, times(1)).save(any(Candidate.class));
    }

    @Test
    void deleteCandidate_ShouldDeleteCandidate() {
        when(candidateRepository.existsById(candidateId)).thenReturn(true);

        candidateService.deleteCandidate(candidateId);

        verify(candidateRepository, times(1)).existsById(candidateId);
        verify(candidateRepository, times(1)).deleteById(candidateId);
    }

    @Test
    void deleteCandidate_ShouldThrowException_WhenCandidateNotFound() {
        when(candidateRepository.existsById(candidateId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> candidateService.deleteCandidate(candidateId));

        verify(candidateRepository, times(1)).existsById(candidateId);
        verify(candidateRepository, never()).deleteById(candidateId);
    }
}