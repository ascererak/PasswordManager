package edu.khai.csn.abondar.passwordmanager.Model;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class IOHelper {

    public static void writeToFile(Context context, String fileName, String str) {
        try {
            File file = new File(fileName);
            FileOutputStream fos = new FileOutputStream(file);

            fos.write(str.getBytes(), 0, str.length());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
