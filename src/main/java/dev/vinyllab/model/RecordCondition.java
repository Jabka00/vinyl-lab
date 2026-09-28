package dev.vinyllab.model;

public enum RecordCondition {
  MINT("M", "Ідеальний"),
  NEAR_MINT("NM", "Майже ідеальний"),
  VERY_GOOD_PLUS("VG+", "Дуже хороший+"),
  VERY_GOOD("VG", "Дуже хороший"),
  GOOD("G", "Хороший"),
  FAIR("F", "Задовільний"),
  POOR("P", "Поганий");

  private final String code;
  private final String label;

  RecordCondition(String code, String label) {
    this.code = code;
    this.label = label;
  }

  public String getCode() {
    return code;
  }

  public String getLabel() {
    return label;
  }

  public String display() {
    return label + " (" + code + ")";
  }
}
