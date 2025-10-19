package com.beyt.jdq.jpa.query;

import com.beyt.jdq.core.helper.QueryHelper;
import com.beyt.jdq.core.model.enums.JoinType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DynamicSpecificationTest {

    @Test
    void getFieldName() {
        Assertions.assertEquals("name", QueryHelper.getFieldName("asd.asd<asd>name"));
    }

    @Test
    void getFieldJoins() {
        List<Pair<String, JoinType>> fieldJoins = new ArrayList<>();
        fieldJoins.add(Pair.of("asd2fg", JoinType.INNER));
        fieldJoins.add(Pair.of("asdhj", JoinType.LEFT));
        fieldJoins.add(Pair.of("asdasda", JoinType.RIGHT));

        assertEquals(fieldJoins, QueryHelper.getFieldJoins("asd2fg.asdhj<asdasda>name"));
    }
}
