package edu.khai.csn.abondar.passwordmanager.View;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.rdrei.android.dirchooser.DirectoryChooserConfig;
import net.rdrei.android.dirchooser.DirectoryChooserFragment;

import java.util.ArrayList;

import edu.khai.csn.abondar.passwordmanager.Model.DBHelper;
import edu.khai.csn.abondar.passwordmanager.Model.GetCorrectPath;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.Password;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.User;
import edu.khai.csn.abondar.passwordmanager.Presenter.CryptoPresenter;
import edu.khai.csn.abondar.passwordmanager.Presenter.XmlPresenter;
import edu.khai.csn.abondar.passwordmanager.R;

public class SettingsActivity extends AppCompatActivity implements DirectoryChooserFragment.OnFragmentInteractionListener,
        TransmitDataView, XmlView {

    private FrameLayout mImport;
    private FrameLayout mExport;
    private ArrayList<Password> mPasswords;
    private User mUser;
    private TextView mName;
    private TextView mUsername;
    private TextView mEmail;
    private EditText mMasterPass;
    private FrameLayout btnChangeMaster;
    private DBHelper db;
    private CryptoPresenter mCryptoPresenter;
    private String mPassword;
    private Toolbar mToolBar;
    private DirectoryChooserFragment mDialog;
    private XmlPresenter mXmlPres;
    private String mDirectory;
    private ArrayList<Password> passToImport;
    private String mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initialize();
        getCurrentUser();

        mName.setText("Name: " + mUser.getName());
        mUsername.setText("Username: " + mUser.getUsername());
        mEmail.setText("Email: " + mUser.getEmail());

        try {
            mPassword = mUser.getMasterPassword();
            mCryptoPresenter.decrypt();
            mMasterPass.setText(mPassword);
        } catch (Exception e) {
            Log.e("Exception", "While encryption");
        }
        btnChangeMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMasterClick();
            }
        });


        DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                .newDirectoryName("DialogSample")
                .build();
        mDialog = DirectoryChooserFragment.newInstance(config);
        mExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeToXml();
            }
        });
        mImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readFromXml();
            }
        });
    }

    private void changeMasterClick() {
        try {
            mPassword = mMasterPass.getText().toString().trim();
            mCryptoPresenter.encrypt();

        } catch (Exception e) {
            Log.e("Exception", "Encryption exception");
        }
        db.updateMaster(mUser, mPassword);
        Toast.makeText(getApplication(), "Master Password has been changed", Toast.LENGTH_LONG).show();
    }

    private void initialize() {
        mImport = findViewById(R.id.btnImport);
        mExport = findViewById(R.id.btnExport);
        mToolBar = findViewById(R.id.navAction);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mName = findViewById(R.id.tvName);
        mUsername = findViewById(R.id.tvUsernameSettings);
        mEmail = findViewById(R.id.tvEmailSettings);
        mMasterPass = findViewById(R.id.editMasterPass);
        btnChangeMaster = findViewById(R.id.btnChangeMaster);
        db = new DBHelper(this);
        mCryptoPresenter = new CryptoPresenter(this);
        mXmlPres = new XmlPresenter(this);
    }

    private void getCurrentUser() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mPasswords = (ArrayList<Password>) extras.getSerializable("passwords");
            mUser = (User) extras.getSerializable("user");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            setResult(789, new Intent());
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void writeToXml() {
        mDialog.show(getFragmentManager(), null);
    }

    private void readFromXml() {

        Intent intent = new Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {

            Uri selectedFile = data.getData(); //The uri with the location of the file
            mPath = GetCorrectPath.getPathFromUri(this, selectedFile);
            mXmlPres.parseXml();
            addImported();
        }
    }

    private void addImported() {
        int i = mPasswords.size();
        for (Password p : passToImport) {
            int j = 0;
            for(; j < i; j++) {
                if (mPasswords.get(j).getServiceName().equals(p.getServiceName()) &&
                        mPasswords.get(j).getUserName().equals(p.getUserName())) {
                    break;
                }
            }
            if(i == j)
                mPasswords.add(p);
        }

        Bundle extras = new Bundle();
        extras.putSerializable("imported", mPasswords);
        Intent intent = new Intent();
        intent.putExtras(extras);
        setResult(1101, intent);
        finish();
    }

    @Override
    public void onSelectDirectory(@NonNull String path) {
        mDirectory = path;
        mXmlPres.writeToXml();
        mDialog.dismiss();
    }

    @Override
    public void onCancelChooser() {
        mDialog.dismiss();
    }

    @Override
    public void setPassword(String password) {
        mPassword = password;
    }

    @Override
    public String getPassword() {
        return mPassword;
    }

    @Override
    public ArrayList<Password> getmPasswords(){
        return mPasswords;
    }

    @Override
    public User getmUser() {
        return mUser;
    }

    @Override
    public String getmDirectory() {
        return mDirectory;
    }

    @Override
    public Context getmContext() {
        return this;
    }

    @Override
    public String getmPath() {
        return mPath;
    }

    @Override
    public void setPassToImport(ArrayList<Password> passToImport) {
        this.passToImport = passToImport;
    }
}
