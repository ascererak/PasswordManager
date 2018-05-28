package edu.khai.csn.abondar.passwordmanager.View;

import android.content.Context;

import java.util.ArrayList;

import edu.khai.csn.abondar.passwordmanager.Model.Entities.Password;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.User;

/**
 * Created by Alexey Bondar on 28-May-18.
 */

public interface XmlView {
    ArrayList<Password> getmPasswords();
    User getmUser();
    String getmDirectory();
    Context getmContext();
    String getmPath();
    void setPassToImport(ArrayList<Password> passToImport);
}
