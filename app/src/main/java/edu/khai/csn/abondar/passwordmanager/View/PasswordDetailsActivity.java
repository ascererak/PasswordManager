package edu.khai.csn.abondar.passwordmanager.View;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.khai.csn.abondar.passwordmanager.Model.DBHelper;
import edu.khai.csn.abondar.passwordmanager.Model.GeneratePassword;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.Password;
import edu.khai.csn.abondar.passwordmanager.Presenter.CryptoPresenter;
import edu.khai.csn.abondar.passwordmanager.R;

public class PasswordDetailsActivity extends AppCompatActivity implements TransmitDataView {

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
    private boolean mIsEditingAllowed;
    private CryptoPresenter mCryptoPresenter;
    private FrameLayout mBtnSaveChanges;
    private TextView mTvCreationDate;
    private TextView mTvModifyingDate;
    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_details);
        final Password password = (Password) getIntent().getExtras().getSerializable("pass");

        initialize();

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

        setEditTexts(password);
    }

    private void initialize() {
        mGeneratedPasswordLength = 8;
        mIsEditingAllowed = false;
        mEtService = findViewById(R.id.ServiceShow);
        mEtUsername = findViewById(R.id.UsernameShow);
        mEtPassword = findViewById(R.id.PasswordShow);
        mBtnSaveChanges = findViewById(R.id.btnSaveChanges);
        mTvCreationDate = findViewById(R.id.tvCreationDate);
        mEtAddInfo = findViewById(R.id.AddInfoShow);
        mTvModifyingDate = findViewById(R.id.tvModifingDate);
        mToolBar = findViewById(R.id.navAction);
        mSeekBarShower = findViewById(R.id.seekBarShowerWD);
        mSeekBar = findViewById(R.id.seekBarWD);
        mSeekBar.setProgress(8);
        mCryptoPresenter = new CryptoPresenter(this);
        //mCrypto = new Cryptography("passwordmanager1");
        mDb = new DBHelper(this);

        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setEditTexts(Password password) {
        mPassword = password.getPassword();
        mEtService.setText(password.getServiceName());
        mEtUsername.setText(password.getUserName());
        mEtAddInfo.setText(password.getAdditionalInformation());
        String date = convertDate(password.getCreationDate().toString());
        mTvCreationDate.setText(date);
        date = convertDate(password.getModifyingDate().toString());
        mTvModifyingDate.setText(date);

        //String decrPassword = "";
        try {
            mCryptoPresenter.decrypt();
        } catch (Exception e) {
            Log.e("Decryption exception", "PasswordDetailsActivity");
        }
        mEtPassword.setText(mPassword);
    }

    public void editTextToString() {
        mService = mEtService.getText().toString().trim();
        mPassword = mEtPassword.getText().toString().trim();
        try {
            mCryptoPresenter.encrypt();
        } catch (Exception e) {
            Log.e("Exception", "Error while encryption");
        }
        mUsername = mEtUsername.getText().toString().trim();
        mAddInfo = mEtAddInfo.getText().toString().trim();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu_edit, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {

            if (!mIsEditingAllowed) {
                mService = mEtService.getText().toString().trim();
                mPassword = mEtPassword.getText().toString().trim();
                mUsername = mEtUsername.getText().toString().trim();
                mAddInfo = mEtAddInfo.getText().toString().trim();

                mBtnSaveChanges.setVisibility(View.VISIBLE);
                mSeekBar.setVisibility(View.VISIBLE);
                mSeekBarShower.setVisibility(View.VISIBLE);
            } else {
                mEtService.setText(mService);
                mEtUsername.setText(mUsername);
                mEtPassword.setText(mPassword);
                mEtAddInfo.setText(mAddInfo);

                mBtnSaveChanges.setVisibility(View.INVISIBLE);
                mSeekBarShower.setVisibility(View.INVISIBLE);
                mSeekBar.setVisibility(View.INVISIBLE);
            }
            mBtnSaveChanges.setEnabled(!mIsEditingAllowed);
            mEtService.setEnabled(!mIsEditingAllowed);
            mEtUsername.setEnabled(!mIsEditingAllowed);
            mEtPassword.setEnabled(!mIsEditingAllowed);
            mEtAddInfo.setEnabled(!mIsEditingAllowed);
            mSeekBar.setEnabled(!mIsEditingAllowed);
            mIsEditingAllowed = !mIsEditingAllowed;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private String convertDate(String oldDate) {

        SimpleDateFormat format = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
        SimpleDateFormat newDateFormat = new SimpleDateFormat("dd MMMM yyyy");
        Date date = new Date();
        try {
            date = format.parse(oldDate);
        } catch (ParseException e) {
            Log.e("ParseException", "Couldn't parse date, PasswordDetailsActivity");
        }
        String result = newDateFormat.format(date);

        return result;
    }

    public void generatePassword() {
        GeneratePassword generator = new GeneratePassword();
        try {
            mEtPassword.setText(generator.generate(mGeneratedPasswordLength));
        } catch (Exception e) {
            Log.e("Exception", "Exception while generating password");
        }
    }

    @Override
    public void setPassword(String password) {
        mPassword = password;
    }

    @Override
    public String getPassword() {
        return mPassword;
    }
}
