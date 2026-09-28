package dev.vinyllab.util;

public final class Text {

  private Text() {
  }

  public static String blankToNull(String value) {
    if (value == null) {
      return null;
    }
    String trimmed = value.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }
}
