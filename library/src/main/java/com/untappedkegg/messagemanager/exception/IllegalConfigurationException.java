package com.untappedkegg.messagemanager.exception;

/**
 * Created by UntappedKegg on 1/17/15.
 */
public class IllegalConfigurationException extends IllegalStateException {
    private static final String DEFAULT_MESSAGE = "Incorrect initialization sequence";

    public IllegalConfigurationException() {
        super(DEFAULT_MESSAGE);
    }

    public IllegalConfigurationException(String detailMessage) {
        super(String.format("%s:\n%s", DEFAULT_MESSAGE, detailMessage));
    }
}
