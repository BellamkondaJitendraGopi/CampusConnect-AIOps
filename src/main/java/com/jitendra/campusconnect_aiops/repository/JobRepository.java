package com.jitendra.campusconnect_aiops.repository;

import com.jitendra.campusconnect_aiops.model.JobListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<JobListing, Long> {
    // NO controller logic here
}
