package com.emergency.panicbutton.family.dto;

import com.emergency.panicbutton.family.enums.RelationshipType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMappingResponse {

    private UUID id;

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("family_member_id")
    private UUID familyMemberId;

    private RelationshipType relationship;
    private String status;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @JsonProperty("family_member")
    private FamilyMemberResponse familyMember;
}
