package com.github.sedubois;

import com.github.sedubois.factorization.presentation.FactorizationController;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component
public interface App {
  FactorizationController controller();
}
