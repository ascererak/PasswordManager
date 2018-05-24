package edu.khai.csn.abondar.passwordmanager.Presenter;

import android.support.annotation.NonNull;
import android.util.Log;

import edu.khai.csn.abondar.passwordmanager.Model.Cryptography;
import edu.khai.csn.abondar.passwordmanager.View.LoginActView;

/**
 * Created by Alexey Bondar on 14-May-18.
 */

public class CryptoPresenter {

    private Cryptography crypto = new Cryptography("passwordmanager1");
    private LoginActView view;

    public CryptoPresenter(@NonNull LoginActView view) {
        this.view = view;
    }

    public void encrypt() {

        try {
            view.setPassword(crypto.encrypt(view.getPassword()));
        } catch (Exception e) {
            Log.e("ne nice", "exception");
        }
    }

    public void decrypt() {
        try {
            view.setPassword(crypto.decrypt(view.getPassword()));
        } catch (Exception e) {
            Log.e("ne nice", "exception");
        }
    }
}
