package edu.khai.csn.abondar.passwordmanager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.DBHelper;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.User;
import edu.khai.csn.abondar.passwordmanager.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    protected FrameLayout btnLogin;
    protected EditText username;
    protected EditText password;
    protected TextView forgotPass;
    protected ArrayList<User> users = new ArrayList<>();
    protected DBHelper db;
    protected ActivityLoginBinding mBinding;
    protected TextView textViewOnButtonLogIn;
    protected ProgressBar progressBar;
    protected View reveal;
    protected Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        textViewOnButtonLogIn = findViewById(R.id.textViewOnButtonLogin);
        progressBar = findViewById(R.id.progressBar);
        reveal = findViewById(R.id.reveal);

        db = new DBHelper(this);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        username = findViewById(R.id.etUsername);
        password = findViewById(R.id.etPassword);
        forgotPass = findViewById(R.id.tvForgotPass);
    }

    @Override
    public void onClick(View view) {
        String _username = username.getText().toString();
        String _password = password.getText().toString();

        Cryptography crypto = new Cryptography("passwordmanager1");
        try {
            _password = crypto.encrypt(_password);
        }catch (Exception e){}

        if (db.getUser(_username, _password)) {//|| found || (_username.equals("admin") && _password.equals("admin"))) {
            intent = new Intent(this, HomeActivity.class);
            Bundle extras = new Bundle();
            extras.putSerializable("user", db.getUser(_username));
            intent.putExtras(extras);
            load();
        }
        else{
            Toast.makeText(this, "There is no such user or password is incorrect", Toast.LENGTH_SHORT).show();
        }

    }

    private void startHomeActivity(){

    }

    private void load(){
        animateButtonWidth();

        fadeOutTextAndShowProgressDialog();

        nextAction();
    }

    private void animateButtonWidth(){
        ValueAnimator anim = ValueAnimator.ofInt(mBinding.btnLogin.getMeasuredWidth(), mBinding.btnLogin.getMeasuredHeight());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = mBinding.btnLogin.getLayoutParams();
                layoutParams.width = val;
                mBinding.btnLogin.requestLayout();
            }
        });
        anim.setDuration(250);
        anim.start();
    }

    private void fadeOutTextAndShowProgressDialog(){
        mBinding.textViewOnButtonLogin.animate().alpha(0f)
                .setDuration(250)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        showProgressDialog();
                    }
                }).start();
    }

    private void showProgressDialog(){
        mBinding.progressBar
                .getIndeterminateDrawable()
                .setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
        mBinding.progressBar.setVisibility(View.VISIBLE);
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

    private void nextAction(){
        new Handler().postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                revealButton();

                fadeOutProgressDialog();

                delayedStartNextActivity();
            }
        }, 1000);
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void revealButton(){
        mBinding.btnLogin.setElevation(0f);

        mBinding.reveal.setVisibility(View.VISIBLE);
        int cx = mBinding.reveal.getWidth();
        int cy = mBinding.reveal.getHeight();

        int x = (int) (mBinding.btnLogin.getMeasuredHeight()/2 + mBinding.btnLogin.getX());
        int y = (int) (mBinding.btnLogin.getMeasuredHeight()/2 + mBinding.btnLogin.getY());

        float finalRadius = Math.max(cx, cy) * 1.2f;

        Animator reveal = ViewAnimationUtils.createCircularReveal(mBinding.reveal, x, y, 56, finalRadius);
        reveal.setDuration(350);
        reveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
               //finish();
               //  reset();
            }
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            private void reset(){
                mBinding.textViewOnButtonLogin.setVisibility(View.VISIBLE);
                mBinding.textViewOnButtonLogin.setAlpha(1f);
                mBinding.btnLogin.setElevation(4f);
                ViewGroup.LayoutParams layoutParams = mBinding.btnLogin.getLayoutParams();
                layoutParams.width =  mBinding.btnSignUpLS.getMeasuredWidth();
                mBinding.btnLogin.requestLayout();
            }});

        reveal.start();
    }

    private void fadeOutProgressDialog(){
        mBinding.progressBar.animate().alpha(0f).setDuration(200).start();
    }

    private void delayedStartNextActivity(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
           }
       }, 100);
    }
}
