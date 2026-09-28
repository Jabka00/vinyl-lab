package dev.vinyllab.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArtistForm {

  @NotBlank
  @Size(max = 120)
  private String name;

  @NotBlank
  @Size(max = 80)
  private String country;

  @Size(max = 2000)
  private String biography;
}
