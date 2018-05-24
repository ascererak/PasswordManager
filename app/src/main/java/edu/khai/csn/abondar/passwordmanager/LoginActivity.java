package edu.khai.csn.abondar.passwordmanager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
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
import android.util.Log;
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
    protected String mPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        findViews();

        db = new DBHelper(this);

        btnLogin.setOnClickListener(this);
    }

    private void findViews(){
        textViewOnButtonLogIn = findViewById(R.id.textViewOnButtonLogin);
        progressBar = findViewById(R.id.progressBar);
        reveal = findViewById(R.id.reveal);
        btnLogin = findViewById(R.id.btnLogin);
        username = findViewById(R.id.etUsername);
        password = findViewById(R.id.etPassword);
        forgotPass = findViewById(R.id.tvForgotPass);
    }

    @Override
    public void onClick(View view) {
        String _username = username.getText().toString();
        mPassword = password.getText().toString();

        encrypt();

        if (db.getUser(_username, mPassword)) {
            prepareToLaunchActivity(_username);
        }
        else{
            Toast.makeText(this, "There is no such user or password is incorrect", Toast.LENGTH_SHORT).show();
        }
    }

    private void prepareToLaunchActivity(String _username) {
        intent = new Intent(this, HomeActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable("user", db.getUser(_username));
        intent.putExtras(extras);
        load();
    }

    public void encrypt(){

        String key = "passwordmanager1";
        Cryptography crypto = new Cryptography(key);

        try {
            mPassword = crypto.encrypt(mPassword);
        }
        catch (Exception e) {
            Log.e("Exception", "Something went wrong while encrypting password");
        }
    }

    private void load() {
        animateButtonWidth();

        fadeOutTextAndShowProgressDialog();

        nextAction();
    }

    private void animateButtonWidth() {
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

    private void fadeOutTextAndShowProgressDialog() {
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

    private void showProgressDialog() {
        mBinding.progressBar
                .getIndeterminateDrawable()
                .setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
        mBinding.progressBar.setVisibility(View.VISIBLE);
    }

    public void forgotPassClick(View view) {
        Intent intent = new Intent(this, RestorePasswordActivity.class);
        startActivity(intent);
    }

    public void btnSignupLSClick(View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    private void nextAction() {
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
    private void revealButton() {
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
            }
            //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            //private void reset(){
            //    mBinding.textViewOnButtonLogin.setVisibility(View.VISIBLE);
            //    mBinding.textViewOnButtonLogin.setAlpha(1f);
            //    mBinding.btnLogin.setElevation(4f);
            //    ViewGroup.LayoutParams layoutParams = mBinding.btnLogin.getLayoutParams();
            //    layoutParams.width =  mBinding.btnSignUpLS.getMeasuredWidth();
            //    mBinding.btnLogin.requestLayout();
            //
            });

        reveal.start();
    }

    private void fadeOutProgressDialog() {
        mBinding.progressBar.animate().alpha(0f).setDuration(200).start();
    }

    private void delayedStartNextActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
           }
       }, 100);
    }
}
