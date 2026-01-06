package com.jobportal.service;

import com.jobportal.dto.AuthDTO;
import com.jobportal.entity.Candidate;
import com.jobportal.entity.Recruiter;
import com.jobportal.exception.ResourceExistsException;
import com.jobportal.repository.CandidateRepository;
import com.jobportal.repository.RecruiterRepository;
import com.jobportal.security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final CandidateRepository candidateRepository;
    private final RecruiterRepository recruiterRepository;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder,
                       CandidateRepository candidateRepository,
                       RecruiterRepository recruiterRepository,
                       UserDetailsServiceImpl userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.candidateRepository = candidateRepository;
        this.recruiterRepository = recruiterRepository;
        this.userDetailsService = userDetailsService;
    }

    public AuthDTO.LoginResponse login(AuthDTO.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Determine role and get user ID
        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        Long userId = null;
        String name = "";

        if ("ROLE_CANDIDATE".equals(role)) {
            Candidate candidate = candidateRepository.findByEmail(request.getEmail()).orElseThrow();
            userId = candidate.getId();
            name = candidate.getName();
        } else if ("ROLE_RECRUITER".equals(role)) {
            Recruiter recruiter = recruiterRepository.findByEmail(request.getEmail()).orElseThrow();
            userId = recruiter.getId();
            name = recruiter.getName();
        }

        String token = jwtService.generateToken(userDetails, userId, role);

        return new AuthDTO.LoginResponse(
                token,
                userId,
                request.getEmail(),
                name,
                role
        );
    }

    @Transactional
    public AuthDTO.LoginResponse registerCandidate(AuthDTO.RegisterRequest request) {
        if (candidateRepository.existsByEmail(request.getEmail())) {
            throw new ResourceExistsException("Candidate with email already exists");
        }

        Candidate candidate = new Candidate();
        candidate.setName(request.getName());
        candidate.setEmail(request.getEmail());
        candidate.setPassword(passwordEncoder.encode(request.getPassword()));

        Candidate savedCandidate = candidateRepository.save(candidate);

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails, savedCandidate.getId(), "ROLE_CANDIDATE");

        return new AuthDTO.LoginResponse(
                token,
                savedCandidate.getId(),
                savedCandidate.getEmail(),
                savedCandidate.getName(),
                "ROLE_CANDIDATE"
        );
    }

    @Transactional
    public AuthDTO.LoginResponse registerRecruiter(AuthDTO.RegisterRequest request) {
        if (recruiterRepository.existsByEmail(request.getEmail())) {
            throw new ResourceExistsException("Recruiter with email already exists");
        }

        Recruiter recruiter = new Recruiter();
        recruiter.setName(request.getName());
        recruiter.setCompany(request.getCompany());
        recruiter.setEmail(request.getEmail());
        recruiter.setPassword(passwordEncoder.encode(request.getPassword()));

        Recruiter savedRecruiter = recruiterRepository.save(recruiter);

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails, savedRecruiter.getId(), "ROLE_RECRUITER");

        return new AuthDTO.LoginResponse(
                token,
                savedRecruiter.getId(),
                savedRecruiter.getEmail(),
                savedRecruiter.getName(),
                "ROLE_RECRUITER"
        );
    }
}
