package com.beyt.jdq.core.helper;

import com.beyt.jdq.core.model.enums.JoinType;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class QueryHelper {
    public static String getFieldName(String key) {
        String[] splitedKey = key.split(">|<|\\.");
        return splitedKey[splitedKey.length - 1];
    }

    public static List<Pair<String, JoinType>> getFieldJoins(String key) {
        List<Pair<String, JoinType>> fieldJoins = new ArrayList<>();

        String subKey = key;
        JoinType joinType = null;
        int index = -1;


        while (subKey.chars().anyMatch(c -> Arrays.stream(JoinType.values()).anyMatch(j -> j.getSeparator().charValue() == c))) {

            for (JoinType value : JoinType.values()) {
                int indexOf = subKey.indexOf(value.getSeparator());
                if (indexOf > -1 && (index == -1 || indexOf < index)) {
                    index = indexOf;
                    joinType = value;
                }
            }

            if (Objects.nonNull(joinType)) {
                fieldJoins.add(Pair.of(subKey.substring(0, index), joinType));
                subKey = subKey.substring(index + 1);
            }

            index = -1;
            joinType = null;
        }

        return fieldJoins;
    }
}
