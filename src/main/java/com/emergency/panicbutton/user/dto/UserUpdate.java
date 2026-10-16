package com.emergency.panicbutton.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdate {

    @Size(min = 2, max = 255)
    private String name;

    @Email
    private String email;

    @Size(min = 6, max = 50)
    @JsonProperty("phone_number")
    private String phoneNumber;

    @Size(min = 8, max = 128)
    private String password;

    private String status;
}
