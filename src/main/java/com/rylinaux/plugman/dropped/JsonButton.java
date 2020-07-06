package com.rylinaux.plugman.dropped;

import java.util.UUID;

public enum JsonButton 
{
    LOAD (UUID.randomUUID ().toString (), null),
    CANCEL (UUID.randomUUID ().toString (), null);

    private String uuid;
    private Runnable runnable;

    JsonButton(String toString, Runnable runnable) {
        uuid = toString;
        this.runnable = runnable;
    }

    public Runnable runnable() {
        return runnable;
    }

    public String getUUID(Runnable runnable) {
        if (runnable != null) this.runnable = runnable;
        return uuid;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }
}
