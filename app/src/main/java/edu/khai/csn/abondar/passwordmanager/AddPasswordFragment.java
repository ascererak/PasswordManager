package edu.khai.csn.abondar.passwordmanager;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import edu.khai.csn.abondar.passwordmanager.Model.Entities.DBHelper;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.GeneratePassword;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.Password;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddPasswordFragment extends Fragment implements View.OnClickListener{

    private User user;
    private Button addPassword;
    private EditText etServiceName;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etAddInfo;
    private String serviceName;
    private String userName;
    private String _password;
    private String addInfo;
    private Password password;
    private Button btnGeneratePassword;
    private SeekBar seekBar;
    private TextView tvSeekBarShower;
    private int generatedPasswordLength = 8;

    public AddPasswordFragment() {
        // Required empty public constructor
    }

    public Password getPassword(){
        return password;
    }
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.fragment_add_password, container, false);
        addPassword = view.findViewById(R.id.btnAddPassword);
        addPassword.setOnClickListener(this);
        etServiceName = view.findViewById(R.id.newService);
        etUsername = view.findViewById(R.id.newUsername);
        etPassword = view.findViewById(R.id.newPassword);
        etAddInfo = view.findViewById(R.id.addInfo);
        btnGeneratePassword = view.findViewById(R.id.btnGenerate);
        btnGeneratePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePassword();
            }
        });

        seekBar = view.findViewById(R.id.seekBar);
        seekBar.setProgress(8);
        tvSeekBarShower = view.findViewById(R.id.seekBarShower);
        tvSeekBarShower.setText(Integer.toString(8));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress<=8) {
                    seekBar.setProgress(8);
                    tvSeekBarShower.setText(Integer.toString(8));
                    generatedPasswordLength = 8;
                }
                else{
                    tvSeekBarShower.setText(Integer.toString(progress));
                    generatedPasswordLength = progress;
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
        serviceName = etServiceName.getText().toString().trim();
        userName = etUsername.getText().toString().trim();
        _password = etPassword.getText().toString().trim();
        addInfo = etAddInfo.getText().toString().trim();
        Date date = new Date();
        Cryptography crypto = new Cryptography("passwordmanager1");
        try{_password = crypto.encrypt(_password);}catch (Exception e){
            Log.e("ne nice", "exception");
        }
        password = new Password(_password, serviceName, userName, addInfo, 0, date, date);
        Bundle extras = new Bundle();
        extras.putSerializable("password", password);
        this.setArguments(extras);
        //getActivity().getFragmentManager().popBackStackImmediate();
        DBHelper db = new DBHelper(getActivity());
        //user =
        HomeActivity homeActivity = (HomeActivity) getActivity();

        db.addPassword(homeActivity.getUser(), password);
        homeActivity.animateFab();
        homeActivity.onStart();
        getActivity().getSupportFragmentManager().popBackStackImmediate();
        //Toast.makeText(getActivity(), "Password was successfully added!", Toast.LENGTH_LONG).show();
    }

    public void generatePassword() {
        GeneratePassword generator = new GeneratePassword();
        try{etPassword.setText(generator.generate(generatedPasswordLength));}catch (Exception e){}
    }

}
