package edu.khai.csn.abondar.passwordmanager;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import java.util.ArrayList;

import edu.khai.csn.abondar.passwordmanager.Model.Entities.DBHelper;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.Password;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.User;

public class HomeActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,
        NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle Toggle;
    private ActionBar actionBar;
    private android.support.v7.widget.Toolbar mToolBar;
    private DBHelper db;
    private ArrayList<Password> mPasswordList;
    private FloatingActionButton fab;
    private Animation rotate_backward, rotate_forward;
    private Boolean isFabOpen = false;
    private User user;
    private TextView lblUsername;
    private String mServiceName;
    //private String[] c_services = {"Twitter", "Google", "Instagram"};
    //private String[] accountLogins = {"@ascererak", "a.bondar@student.csn.khai.edu", "@ascererak"};
    //private int[] accountImages = {R.drawable.ic_icon_twitter, R.drawable.ic_icon_google, R.drawable.ic_icon_instagram};
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    //private ArrayList<Password> arrayList = new ArrayList<>();
    private RelativeLayout layoutContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getCurrentUser();
      //  lblUsername.setText(user.getUsername());
        db = new DBHelper(this);
        mPasswordList = db.getPassword(user.getUsername());
        mToolBar = findViewById(R.id.navAction);
        setSupportActionBar(mToolBar);
        fab = findViewById(R.id.fab);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        layoutContent = findViewById(R.id.layoutContent);

        mDrawerLayout = findViewById(R.id.drawerLayout);

        Toggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(Toggle);
        Toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0f0c29")));

        buildRecyclerView();

        //mRecyclerView.setFocusable(false);
        //mRecyclerView.setFocusableInTouchMode(false);

        mAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick (int position) {

                mServiceName = mPasswordList.get(position).getServiceName();
                callWatchPasswordDetailsActivity();
            }
        }
);


        //int count = 0;
//
        //for(Password name : passwordList) {//c_services) {
        //    arrayList.add(new Service(name, accountLogins[count], accountImages[count]));
        //    count++;
        //}




        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void buildRecyclerView(){
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new RecyclerAdapter(mPasswordList, this);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Getting a current user
     */
    private void getCurrentUser(){
        Bundle extras = getIntent().getExtras();
        if(extras!=null)
            user = (User) extras.getSerializable("user");
        lblUsername = findViewById(R.id.lblUsername5);
    }

    private void callWatchPasswordDetailsActivity() {
        //Toast.makeText(this, mServiceName, Toast.LENGTH_LONG);
         Intent intent = new Intent(this, PasswordDetailsActivity.class);
         intent.putExtra("service", mServiceName);
         startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (Toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public  void onStart(){
        super.onStart();
        db = new DBHelper(this);
        mPasswordList = db.getPassword(user.getUsername());
        mAdapter = new RecyclerAdapter(mPasswordList, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.navigation_menu_search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {

        s = s.toLowerCase();
        ArrayList<Password> newList = new ArrayList<>();
        for(Password password : mPasswordList){
            String serviceName = password.getServiceName().toLowerCase();

            if(serviceName.contains(s))
                newList.add(password);
        }
        mAdapter.setFilter(newList);

        return true;
    }

    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;
        int id = item.getItemId();

        switch (id){
            case R.id.nav_home:
                fragment = new DefaultFragment();
                break;
            case R.id.nav_account:
               // fragment = new AccountFragment();
                break;
            case R.id.nav_import_export:
              //  fragment = new ImportExportFragment();
                break;
            case R.id.nav_settings:
            //    fragment = new SettingsFragment();
                break;
            case R.id.nav_logout:
                finish();
                return false;
        }


        mDrawerLayout.closeDrawers();
        return false;
    }

    public void addPassword(View v){
        Fragment fragment;

        if(!isFabOpen) {
            animateFab();
            fragment = new AddPasswordFragment();
        }
        else{
            animateFab();
            fragment = new DefaultFragment();
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();



        ///extras = new Bundle();
        ///extras = fragment.getArguments().getBundle("password");
        ///Password password = (Password) extras.getSerializable("password");
        ///db.addPassword(user, password);
        //animateFab();
        //Intent intent = new Intent(this, AddPasswordActivity.class);
        //Bundle extras = new Bundle();
        //extras.putSerializable("user", user);
        //intent.putExtras(extras);
        //startActivityForResult(intent, 2);
        //fab.startAnimation(rotate_backward);
        //startActivity(intent);
    }

    public void animateFab() {
        if (isFabOpen) {
            fab.startAnimation(rotate_backward);
            isFabOpen = false;
        }
        else{
            fab.startAnimation(rotate_forward);
            isFabOpen = true;
        }
    }

    public User getUser(){
        return user;
    }

    public void watchPasswordDetails(){
       // TextView tvServiceName = findViewById(R.id.serviceName);
        String _serviceName = "Very good and very wonderful";//tvServiceName.getText().toString();
        Toast.makeText(HomeActivity.this, _serviceName, Toast.LENGTH_LONG);
       // Fragment fragment = new WatchDetailsFragment();
       // FragmentManager manager = getSupportFragmentManager();
       // FragmentTransaction transaction = manager.beginTransaction();
       // transaction.addToBackStack(null);
       // transaction.replace(R.id.fragment, fragment);
       // transaction.commit();
    }
}
