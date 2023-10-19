package com.cq.exchange.task;

public interface Grabber extends Runnable {

    default void close() {
    }
}
