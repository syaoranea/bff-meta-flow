package com.metasflow.bff.domain.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequest {
    private String name;
    
    @JsonProperty("goal")
    private String suggestion;
    private Boolean accessFirst;
    private String profession;
}
