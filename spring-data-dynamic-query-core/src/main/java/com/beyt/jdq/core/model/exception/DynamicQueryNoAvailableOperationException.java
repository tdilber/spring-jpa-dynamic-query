package com.beyt.jdq.core.model.exception;


/**
 * Created by tdilber at 24-Aug-19
 */
public class DynamicQueryNoAvailableOperationException extends RuntimeException {

    public DynamicQueryNoAvailableOperationException(String errorMessage) {
        super(errorMessage);
    }

    public DynamicQueryNoAvailableOperationException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
