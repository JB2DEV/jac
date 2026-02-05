package com.jb2dev.cv.domain.exception;

public class InvalidAggregateStateException extends DomainException {

    private final String aggregateType;
    private final String reason;

    public InvalidAggregateStateException(String aggregateType, String reason) {
        super(String.format("Invalid state for %s: %s", aggregateType, reason));
        this.aggregateType = aggregateType;
        this.reason = reason;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public String getReason() {
        return reason;
    }
}
