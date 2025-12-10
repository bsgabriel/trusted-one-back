package com.bsg.trustedone.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupFormDto {

    @NotBlank(message = "Group name is required")
    private String name;

    private String description;

    @Builder.Default
    private List<Long> partners = new ArrayList<>();
}
