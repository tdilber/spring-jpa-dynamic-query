package com.beyt.jdq.core.util;

import org.springframework.util.Assert;

import java.util.Locale;

/**
 * Created by tdilber at 14-Sep-19
 */
public class StringUtil {

    public static String lowerFirstLetter(String simpleName) {
        Assert.hasText(simpleName, "must have text!");
        return simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
    }

    public static String uppercaseFirstLetter(String simpleName) {
        Assert.hasText(simpleName, "must have text!");
        return simpleName.substring(0, 1).toUpperCase(Locale.ENGLISH) + simpleName.substring(1);
    }
}
