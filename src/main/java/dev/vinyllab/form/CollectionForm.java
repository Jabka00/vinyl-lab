package dev.vinyllab.form;

import dev.vinyllab.model.RecordCondition;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollectionForm {

  @NotNull
  private Long albumId;

  @NotNull
  private RecordCondition condition;

  @PastOrPresent
  private LocalDate acquiredOn;

  @Size(max = 1000)
  private String notes;
}
