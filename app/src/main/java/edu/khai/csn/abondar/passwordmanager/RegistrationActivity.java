package edu.khai.csn.abondar.passwordmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Pattern;

import edu.khai.csn.abondar.passwordmanager.Model.Entities.DBHelper;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.Password;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.User;

public class RegistrationActivity extends AppCompatActivity {
    private EditText mEtName;
    private EditText mEtEmail;
    private EditText mEtPassword;
    private EditText mEtConfirmPassword;
    private EditText mEtUsername;
    private FrameLayout btnSignup;

    private String name;
    private String username;
    private String email;
    private String password;
    private String confirmPassword;

    //private ArrayList<User> users;

    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Bundle extras = getIntent().getExtras();
        //if(extras!=null){
        //    users = (ArrayList<User>) extras.getSerializable("users");
        //}

        getControls();
    }

    private void getControls(){
        db = new DBHelper(this);
        mEtName = findViewById(R.id.name);
        mEtUsername = findViewById(R.id.username);
        mEtEmail = findViewById(R.id.email);
        mEtPassword = findViewById(R.id.password);
        mEtConfirmPassword = findViewById(R.id.confirmPassword);
        btnSignup = findViewById(R.id.btnSignUpRS);
    }

    public void btnSignup_click(View view){
        inizialize();
        if(!validate()){
            Toast.makeText(this, "Sign up has failed", Toast.LENGTH_SHORT).show();
        }
        else{
            onSignupSuccess();
        }
    }

    private boolean validate(){
        boolean valid = true;
        if(name.isEmpty()|| name.length() > 32){
            mEtName.setError("Please enter valid name");
            valid = false;
        }

        if(username.isEmpty()|| username.length() > 32){
            mEtUsername.setError("Please enter valid username");
            valid = false;
        }

        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mEtEmail.setError("Please enter valid email");
            valid = false;
        }

        if(password.isEmpty()){
            mEtPassword.setError("Please enter Password");
            valid = false;
        }

        if(confirmPassword.isEmpty()){
            mEtConfirmPassword.setError("Please confirm your password");
            valid = false;
        }

        if(!password.equals(confirmPassword)){
            mEtConfirmPassword.setError("Passwords does not match");
            valid = false;
        }

        return valid;
    }

    private void onSignupSuccess(){
        Cryptography crypto = new Cryptography("passwordmanager1");

        try {
            password = crypto.encrypt(password);
        }catch (Exception e){}

        User newUser = new User(0, username, name, email, password, new ArrayList<Password>());
        if(db.getUser(username, email, 1)){
            Toast.makeText(this, "User with such username or email has all ready been registered", Toast.LENGTH_SHORT).show();
        }
        else{
        //users.add(newUser);
            db.addUser(newUser, new Password());
            Toast.makeText(this, "Registered", Toast.LENGTH_SHORT).show();
        //Bundle extras = new Bundle();
        //Intent intent = new Intent();
        //extras.putSerializable("users1", users);
       // intent.putExtras(extras);
        //setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    private void inizialize(){
        name = mEtName.getText().toString().trim();
        username = mEtUsername.getText().toString().trim();
        email = mEtEmail.getText().toString().trim();
        password = mEtPassword.getText().toString().trim();
        confirmPassword = mEtConfirmPassword.getText().toString().trim();
    }
}
