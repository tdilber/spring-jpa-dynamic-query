package com.beyt.jdq.core.util.field.helper;

import java.util.Random;

/**
 * Created by tdilber at 11/17/2020
 */
public interface IFieldHelper<T> {
    static final Random random = new Random();

    T fillRandom();

    T fillValue(String value);

    String createGeneratorCode(String value);
}
