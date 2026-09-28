package dev.vinyllab.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterForm {

  @NotBlank
  @Pattern(regexp = "^[a-zA-Z0-9_]{3,30}$", message = "Від 3 до 30 символів: латиниця, цифри або підкреслення")
  private String username;

  @NotBlank
  @Email
  @Size(max = 160)
  private String email;

  @NotBlank
  @Size(min = 8, max = 72)
  private String password;

  @NotBlank
  private String confirmPassword;

  public boolean passwordsMatch() {
    return password != null && password.equals(confirmPassword);
  }
}
