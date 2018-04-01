package edu.khai.csn.abondar.passwordmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Main extends AppCompatActivity implements View.OnClickListener{

    protected CardView btn;
    protected EditText username;
    protected EditText password;
    protected TextView forgotPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        btn = findViewById(R.id.cardView);
        btn.setOnClickListener(this);
        username = findViewById(R.id.etUsername);
        password = findViewById(R.id.etPassword);
        forgotPass = findViewById(R.id.tvForgotPass);
    }

    @Override
    public void onClick(View view) {

        if (username.getText().toString().equals("admin") && password.getText().toString().equals("admin")) {
            Intent intent = new Intent(this, Second.class);
            startActivity(intent);
        }
    }

    public void forgotPassClick(View view) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage("So remember it, I don't actually care if you forgot things");
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
}
