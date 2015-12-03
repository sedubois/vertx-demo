package com.github.sedubois.factorization.service;

import java.util.ArrayList;
import java.util.List;

public class Factorizer {

  public static List<Long> factorize(long number) {
    long n = number;
    List<Long> factors = new ArrayList<>();
    for (long i = 2; i <= n; i++) {
      while (n % i == 0) {
        factors.add(i);
        n /= i;
      }
    }
    return factors;
  }
}
