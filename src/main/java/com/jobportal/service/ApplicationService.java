package com.jobportal.service;

import com.jobportal.dto.ApplicationDTO;
import com.jobportal.dto.CandidateDTO;
import com.jobportal.dto.JobDTO;

import com.jobportal.entity.ApplicationStatus;
import com.jobportal.entity.Candidate;
import com.jobportal.entity.Job;
import com.jobportal.entity.JobApplication;
import com.jobportal.exception.ResourceExistsException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.exception.UnauthorizedException;
import com.jobportal.repository.CandidateRepository;
import com.jobportal.repository.JobApplicationRepository;
import com.jobportal.repository.JobRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final CandidateRepository candidateRepository;

    public ApplicationService(JobApplicationRepository applicationRepository,
                              JobRepository jobRepository,
                              CandidateRepository candidateRepository) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.candidateRepository = candidateRepository;
    }

    @PreAuthorize("hasRole('CANDIDATE')")
    @Transactional
    public ApplicationDTO.Response applyForJob(Long jobId, Long candidateId) {
        if (applicationRepository.existsByJobIdAndCandidateId(jobId, candidateId)) {
            throw new ResourceExistsException("You have already applied for this job");
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

        JobApplication application = new JobApplication();
        application.setJob(job);
        application.setCandidate(candidate);

        JobApplication saved = applicationRepository.save(application);
        return convertToDTO(saved);
    }

    @PreAuthorize("hasRole('CANDIDATE') and #candidateId == authentication.principal.id")
    public List<ApplicationDTO.Response> getApplicationsByCandidate(Long candidateId) {
        List<JobApplication> applications = applicationRepository.findByCandidateId(candidateId);
        return applications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('RECRUITER')")
    public List<ApplicationDTO.Response> getApplicationsByJob(Long jobId, Long recruiterId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (!job.getPostedBy().getId().equals(recruiterId)) {
            throw new UnauthorizedException("You can only view applications for your own jobs");
        }

        List<JobApplication> applications = applicationRepository.findByJobId(jobId);
        return applications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @Transactional
    public ApplicationDTO.Response updateApplicationStatus(Long applicationId,
                                                           String status,
                                                           Long recruiterId) {
        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!application.getJob().getPostedBy().getId().equals(recruiterId)) {
            throw new UnauthorizedException("You can only update status for your own job applications");
        }

        try {
            ApplicationStatus applicationStatus = ApplicationStatus.valueOf(status.toUpperCase());
            application.setStatus(applicationStatus);
            JobApplication updated = applicationRepository.save(application);
            return convertToDTO(updated);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
    }

    @PreAuthorize("hasRole('CANDIDATE') and #candidateId == authentication.principal.id")
    @Transactional
    public void withdrawApplication(Long applicationId, Long candidateId) {
        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!application.getCandidate().getId().equals(candidateId)) {
            throw new UnauthorizedException("You can only withdraw your own applications");
        }

        applicationRepository.delete(application);
    }

    private ApplicationDTO.Response convertToDTO(JobApplication application) {
        ApplicationDTO.Response dto = new ApplicationDTO.Response();
        dto.setId(application.getId());
        dto.setAppliedAt(application.getAppliedAt());
        dto.setStatus(application.getStatus().toString());

        JobDTO.Response jobDTO = new JobDTO.Response();
        jobDTO.setId(application.getJob().getId());
        jobDTO.setTitle(application.getJob().getTitle());
        dto.setJob(jobDTO);

        CandidateDTO.Response candidateDTO = new CandidateDTO.Response();
        candidateDTO.setId(application.getCandidate().getId());
        candidateDTO.setName(application.getCandidate().getName());
        candidateDTO.setEmail(application.getCandidate().getEmail());
        dto.setCandidate(candidateDTO);

        return dto;
    }
}
