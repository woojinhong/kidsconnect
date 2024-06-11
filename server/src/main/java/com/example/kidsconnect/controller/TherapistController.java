package com.example.kidsconnect.controller;

import com.example.kidsconnect.domain.Therapist;
import com.example.kidsconnect.dto.LoginDto;
import com.example.kidsconnect.dto.TherapistInfoDto;
import com.example.kidsconnect.dto.TherapistSignUpDto;
import com.example.kidsconnect.service.TherapistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/therapist")
public class TherapistController {
    @Autowired
    TherapistService therapistService;



    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody TherapistSignUpDto therapistSignUpDto){

        return therapistService.signUp(therapistSignUpDto);
    }


}