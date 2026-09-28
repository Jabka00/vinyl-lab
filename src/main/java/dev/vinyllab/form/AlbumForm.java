package dev.vinyllab.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlbumForm {

  @NotBlank
  @Size(max = 160)
  private String title;

  @NotNull
  @Min(1900)
  @Max(2035)
  private Integer releaseYear;

  @Size(max = 80)
  private String label;

  @NotNull
  private Long artistId;

  @NotEmpty
  private Set<Long> genreIds = new LinkedHashSet<>();
}
