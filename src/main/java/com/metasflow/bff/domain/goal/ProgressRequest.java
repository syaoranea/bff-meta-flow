package com.metasflow.bff.domain.goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgressRequest {
    private String goalId;
    private String subgoalId;
    private Integer xp;
}
