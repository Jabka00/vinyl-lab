package dev.vinyllab.util;

public final class Ukrainian {

  private Ukrainian() {
  }

  public static String records(long count) {
    return count + " " + plural(count, "платівка", "платівки", "платівок");
  }

  public static String ratings(long count) {
    return count + " " + plural(count, "оцінка", "оцінки", "оцінок");
  }

  private static String plural(long count, String one, String few, String many) {
    long n = Math.abs(count) % 100;
    long n1 = n % 10;
    if (n > 10 && n < 20) {
      return many;
    }
    if (n1 == 1) {
      return one;
    }
    if (n1 >= 2 && n1 <= 4) {
      return few;
    }
    return many;
  }
}
