package com.icloud.itfukui0922.strategy;

public class FlagManagement {
    /* 0日目の挨拶 */
    boolean greeting = false;

    public boolean isGreeting() {
        return greeting;
    }

    public void setGreeting(boolean greeting) {
        this.greeting = greeting;
    }
}
