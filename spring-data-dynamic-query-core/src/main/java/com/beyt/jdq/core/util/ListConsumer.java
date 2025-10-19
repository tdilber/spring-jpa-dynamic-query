package com.beyt.jdq.core.util;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by tdilber at 7/17/2020
 */
public interface ListConsumer<Entity> extends Consumer<List<Entity>> {
    void accept(List<Entity> entityList);
}
