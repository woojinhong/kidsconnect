package com.example.kidsconnect.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;



import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@DynamicInsert

public class TherapistInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String bio;
    private String content;


    private Boolean identityCheck;
    private Boolean crimeCheck;
    @ElementCollection
    @CollectionTable(name = "therapist_certificate", joinColumns = @JoinColumn(name = "therapist_info_id"))
    @Column(name = "certificate")
    private List<String> certificate =new ArrayList<>();
    @ElementCollection
    @CollectionTable(name = "therapist_age_range", joinColumns = @JoinColumn(name = "therapist_info_id"))
    @Column(name = "age_range")
    private List<String> ageRange =new ArrayList<>();
    @Lob
    private byte[] imageFile;
    private int viewCnt;

    private LocalDateTime inDate;

    private LocalDateTime upDate;


    @OneToOne
    @JoinColumn(name = "therapist_id")
    private Therapist therapist;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "therapist_info_id")
    private List<TherapistEducation> education;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "therapist_info_id")
    private List<TherapistExperience> experience;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "therapist_info_id")
    private List<TherapistInfoSymptom> therapistInfoSymptom;

    public void addTherapistExperience(TherapistExperience therapistExperience){
        if (this.experience == null) {
            this.experience = new ArrayList<>();
        }
        this.experience.add(therapistExperience);
    }
    public void addTherapistEducation(TherapistEducation therapistEducation){
        if (this.education == null) {
            this.education = new ArrayList<>();
        }

        this.education.add(therapistEducation);
    }

    public void addTherapistInfoSymptom(TherapistInfoSymptom therapistInfoSymptom){
        if (this.therapistInfoSymptom == null) {
            this.therapistInfoSymptom = new ArrayList<>();
        }

        this.therapistInfoSymptom.add(therapistInfoSymptom);
    }


    @PrePersist
    protected void onCreate() {
        if (this.inDate == null) {
            this.inDate = LocalDateTime.now();
        }
        if (this.upDate == null) {
            this.upDate = LocalDateTime.now();
        }

    }

    @PreUpdate
    protected void onUpdate() {
        this.upDate = LocalDateTime.now();
    }

}
