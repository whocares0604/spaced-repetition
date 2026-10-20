package io.github.whocares0604.spacedrepetition.api;

public abstract class NotFoundException extends RuntimeException {

    protected NotFoundException(String message) {
        super(message);
    }
}
