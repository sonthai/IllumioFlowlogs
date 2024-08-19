package com.illumio.flow_logs;

public class FlowLogsException extends RuntimeException{
    public FlowLogsException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public FlowLogsException(String message) {
        super(message);
    }
}
