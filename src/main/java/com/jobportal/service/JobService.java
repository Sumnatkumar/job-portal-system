package com.jobportal.service;

import com.jobportal.dto.JobDTO;
import com.jobportal.dto.RecruiterDTO;
import com.jobportal.entity.Job;
import com.jobportal.entity.Recruiter;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.exception.UnauthorizedException;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.RecruiterRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final RecruiterRepository recruiterRepository;

    public JobService(JobRepository jobRepository, RecruiterRepository recruiterRepository) {
        this.jobRepository = jobRepository;
        this.recruiterRepository = recruiterRepository;
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @Transactional
    public JobDTO.Response createJob(JobDTO.CreateRequest request, Long recruiterId) {
        Recruiter recruiter = recruiterRepository.findById(recruiterId)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));

        Job job = new Job();
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setRequiredSkills(request.getRequiredSkills());
        job.setExperienceRequired(request.getExperienceRequired());
        job.setLocation(request.getLocation());
        job.setPostedBy(recruiter);

        Job savedJob = jobRepository.save(job);
        return convertToDTO(savedJob);
    }

    public JobDTO.Response getJob(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        return convertToDTO(job);
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @Transactional
    public JobDTO.Response updateJob(Long id, JobDTO.UpdateRequest request, Long recruiterId) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (!job.getPostedBy().getId().equals(recruiterId)) {
            throw new UnauthorizedException("You can only update your own jobs");
        }

        if (request.getTitle() != null) {
            job.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            job.setDescription(request.getDescription());
        }
        if (request.getRequiredSkills() != null) {
            job.setRequiredSkills(request.getRequiredSkills());
        }
        if (request.getExperienceRequired() != null) {
            job.setExperienceRequired(request.getExperienceRequired());
        }
        if (request.getLocation() != null) {
            job.setLocation(request.getLocation());
        }

        Job updated = jobRepository.save(job);
        return convertToDTO(updated);
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @Transactional
    public void deleteJob(Long id, Long recruiterId) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (!job.getPostedBy().getId().equals(recruiterId)) {
            throw new UnauthorizedException("You can only delete your own jobs");
        }

        jobRepository.delete(job);
    }

    public List<JobDTO.Response> searchJobs(JobDTO.SearchRequest request) {
        List<Job> jobs = jobRepository.searchJobs(
                request.getKeyword(),
                request.getLocation(),
                request.getMinExperience(),
                request.getMaxExperience()
        );

        // Filter by skills if provided
        if (request.getSkills() != null && !request.getSkills().isEmpty()) {
            jobs = jobs.stream()
                    .filter(job -> job.getRequiredSkills().stream()
                            .anyMatch(skill -> request.getSkills().contains(skill)))
                    .collect(Collectors.toList());
        }

        return jobs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('RECRUITER')")
    public List<JobDTO.Response> getJobsByRecruiter(Long recruiterId) {
        List<Job> jobs = jobRepository.findByPostedById(recruiterId);
        return jobs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private JobDTO.Response convertToDTO(Job job) {
        JobDTO.Response dto = new JobDTO.Response();
        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setDescription(job.getDescription());
        dto.setRequiredSkills(job.getRequiredSkills());
        dto.setExperienceRequired(job.getExperienceRequired());
        dto.setLocation(job.getLocation());
        dto.setPostedDate(job.getPostedDate());

        RecruiterDTO.Response recruiterDTO = new RecruiterDTO.Response();
        recruiterDTO.setId(job.getPostedBy().getId());
        recruiterDTO.setName(job.getPostedBy().getName());
        recruiterDTO.setCompany(job.getPostedBy().getCompany());
        recruiterDTO.setEmail(job.getPostedBy().getEmail());
        dto.setPostedBy(recruiterDTO);

        return dto;
    }
}
