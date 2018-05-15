package edu.khai.csn.abondar.passwordmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.khai.csn.abondar.passwordmanager.Model.Entities.DBHelper;

public class RestorePasswordActivity extends AppCompatActivity {

    private EditText mEtEmail;
    private String mEmail;
    private Button mBtnRestorePassword;
    private final String mSenderEmail = "recover.pass.manager@gmail.com";
    private final String mSenderPassword = "qw56io09wwdc";
    private static final String mLetterTheme = "Password recovery";
    private static final String mBodyPart1 = "This is password recovery letter.\nYour password is: ";
    private static final String mBodyPart2 = ".\nIf you didn't request password recovery, just ignore this letter.\n\n" +
            "If you are having any troubles using our service, please give us a feedback in reply to this letter.";
    DBHelper db;
    Context context;
    Cryptography crypto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_password);

        Toolbar mToolBar = findViewById(R.id.navAction);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mEtEmail = findViewById(R.id.email_restpass);
        mBtnRestorePassword = findViewById(R.id.btnRestPass);
        db = new DBHelper(this);
        context = this;
        crypto = new Cryptography("passwordmanager1", context);

        mBtnRestorePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = "";
                mEmail = mEtEmail.getText().toString();

                if (mEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
                    mEtEmail.setError("Please enter valid email");
                } else {
                    if ((password = db.getUserByEmail(mEmail)) == null) {
                        Toast.makeText(context, "User with such email doesn't exist", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            password = crypto.decrypt(password);
                        } catch (Exception e) {
                        }
                        final String body = mBodyPart1 + password + mBodyPart2;
                        Thread thread = new Thread(new Runnable() {

                            @Override
                            public void run() {

                                try {
                                    MailSender sender = new MailSender(mSenderEmail, mSenderPassword);
                                    sender.sendMail(mLetterTheme, body, "admin@gmail.com", mEmail, "");
                                } catch (Exception e) {

                                }
                            }
                        });

                        thread.start();


                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
                        dlgAlert.setMessage("Letter with your password was successfully sent to your email.");
                        dlgAlert.setTitle("Password recovery");
                        dlgAlert.setPositiveButton("OK", null);
                        dlgAlert.setCancelable(true);
                        dlgAlert.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                        dlgAlert.create().show();
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}


