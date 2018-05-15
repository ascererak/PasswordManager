package edu.khai.csn.abondar.passwordmanager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import android.widget.Toast;

import java.util.ArrayList;
import android.os.Handler;

import edu.khai.csn.abondar.passwordmanager.Model.Entities.DBHelper;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.Password;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.User;
import edu.khai.csn.abondar.passwordmanager.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,
        NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private DBHelper mDb;
    private ArrayList<Password> mPasswordList;
    private FloatingActionButton mFab;
    private Animation mRotateBackward;
    private Animation mRotateForward;
    private Boolean mIsFabOpen = false;
    private User mUser;
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;
    private ActivityHomeBinding mBinding;
    protected View mReveal;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_home);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        //LoginActivity loginActivity = (LoginActivity) getParent();
        //loginActivity.reset();

        getCurrentUser();
      //  lblUsername.setText(mUser.getUsername());
        mDb = new DBHelper(this);
        mPasswordList = mDb.getPassword(mUser.getUsername());
        android.support.v7.widget.Toolbar mToolBar = findViewById(R.id.navAction);
        setSupportActionBar(mToolBar);
        mFab = findViewById(R.id.fab);
        mRotateBackward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        mRotateForward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        //layoutContent = findViewById(R.id.layoutContent);
        //usernameOnNavHeader = findViewById(R.id.username_on_nav_header);
        //usernameOnNavHeader.setText(mUser.getUsername());

        mDrawerLayout = findViewById(R.id.drawerLayout);
        mReveal = findViewById(R.id.reveal);

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0f0c29")));


        buildRecyclerView();

        //mRecyclerView.setFocusable(false);
        //mRecyclerView.setFocusableInTouchMode(false);

        mAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick (int position) {

                //mServiceName = mPasswordList.get(position).getServiceName();
                //callWatchPasswordDetailsActivity();
            }
        }
);


        //int count = 0;
//
        //for(Password name : passwordList) {//c_services) {
        //    arrayList.add(new Service(name, accountLogins[count], accountImages[count]));
        //    count++;
        //}




        NavigationView navigationView = mBinding.navigationView;//findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void buildRecyclerView(){
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mAdapter = new RecyclerAdapter(mPasswordList, this);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Getting a current mUser
     */
    private void getCurrentUser(){
        Bundle extras = getIntent().getExtras();
        if(extras!=null)
            mUser = (User) extras.getSerializable("user");
       // lblUsername = findViewById(R.id.lblUsername5);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public  void onStart(){
        super.onStart();
        mDb = new DBHelper(this);
        mPasswordList = mDb.getPassword(mUser.getUsername());
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

    //@Override
    //public void onPause() {
    //    super.onPause();
    //    LoginActivity login = (LoginActivity) getParent();
    //    login.mBinding.mReveal.setVisibility(View.INVISIBLE);
    //    finish();
    //}


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
                LoginActivity login = (LoginActivity) getParent();
                login.mBinding.reveal.setVisibility(View.INVISIBLE);
                finish();
                return false;
        }
        mDrawerLayout.closeDrawers();
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void addPassword(View v){
        Fragment fragment;

        if(!mIsFabOpen) {
            animateFab();
            mBinding.fab.setElevation(0f);
            mBinding.reveal.setVisibility(View.VISIBLE);

            int cx = mBinding.reveal.getWidth();
            int cy = mBinding.reveal.getHeight();

            int x = (int) (mBinding.fab.getMeasuredHeight()/2 + mBinding.fab.getX());
            int y = (int) (mBinding.fab.getMeasuredHeight()/2 + mBinding.fab.getY());

            float finalRadius = Math.max(cx, cy) * 1.2f;

            Animator reveal = ViewAnimationUtils.createCircularReveal(mBinding.reveal, x, y, 56, finalRadius);
            reveal.setDuration(350);
            reveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    //finish();
                    //  reset();
                }});

            reveal.start();

            fragment = new AddPasswordFragment();
            FragmentManager manager = getSupportFragmentManager();
            final FragmentTransaction transaction = manager.beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.fragment, fragment);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    transaction.commit();
                    mBinding.reveal.setVisibility(View.INVISIBLE);
                }
            }, 300);
        }

        else{
            animateFab();
            mBinding.reveal.setVisibility(View.VISIBLE);
            int x = mBinding.layoutContent.getRight();
            int y = mBinding.layoutContent.getBottom();

            int startRadius = Math.max(mBinding.layoutContent.getWidth(), mBinding.layoutContent.getHeight());
            int endRadius = 0;

            Animator anim = ViewAnimationUtils.createCircularReveal(mBinding.reveal, x, y, startRadius, endRadius);
            anim.setDuration(350);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                }
            });
            anim.start();
            fragment = new DefaultFragment();
            FragmentManager manager = getSupportFragmentManager();
            final FragmentTransaction transaction = manager.beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.fragment, fragment);
            transaction.commit();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //transaction.commit();
                    mBinding.reveal.setVisibility(View.INVISIBLE);
                }
            }, 300);
        }







        ///extras = new Bundle();
        ///extras = fragment.getArguments().getBundle("password");
        ///Password password = (Password) extras.getSerializable("password");
        ///mDb.addPassword(mUser, password);
        //animateFab();
        //Intent intent = new Intent(this, AddPasswordActivity.class);
        //Bundle extras = new Bundle();
        //extras.putSerializable("mUser", mUser);
        //intent.putExtras(extras);
        //startActivityForResult(intent, 2);
        //mFab.startAnimation(mRotateBackward);
        //startActivity(intent);
    }

    public void animateFab() {
        if (mIsFabOpen) {
            mFab.startAnimation(mRotateBackward);
            mIsFabOpen = false;
        }
        else{
            mFab.startAnimation(mRotateForward);
            mIsFabOpen = true;
        }
    }

    public User getUser(){
        return mUser;
    }

}
