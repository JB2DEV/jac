package com.jb2dev.cv.application.exception;

public class UseCaseExecutionException extends ApplicationException {

    private final String useCaseName;

    public UseCaseExecutionException(String useCaseName, String message) {
        super(String.format("Use case '%s' failed: %s", useCaseName, message));
        this.useCaseName = useCaseName;
    }

    public UseCaseExecutionException(String useCaseName, String message, Throwable cause) {
        super(String.format("Use case '%s' failed: %s", useCaseName, message), cause);
        this.useCaseName = useCaseName;
    }

    public String getUseCaseName() {
        return useCaseName;
    }
}
