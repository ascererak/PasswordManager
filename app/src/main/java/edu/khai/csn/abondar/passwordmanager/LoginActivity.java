package edu.khai.csn.abondar.passwordmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.khai.csn.abondar.passwordmanager.Model.Entities.DBHelper;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.Password;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.User;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    protected Button btn;
    protected EditText username;
    protected EditText password;
    protected TextView forgotPass;
    protected ArrayList<User> users = new ArrayList<>();
    protected DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DBHelper(this);
        btn = findViewById(R.id.btnLogin);
        btn.setOnClickListener(this);
        username = findViewById(R.id.etUsername);
        password = findViewById(R.id.etPassword);
        forgotPass = findViewById(R.id.tvForgotPass);
    }

    @Override
    public void onClick(View view) {
        //boolean found = false;
        String _username = username.getText().toString();
        String _password = password.getText().toString();

        //for(User user:users){
        //    if(user.getUsername().equals(_username) && user.getMasterPassword().equals(_password)){
        //        found = true;
        //        break;
        //    }
        //}

        if (db.getUser(_username, _password)) {//|| found || (_username.equals("admin") && _password.equals("admin"))) {
            Intent intent = new Intent(this, Second.class);
            Bundle extras = new Bundle();
            extras.putSerializable("user", db.getUser(_username));
            intent.putExtras(extras);
            startActivityForResult(intent, 1);
            //startActivity(intent);
        }
        else{
            Toast.makeText(this, "There is no such user or password is incorrect", Toast.LENGTH_SHORT).show();
        }
    }

    public void forgotPassClick(View view) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage("Coming soon...");
        dlgAlert.setTitle("Password recovery");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss the dialog
                    }
                });
        dlgAlert.create().show();
    }

    public void btnSignupLS_click(View view){
        Intent intent = new Intent(this, RegistrationActivity.class);
        //   Bundle extras = new Bundle();
        // extras.putSerializable("users", users);
        //intent.putExtras(extras);
        //startActivityForResult(intent, 1);
        startActivity(intent);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(data == null) {
//            return;
//        }
//
//        Bundle bundle = data.getExtras();
//        users = (ArrayList<User>) bundle.getSerializable("users1");
//    }

    public ArrayList<User> getUsers(){
        return users;
    }

    public void setUsers(ArrayList<User> users){
        this.users = users;
    }
}
