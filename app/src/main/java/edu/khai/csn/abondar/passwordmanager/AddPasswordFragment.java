package edu.khai.csn.abondar.passwordmanager;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Date;

import edu.khai.csn.abondar.passwordmanager.Model.Entities.DBHelper;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.GeneratePassword;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.Password;
import edu.khai.csn.abondar.passwordmanager.Presenter.CryptoPresenter;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddPasswordFragment extends Fragment implements View.OnClickListener{

    private EditText mEtServiceName;
    private EditText mEtUsername;
    private EditText mEtPassword;
    private EditText mEtAddInfo;
    private Password mPassword;
    private TextView mTvSeekBarShower;
    private int mGeneratedPasswordLength;

    public AddPasswordFragment() {
        // Required empty public constructor
    }

    public Password getPassword(){
        return mPassword;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_password, container, false);
        Button addPassword = view.findViewById(R.id.btnAddPassword);
        addPassword.setOnClickListener(this);
        mEtServiceName = view.findViewById(R.id.newService);
        mGeneratedPasswordLength = 8;
        mEtUsername = view.findViewById(R.id.newUsername);
        mEtPassword = view.findViewById(R.id.newPassword);
        mEtAddInfo = view.findViewById(R.id.addInfo);
        Button btnGeneratePassword = view.findViewById(R.id.btnGenerate);
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
                if(progress<=8) {
                    seekBar.setProgress(8);
                    mTvSeekBarShower.setText(Integer.toString(8));
                    mGeneratedPasswordLength = 8;
                }
                else{
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

    @Override
    public void onClick(View v) {
        String serviceName = mEtServiceName.getText().toString().trim();
        String userName = mEtUsername.getText().toString().trim();
        String _password = mEtPassword.getText().toString().trim();
        String addInfo = mEtAddInfo.getText().toString().trim();
        Date date = new Date();
        //CryptoPresenter crypto = new CryptoPresenter(this);
        //crypto.encrypt();
        Cryptography crypto = new Cryptography("passwordmanager1", getActivity());
        try{
            _password = crypto.encrypt(_password);}catch (Exception e){
            Log.e("ne nice", "exception");
        }
        mPassword = new Password(_password, serviceName, userName, addInfo, 0, date, date);
        Bundle extras = new Bundle();
        extras.putSerializable("mPassword", mPassword);
        this.setArguments(extras);
        //getActivity().getFragmentManager().popBackStackImmediate();
        DBHelper db = new DBHelper(getActivity());
        //user =
        HomeActivity homeActivity = (HomeActivity) getActivity();

        db.addPassword(homeActivity.getUser(), mPassword);
        homeActivity.animateFab();
        homeActivity.onStart();
        getActivity().getSupportFragmentManager().popBackStackImmediate();
        //Toast.makeText(getActivity(), "Password was successfully added!", Toast.LENGTH_LONG).show();
    }

    public void generatePassword() {
        GeneratePassword generator = new GeneratePassword();
        try{
            mEtPassword.setText(generator.generate(mGeneratedPasswordLength));}catch (Exception e){}
    }

}
