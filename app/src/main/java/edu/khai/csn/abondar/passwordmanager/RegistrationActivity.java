package edu.khai.csn.abondar.passwordmanager;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Pattern;

import edu.khai.csn.abondar.passwordmanager.Model.Entities.DBHelper;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.Password;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.User;

public class RegistrationActivity extends AppCompatActivity {
    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private EditText etUsername;
    private Button btnSignup;

    private String name;
    private String username;
    private String email;
    private String password;
    private String confirmPassword;

    private ArrayList<User> users;

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
        etName = findViewById(R.id.name);
        etUsername = findViewById(R.id.username);
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        etConfirmPassword = findViewById(R.id.confirmPassword);
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
            etName.setError("Please enter valid name");
            valid = false;
        }

        if(username.isEmpty()|| username.length() > 32){
            etUsername.setError("Please enter valid username");
            valid = false;
        }

        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError("Please enter valid email");
            valid = false;
        }

        if(password.isEmpty()){
            etPassword.setError("Please enter Password");
            valid = false;
        }

        if(confirmPassword.isEmpty()){
            etConfirmPassword.setError("Please confirm your password");
            valid = false;
        }

        if(!password.equals(confirmPassword)){
            etConfirmPassword.setError("Passwords does not match");
            valid = false;
        }

        return valid;
    }

    private void onSignupSuccess(){
        User newUser = new User(0, username,name, email, password, new ArrayList<Password>());
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
        name = etName.getText().toString().trim();
        username = etUsername.getText().toString().trim();
        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        confirmPassword = etConfirmPassword.getText().toString().trim();
    }
}
