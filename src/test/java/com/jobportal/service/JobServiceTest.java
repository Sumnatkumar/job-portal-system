package com.jobportal.service;

import com.jobportal.dto.JobDTO;
import com.jobportal.entity.Job;
import com.jobportal.entity.Recruiter;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.exception.UnauthorizedException;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.RecruiterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private RecruiterRepository recruiterRepository;

    @InjectMocks
    private JobService jobService;

    private Job job;
    private Recruiter recruiter;
    private Long jobId = 1L;
    private Long recruiterId = 1L;

    @BeforeEach
    void setUp() {
        Set<String> skills = new HashSet<>();
        skills.add("Java");
        skills.add("Spring Boot");

        recruiter = new Recruiter();
        recruiter.setId(recruiterId);
        recruiter.setName("Tech Corp");
        recruiter.setEmail("hr@techcorp.com");

        job = new Job();
        job.setId(jobId);
        job.setTitle("Senior Java Developer");
        job.setDescription("Looking for senior Java developer");
        job.setRequiredSkills(skills);
        job.setExperienceRequired(5);
        job.setLocation("Remote");
        job.setPostedBy(recruiter);
    }

    @Test
    void createJob_ShouldCreateAndReturnJob() {
        JobDTO.CreateRequest request = new JobDTO.CreateRequest();
        request.setTitle("Java Developer");
        request.setDescription("Java developer needed");
        request.setExperienceRequired(3);
        request.setLocation("Remote");

        when(recruiterRepository.findById(recruiterId)).thenReturn(Optional.of(recruiter));
        when(jobRepository.save(any(Job.class))).thenReturn(job);

        JobDTO.Response result = jobService.createJob(request, recruiterId);

        assertNotNull(result);
        verify(recruiterRepository, times(1)).findById(recruiterId);
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    void getJob_ShouldReturnJob_WhenJobExists() {
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        JobDTO.Response result = jobService.getJob(jobId);

        assertNotNull(result);
        assertEquals(jobId, result.getId());
        verify(jobRepository, times(1)).findById(jobId);
    }

    @Test
    void getJob_ShouldThrowException_WhenJobNotFound() {
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> jobService.getJob(jobId));

        verify(jobRepository, times(1)).findById(jobId);
    }

    @Test
    void updateJob_ShouldUpdateJob_WhenAuthorized() {
        JobDTO.UpdateRequest request = new JobDTO.UpdateRequest();
        request.setTitle("Updated Title");

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(jobRepository.save(any(Job.class))).thenReturn(job);

        JobDTO.Response result = jobService.updateJob(jobId, request, recruiterId);

        assertNotNull(result);
        verify(jobRepository, times(1)).findById(jobId);
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    void updateJob_ShouldThrowException_WhenUnauthorized() {
        Long otherRecruiterId = 2L;
        JobDTO.UpdateRequest request = new JobDTO.UpdateRequest();

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        assertThrows(UnauthorizedException.class,
                () -> jobService.updateJob(jobId, request, otherRecruiterId));

        verify(jobRepository, times(1)).findById(jobId);
        verify(jobRepository, never()).save(any(Job.class));
    }
}
