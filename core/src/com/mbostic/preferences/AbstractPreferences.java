package com.mbostic.preferences;

public abstract class AbstractPreferences {

    public abstract void writeString(String name, String value);

    public abstract String readString(String name);

    public abstract void writeBoolean(String name, boolean value);

    public abstract boolean readBoolean(String name);
    public abstract void writeInt(String name, int value);

    public abstract int readInt(String name);

    public abstract boolean has(String value);
}
