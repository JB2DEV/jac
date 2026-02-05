package com.jb2dev.cv.domain.exception;

public class InvalidLanguageException extends DomainException {

    private final String invalidCode;

    public InvalidLanguageException(String invalidCode) {
        super(String.format("Invalid language code: '%s'. Supported codes are: es_ES, en_EN", invalidCode));
        this.invalidCode = invalidCode;
    }

    public String getInvalidCode() {
        return invalidCode;
    }
}
