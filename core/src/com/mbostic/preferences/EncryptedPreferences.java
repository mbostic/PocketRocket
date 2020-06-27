package com.mbostic.preferences;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.mbostic.rocket.RocketMain;

import java.awt.Color;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;


public class EncryptedPreferences extends AbstractPreferences{

    private SecretKey key;
    private BASE64Encoder base64encoder;
    private BASE64Decoder base64decoder;
    private Preferences prefs;

    public EncryptedPreferences(){
        // PREFS
        prefs = Gdx.app.getPreferences("prefs");
        base64encoder = new BASE64Encoder();
        base64decoder = new BASE64Decoder();
        try {
            Gdx.app.debug("EncryptedPreferences", "email: "+ RocketMain.adsController.getAccountName());
            //naredi key na podlagi google racuna
            DESKeySpec keySpec = new DESKeySpec(RocketMain.adsController.getAccountName().getBytes("UTF8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            key = keyFactory.generateSecret(keySpec);
        }catch(Exception e){
            Gdx.app.debug("EncryptedPreferences", "Error :" + e.getMessage());
        }
    }


    public String encrypt(String input) {

        try{
            byte[] cleartext = input.getBytes("UTF8");
            Cipher cipher = Cipher.getInstance("DES"); // cipher is not thread safe
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return base64encoder.encode(cipher.doFinal(cleartext));

        }catch(Exception e){
        Gdx.app.debug("EncryptedPreferences", "Error :" + e.getMessage());
        return "error!";
    }
    }

    public String decrypt(String encryptedInput) {
        try {

            byte[] encryptedPwdBytes = base64decoder.decodeBuffer(encryptedInput);

            Cipher cipher = Cipher.getInstance("DES");// cipher is not thread safe
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(encryptedPwdBytes), "utf-8");

        } catch (Exception e) {
            Gdx.app.debug("EncryptedPreferences", "Error :" + e.getMessage());
            return "error!";

        }
    }

    public void writeString(String name, String value){

        prefs.putString(encrypt(name), encrypt(value));
        prefs.flush();
    }

    public String readString(String name){

        return decrypt(prefs.getString(encrypt(name)));
    }

    public void writeBoolean(String name, boolean value){

        String v = value ? "dkam%#dw23e" : "r4eae%4wr3";

        prefs.putString(encrypt(name), v);
        prefs.flush();
    }

    public boolean readBoolean(String name){

        return prefs.getString(encrypt(name)).equals("dkam%#dw23e");
    }

    public void writeInt(String name, int value){
        prefs.putString(encrypt(name), encrypt(""+value));
        prefs.flush();

    }

    public int readInt(String name){

        return Integer.parseInt(decrypt(prefs.getString(encrypt(name))));
    }

    public boolean has(String value){

        return prefs.contains(encrypt(value));
    }

}
