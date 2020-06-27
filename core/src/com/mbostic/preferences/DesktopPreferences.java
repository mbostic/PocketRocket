package com.mbostic.preferences;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class DesktopPreferences extends AbstractPreferences{

    private Preferences prefs;

    public DesktopPreferences(){
        prefs = Gdx.app.getPreferences("prefs");
    }


    public void writeString(String name, String value){
        prefs.putString(name, value);
        prefs.flush();
    }

    public String readString(String name){

        return prefs.getString(name);
    }

    public void writeBoolean(String name, boolean value){
        prefs.putBoolean(name, value);
        prefs.flush();
    }

    public boolean readBoolean(String name){

        return prefs.getBoolean(name);
    }

    public void writeInt(String name, int value){
        prefs.putInteger(name, value);
        prefs.flush();

    }

    public int readInt(String name){

        return prefs.getInteger(name);
    }

    public boolean has(String value){

        return prefs.contains(value);
    }

}
