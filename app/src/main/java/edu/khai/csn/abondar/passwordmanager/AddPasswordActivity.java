package edu.khai.csn.abondar.passwordmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.khai.csn.abondar.passwordmanager.Model.Entities.DBHelper;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.Password;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.User;

public class AddPasswordActivity extends AppCompatActivity {

    private EditText etServiceName;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etAddInfo;
    private Button btnAddPass;
    private String serviceName;
    private String userName;
    private String _password;
    private String addInfo;
    private User user;
    Password password;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password);
        db = new DBHelper(this);
        etServiceName = findViewById(R.id.newService);
        etUsername = findViewById(R.id.newUsername);
        etPassword = findViewById(R.id.newPassword);
        etAddInfo = findViewById(R.id.addInfo);

        Bundle extras = getIntent().getExtras();
        if(extras!=null)
            user = (User) extras.getSerializable("user");

    }

    public void addPasswordToDB(View view){
        serviceName = etServiceName.getText().toString().trim();
        userName = etUsername.getText().toString().trim();
        _password = etPassword.getText().toString().trim();
        addInfo = etAddInfo.getText().toString().trim();
        Date date = new Date();
        password = new Password(_password, serviceName, userName, addInfo, 0, date, date);
        db.addPassword(user, password);
        finish();
    }
}
