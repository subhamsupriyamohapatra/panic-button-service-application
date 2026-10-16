package com.emergency.panicbutton.family.dto;

import com.emergency.panicbutton.family.enums.RelationshipType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FamilyMemberCreate {

    @NotBlank
    @Size(min = 2, max = 255)
    private String name;

    @NotBlank
    @Size(min = 6, max = 50)
    @JsonProperty("phone_number")
    private String phoneNumber;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 128)
    private String password;

    @NotNull
    private RelationshipType relationship;
}
