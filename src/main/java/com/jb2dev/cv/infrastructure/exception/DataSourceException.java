package com.jb2dev.cv.infrastructure.exception;


public class DataSourceException extends InfrastructureException {

    private final String dataSource;

    public DataSourceException(String dataSource, String message) {
        super(String.format("Data source '%s' error: %s", dataSource, message));
        this.dataSource = dataSource;
    }

    public DataSourceException(String dataSource, String message, Throwable cause) {
        super(String.format("Data source '%s' error: %s", dataSource, message), cause);
        this.dataSource = dataSource;
    }

    public String getDataSource() {
        return dataSource;
    }
}
