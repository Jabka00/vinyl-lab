package dev.vinyllab.model;

public enum Role {
  USER("Користувач"),
  ADMIN("Адміністратор");

  private final String label;

  Role(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public String authority() {
    return "ROLE_" + name();
  }
}
