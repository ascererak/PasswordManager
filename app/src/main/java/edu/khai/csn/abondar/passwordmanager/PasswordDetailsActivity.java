package edu.khai.csn.abondar.passwordmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.khai.csn.abondar.passwordmanager.Model.Entities.DBHelper;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.GeneratePassword;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.Password;

public class PasswordDetailsActivity extends AppCompatActivity {

    private EditText mEtService;
    private EditText mEtUsername;
    private EditText mEtAddInfo;
    private EditText mEtPassword;
    private String mService;
    private String mPassword;
    private String mUsername;
    private String mAddInfo;
    private TextView mSeekBarShower;
    private SeekBar mSeekBar;
    private DBHelper mDb;
    private int mGeneratedPasswordLength;
    private boolean mIsEditingAllowed = false;
    private Cryptography mCrypto;
    private Button mBtnSaveChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_details);

        mGeneratedPasswordLength = 8;
        mEtService = findViewById(R.id.ServiceShow);
        mEtUsername = findViewById(R.id.UsernameShow);
        mEtPassword = findViewById(R.id.PasswordShow);
        mBtnSaveChanges = findViewById(R.id.btnSaveChanges);
        TextView mTvCreationDate = findViewById(R.id.tvCreationDate);
        mEtAddInfo = findViewById(R.id.AddInfoShow);
        TextView mTvModifingDate = findViewById(R.id.tvModifingDate);
        Toolbar mToolBar = findViewById(R.id.navAction);
        mSeekBarShower = findViewById(R.id.seekBarShowerWD);
        mSeekBar = findViewById(R.id.seekBarWD);
        mSeekBar.setProgress(8);
        mCrypto = new Cryptography("passwordmanager1", this);

        setSupportActionBar(mToolBar);
        final Password password = (Password) getIntent().getExtras().getSerializable("pass");
        mBtnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextToString();
                Password _password = new Password(mPassword, mService, mUsername, mAddInfo, password.getId(), password.getCreationDate(), new Date());
                mDb.updatePassword(_password);
                finish();
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= 8) {
                    seekBar.setProgress(8);
                    mSeekBarShower.setText(Integer.toString(8));
                    mGeneratedPasswordLength = 8;
                } else {
                    mSeekBarShower.setText(Integer.toString(progress));
                    mGeneratedPasswordLength = progress;
                    generatePassword();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mDb = new DBHelper(this);


        mEtService.setText(password.getServiceName());
        mEtUsername.setText(password.getUserName());
        mEtAddInfo.setText(password.getAdditionalInformation());
        String date = convertDate(password.getCreationDate().toString());
        mTvCreationDate.setText(date);
        date = convertDate(password.getModifyingDate().toString());
        mTvModifingDate.setText(date);

        String decrPassword = "";
        try {
            decrPassword = mCrypto.decrypt(password.getPassword());
        } catch (Exception e) {
        }
        mEtPassword.setText(decrPassword);
    }

    public void editTextToString() {

        mService = mEtService.getText().toString().trim();
        mPassword = mEtPassword.getText().toString().trim();
        try {
            mPassword = mCrypto.encrypt(mPassword);
        } catch (Exception e) {
        }
        mUsername = mEtUsername.getText().toString().trim();
        mAddInfo = mEtAddInfo.getText().toString().trim();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu_edit, menu);
        //MenuItem menuItem = menu.findItem(R.id.action_edit);
        //Button button = (Button) MenuItemCompat.getActionView(menuItem);
//
        //button.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //
        //    }
        //});
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!mIsEditingAllowed) {
            mService = mEtService.getText().toString().trim();
            mPassword = mEtPassword.getText().toString().trim();
            mUsername = mEtUsername.getText().toString().trim();
            mAddInfo = mEtAddInfo.getText().toString().trim();

            mBtnSaveChanges.setVisibility(View.VISIBLE);
            mBtnSaveChanges.setEnabled(true);
            mEtService.setEnabled(true);
            mEtUsername.setEnabled(true);
            mEtPassword.setEnabled(true);
            mEtAddInfo.setEnabled(true);
            mSeekBar.setEnabled(true);
            mSeekBar.setVisibility(View.VISIBLE);
            mSeekBarShower.setVisibility(View.VISIBLE);
        }
        else {
            mEtService.setText(mService);
            mEtUsername.setText(mUsername);
            mEtPassword.setText(mPassword);
            mEtAddInfo.setText(mAddInfo);

            mBtnSaveChanges.setVisibility(View.INVISIBLE);
            mBtnSaveChanges.setEnabled(false);
            mEtService.setEnabled(false);
            mEtUsername.setEnabled(false);
            mEtPassword.setEnabled(false);
            mEtAddInfo.setEnabled(false);
            mSeekBar.setEnabled(false);
            mSeekBarShower.setVisibility(View.INVISIBLE);
            mSeekBar.setVisibility(View.INVISIBLE);
        }

        mIsEditingAllowed = !mIsEditingAllowed;
        return super.onOptionsItemSelected(item);
    }

    private String convertDate(String oldDate) {

        SimpleDateFormat format = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
        SimpleDateFormat newDateFormat = new SimpleDateFormat("dd MMMM yyyy");
        Date date = new Date();
        try {
            date = format.parse(oldDate);
        } catch (ParseException e) {
        }
        String result = newDateFormat.format(date);

        return result;
    }

    public void generatePassword() {
        GeneratePassword generator = new GeneratePassword();
        try {
            mEtPassword.setText(generator.generate(mGeneratedPasswordLength));
        } catch (Exception e) {
        }
    }
}
