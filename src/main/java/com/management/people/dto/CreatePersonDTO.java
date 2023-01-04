package com.management.people.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreatePersonDTO {
    @NotBlank(message = "Name cannot be empty")
    private String name;
    @NotBlank(message = "CEP cannot be empty")
    @Pattern(regexp = "[\\d]{8}", message = "Invalid CEP format!")
    private String cep;
    @NotNull(message = "Birth date cannot be null or empty!")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;
}
