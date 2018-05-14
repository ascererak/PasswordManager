package edu.khai.csn.abondar.passwordmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.DBHelper;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.Password;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.User;

public class RegistrationActivity extends AppCompatActivity {

    private EditText mEtName;
    private EditText mEtEmail;
    private EditText mEtPassword;
    private EditText mEtConfirmPassword;
    private EditText mEtUsername;
    private String mName;
    private String mUsername;
    private String mEmail;
    private String mPassword;
    private String mConfirmPassword;
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getControls();
    }

    private void getControls(){
        db = new DBHelper(this);
        mEtName = findViewById(R.id.name);
        mEtUsername = findViewById(R.id.username);
        mEtEmail = findViewById(R.id.email);
        mEtPassword = findViewById(R.id.password);
        mEtConfirmPassword = findViewById(R.id.confirmPassword);
    }

    public void btnSignupClick(View view){
        initialize();
        if(!validate()){
            Toast.makeText(this, "Sign up has failed", Toast.LENGTH_SHORT).show();
        }
        else{
            onSignupSuccess();
        }
    }

    private boolean validate(){
        boolean valid = true;
        if(mName.isEmpty()|| mName.length() > 32){
            mEtName.setError("Please enter valid name");
            valid = false;
        }

        if(mUsername.isEmpty()|| mUsername.length() > 32){
            mEtUsername.setError("Please enter valid username");
            valid = false;
        }

        if(mEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()){
            mEtEmail.setError("Please enter valid email");
            valid = false;
        }

        if(mPassword.isEmpty()){
            mEtPassword.setError("Please enter Password");
            valid = false;
        }

        if(mConfirmPassword.isEmpty()){
            mEtConfirmPassword.setError("Please confirm your password");
            valid = false;
        }

        if(!mPassword.equals(mConfirmPassword)){
            mEtConfirmPassword.setError("Passwords does not match");
            valid = false;
        }

        return valid;
    }

    private void onSignupSuccess(){
        Cryptography crypto = new Cryptography("passwordmanager1", this);

        try {
            mPassword = crypto.encrypt(mPassword);
        }
        catch (Exception e){
            Log.e("Exception", "Something went wrong while encrypting password");
        }

        User newUser = new User(0, mUsername, mName, mEmail, mPassword, new ArrayList<Password>());

        if(db.getUser(mUsername, mEmail, 1)){
            Toast.makeText(this, "User with such username or email has all ready been registered", Toast.LENGTH_SHORT).show();
        }

        else{
            db.addUser(newUser, new Password());
            Toast.makeText(this, "Registered", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initialize(){
        mName = mEtName.getText().toString().trim();
        mUsername = mEtUsername.getText().toString().trim();
        mEmail = mEtEmail.getText().toString().trim();
        mPassword = mEtPassword.getText().toString().trim();
        mConfirmPassword = mEtConfirmPassword.getText().toString().trim();
    }
}
