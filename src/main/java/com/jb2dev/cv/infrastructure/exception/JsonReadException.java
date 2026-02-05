package com.jb2dev.cv.infrastructure.exception;

public class JsonReadException extends DataSourceException {

    private final String filePath;

    public JsonReadException(String filePath, String message) {
        super("JSON File", message);
        this.filePath = filePath;
    }

    public JsonReadException(String filePath, String message, Throwable cause) {
        super("JSON File", message, cause);
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    @Override
    public String getMessage() {
        return String.format("Failed to read JSON file '%s': %s", filePath, super.getMessage());
    }
}
