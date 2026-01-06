package com.jobportal.repository;

import com.jobportal.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByPostedById(Long recruiterId);

    @Query("SELECT j FROM Job j WHERE " +
            "(:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:minExperience IS NULL OR j.experienceRequired >= :minExperience) AND " +
            "(:maxExperience IS NULL OR j.experienceRequired <= :maxExperience)")
    List<Job> searchJobs(@Param("keyword") String keyword,
                         @Param("location") String location,
                         @Param("minExperience") Integer minExperience,
                         @Param("maxExperience") Integer maxExperience);

    @Query("SELECT j FROM Job j JOIN j.requiredSkills s WHERE s IN :skills")
    List<Job> findBySkills(@Param("skills") List<String> skills);
}
