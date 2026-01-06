package com.jobportal.service;

import com.jobportal.entity.Candidate;
import com.jobportal.entity.Recruiter;
import com.jobportal.repository.CandidateRepository;
import com.jobportal.repository.RecruiterRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CandidateRepository candidateRepository;
    private final RecruiterRepository recruiterRepository;

    public UserDetailsServiceImpl(CandidateRepository candidateRepository,
                                  RecruiterRepository recruiterRepository) {
        this.candidateRepository = candidateRepository;
        this.recruiterRepository = recruiterRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Candidate candidate = candidateRepository.findByEmail(email).orElse(null);
        if (candidate != null) {
            return new User(
                    candidate.getEmail(),
                    candidate.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_CANDIDATE"))
            );
        }

        Recruiter recruiter = recruiterRepository.findByEmail(email).orElse(null);
        if (recruiter != null) {
            return new User(
                    recruiter.getEmail(),
                    recruiter.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_RECRUITER"))
            );
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}
