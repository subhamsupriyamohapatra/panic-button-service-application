package com.emergency.panicbutton.family.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FamilyMemberUpdate {

    @Size(min = 2, max = 255)
    private String name;

    @Size(min = 6, max = 50)
    @JsonProperty("phone_number")
    private String phoneNumber;

    @Email
    private String email;

    @Size(min = 6, max = 128)
    private String password;

    private String status;
}
