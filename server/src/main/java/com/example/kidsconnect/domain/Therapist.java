package com.example.kidsconnect.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@DynamicInsert
@ToString
public class Therapist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNum;
    private String postalCode;
    private String addressDetail;
    private String address;
    private Character gender;
    private boolean freelancer;
    private boolean status;
    private Date dateOfBirth;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime inDate;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime upDate;


    @OneToMany(mappedBy = "therapist", cascade = CascadeType.ALL)
    private List<Enrol> enrol;

    @OneToMany(mappedBy = "therapist", cascade = CascadeType.ALL)
    private List<Reservation> reservation;

    @OneToMany(mappedBy = "therapist", cascade = CascadeType.ALL)
    private List<TherapistExperience> therapistExperience;

    @OneToMany(mappedBy = "therapist", cascade = CascadeType.ALL)
    private List<TherapistInfo> therapistInfo;

    @OneToMany(mappedBy = "therapist", cascade = CascadeType.ALL)
    private List<TherapistReview> therapistReview;

}
