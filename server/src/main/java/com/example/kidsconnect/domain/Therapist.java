package com.example.kidsconnect.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@DynamicInsert
@ToString
public class Therapist implements Loginable {
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
    private String role;


    private LocalDateTime inDate;

    private LocalDateTime upDate;


    @OneToMany(mappedBy = "therapist", cascade = CascadeType.ALL)
    private List<Enrol> enrol;

    @OneToMany(mappedBy = "therapist", cascade = CascadeType.ALL)
    private List<Reservation> reservation;


    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "therapist_id")
    private TherapistInfo therapistInfo;

    @OneToMany(mappedBy = "therapist", cascade = CascadeType.ALL)
    private List<TherapistReview> therapistReview;


    public void addTherapistInfo(TherapistInfo therapistInfo){
        this.therapistInfo = therapistInfo;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String email() {
        return email;
    }

    @Override
    public String password() {
        return password;
    }

    @Override
    public String role() {
        return role;
    }
}


