package com.caiobraz.artista.service.util;

import org.springframework.data.domain.ExampleMatcher;

/**
 * Classe utilitária (factory) para criar um matcher default QBE do Spring.
 */
public class ExampleMatcherUtil {

    private ExampleMatcherUtil() {}

    public static ExampleMatcher defaultMatcher() {
        return ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
    }
}
