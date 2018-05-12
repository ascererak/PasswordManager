package edu.khai.csn.abondar.passwordmanager;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import edu.khai.csn.abondar.passwordmanager.Model.Entities.DBHelper;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.Password;


/**
 * A simple {@link Fragment} subclass.
 */
public class WatchDetailsFragment extends Fragment {

    private DBHelper db;
    private HomeActivity activity;
    private ArrayList<Password> passwords;
    private Password password;
    private String service;

    public WatchDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_watch_details, container, false);

        db = new DBHelper(getActivity());
        activity = (HomeActivity) getActivity();
        passwords = db.getPassword(activity.getUser().getUsername());

        for(Password pass:passwords){
            if(pass.getServiceName().equals(service)){
                password = pass;
                break;
            }
        }

        //TODO create password details view
        return view;
    }

}
