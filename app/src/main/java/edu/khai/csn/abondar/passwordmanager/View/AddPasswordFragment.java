package edu.khai.csn.abondar.passwordmanager.View;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Date;

import edu.khai.csn.abondar.passwordmanager.Model.Cryptography;
import edu.khai.csn.abondar.passwordmanager.Model.DBHelper;
import edu.khai.csn.abondar.passwordmanager.Model.GeneratePassword;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.Password;
import edu.khai.csn.abondar.passwordmanager.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddPasswordFragment extends Fragment implements View.OnClickListener {

    private EditText mEtServiceName;
    private EditText mEtUsername;
    private EditText mEtPassword;
    private EditText mEtAddInfo;
    private Password mPassword;
    private TextView mTvSeekBarShower;
    private FrameLayout addPassword;
    private String serviceName;
    private String userName;
    private String _password;
    private String addInfo;
    private int mGeneratedPasswordLength;

    public AddPasswordFragment() {
        // Required empty public constructor
    }

    public Password getPassword() {
        return mPassword;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_password, container, false);
        initialize(view);
        FrameLayout btnGeneratePassword = view.findViewById(R.id.btnGenerate);
        btnGeneratePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePassword();
            }
        });

        SeekBar seekBar = view.findViewById(R.id.seekBar);
        seekBar.setProgress(8);
        mTvSeekBarShower = view.findViewById(R.id.seekBarShower);
        mTvSeekBarShower.setText(Integer.toString(8));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= 8) {
                    seekBar.setProgress(8);
                    mTvSeekBarShower.setText(Integer.toString(8));
                    mGeneratedPasswordLength = 8;
                } else {
                    mTvSeekBarShower.setText(Integer.toString(progress));
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

        return view;
    }

    private void initialize(View view) {
        addPassword = view.findViewById(R.id.btnAddPassword);
        addPassword.setOnClickListener(this);
        mEtServiceName = view.findViewById(R.id.newService);
        mGeneratedPasswordLength = 8;
        mEtUsername = view.findViewById(R.id.newUsername);
        mEtPassword = view.findViewById(R.id.newPassword);
        mEtAddInfo = view.findViewById(R.id.addInfo);
    }

    @Override
    public void onClick(View v) {

        getTexts();
        Date date = new Date();
        Cryptography crypto = new Cryptography("passwordmanager1");
        try {
            _password = crypto.encrypt(_password);
        } catch (Exception e) {
            Log.e("ne nice", "exception");
        }
        mPassword = new Password(_password, serviceName, userName, addInfo, 0, date, date);
        Bundle extras = new Bundle();
        extras.putSerializable("mPassword", mPassword);
        this.setArguments(extras);
        DBHelper db = new DBHelper(getActivity());
        HomeActivity homeActivity = (HomeActivity) getActivity();

        db.addPassword(homeActivity.getUser(), mPassword);
        homeActivity.animateFab();
        homeActivity.onStart();
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    private void getTexts() {
        serviceName = mEtServiceName.getText().toString().trim();
        userName = mEtUsername.getText().toString().trim();
        _password = mEtPassword.getText().toString().trim();
        addInfo = mEtAddInfo.getText().toString().trim();
    }

    public void generatePassword() {
        GeneratePassword generator = new GeneratePassword();
        try {
            mEtPassword.setText(generator.generate(mGeneratedPasswordLength));
        } catch (Exception e) {
            Log.e("Exception", "Exception while generating password");
        }
    }

}
