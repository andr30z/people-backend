package com.management.people.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CreateAddressDTO {

  @NotBlank(message = "Public place cannot be empty!")
  private String publicPlace; // logradouro

  @NotBlank(message = "CEP cannot be empty")
  @Pattern(regexp = "[\\d]{8}", message = "Invalid CEP format!")
  private String cep;

  private Integer number;

  @NotBlank(message = "City cannot be empty!")
  private String city;
}
