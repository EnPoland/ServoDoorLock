package com.example.philipgo.servodoorlock;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PasswordCheck {


    private static MessageDigest md;
    SharedPreferences preferences;


    public boolean checkPass(String text, Context context) throws IOException {

       preferences = context.getSharedPreferences(MainActivity.passwordHash,0);
       String textcryp = preferences.getString(MainActivity.passwordHash,"");


        String textcry = cryptWithMD5(text);
        System.out.println(textcry);
        assert textcry != null;
        if (textcry.equals(textcryp))return true;
        else return false;

    }

    public static String cryptWithMD5(String pass) {
        try {
            md = MessageDigest.getInstance("MD5");

            byte[] passBytes = pass.getBytes();
            md.reset();
            byte[] digested = md.digest(passBytes);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < digested.length; i++) {
                sb.append(Integer.toHexString(0xff & digested[i]));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(PasswordCheck.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
