package com.management.people.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreatePersonDTO {
    @NotBlank(message = "Name cannot be empty")
    private String name;
    @NotNull(message = "Birth date cannot be null or empty!")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;
}
