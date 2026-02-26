package com.jitendra.campusconnect_aiops.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "job_listings")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String role;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String location;

    private Double packageAmount;

    @ManyToOne
    @JoinColumn(name = "posted_by")
    private User postedBy;
}
