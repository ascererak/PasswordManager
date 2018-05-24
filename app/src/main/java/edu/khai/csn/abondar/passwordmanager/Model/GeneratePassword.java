package edu.khai.csn.abondar.passwordmanager.Model;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Random;

/**
 * Created by Alexey Bondar on 10-May-18.
 */

public class GeneratePassword {

    private String Salt = "password";
    private String[] specialCharacters = {"!","@","#","$","%","^","&","*","_","-","=","+","?"};

    public String generate(int length) throws Exception {

        MessageDigest digest = MessageDigest.getInstance("MD5");
        Salt+=System.currentTimeMillis();
        byte[] messageDigest = digest.digest(Salt.getBytes());
        BigInteger number = new BigInteger(1, messageDigest);
        String hashText = number.toString(16);
        StringBuffer buffer = new StringBuffer(hashText);
        buffer.delete(length-1, hashText.length());
        Random random = new Random();
        for (int i = 0; i < length/2-2; i++){
            int rand = random.nextInt(buffer.length());
            String randChar = specialCharacters[random.nextInt(specialCharacters.length)];
            buffer = new StringBuffer(buffer.substring(0, rand) + randChar + buffer.substring(rand+1));
            int upRand = random.nextInt(buffer.length());
            if(Character.isLetter(buffer.charAt(upRand)))
                buffer = new StringBuffer(buffer.substring(0, upRand) +
                        Character.toUpperCase(buffer.charAt(upRand)) +
                        buffer.substring(upRand+1));
        }


        return buffer.toString();
    }
}
