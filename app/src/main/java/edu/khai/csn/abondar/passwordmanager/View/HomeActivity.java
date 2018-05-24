package edu.khai.csn.abondar.passwordmanager.View;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.content.Intent;
import android.databinding.DataBindingUtil;

import java.util.ArrayList;

import edu.khai.csn.abondar.passwordmanager.Model.DBHelper;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.Password;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.User;
import edu.khai.csn.abondar.passwordmanager.R;
import edu.khai.csn.abondar.passwordmanager.Presenter.RecyclerAdapter;
import edu.khai.csn.abondar.passwordmanager.databinding.ActivityHomeBinding;

/**
 * Class represents home screen activity
 */
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
    private android.support.v7.widget.Toolbar mToolBar;

    /**
     * Method that is responsible for activity creation
     * called automatically before activity started
     *
     * @param savedInstanceState helps to recreate activity state when it reopened
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Connect java code and xml markup
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        initialize();

        buildRecyclerView();
    }

    /**
     * Initialization of necessary variables and
     * connection controls from the markup with the corresponding variables
     */
    private void initialize() {
        // Get information about current logged in user
        getCurrentUser();

        // Get list of passwords from database
        mDb = new DBHelper(this);
        mPasswordList = mDb.getPassword(mUser.getUsername());

        // Set custom tool bar instead of standard actionbar
        mToolBar = findViewById(R.id.navAction);
        setSupportActionBar(mToolBar);

        // Initialize floating animation button
        mFab = findViewById(R.id.fab);
        // Initialize drawer layout
        mDrawerLayout = findViewById(R.id.drawerLayout);

        // Initialize animation variables
        mRotateBackward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        mRotateForward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);

        // Set layout for navigation view
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        // Set navigation view open button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0f0c29")));

        // Replace standard toolbar menu with navigation view
        NavigationView navigationView = mBinding.navigationView;
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Set recycler view to display list of passwords
     */
    public void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mAdapter = new RecyclerAdapter(mPasswordList, this);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Getting a current logged in user
     */
    private void getCurrentUser() {
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            mUser = (User) extras.getSerializable("user");
    }

    /**
     * Handling selection menu items
     *
     * @param item selected menu item
     * @return was item been selected or wasn't
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    /**
     * Continue working with the activity after it was paused and reopened
     */
    @Override
    public void onStart() {
        super.onStart();

        // Get password list from database
        mPasswordList = mDb.getPassword(mUser.getUsername());
        // Set adapter for recycler view
        mAdapter = new RecyclerAdapter(mPasswordList, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Menu creation, setting search button
     *
     * @param menu menu
     * @return successfully created menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.navigation_menu_search, menu);
        // Connection with menu xml markup
        MenuItem menuItem = menu.findItem(R.id.action_search);
        // Set search view on the toolbar
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        // Set listener when text is changed
        searchView.setOnQueryTextListener(this);

        return true;
    }

    /**
     * Submit inputted text in the search field
     *
     * @param s
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    /**
     * Change display list passwords when search field is changed
     *
     * @param s string that was inputted in search field
     * @return
     */
    @Override
    public boolean onQueryTextChange(String s) {

        s = s.toLowerCase();

        // Create new list for found passwords
        ArrayList<Password> newList = new ArrayList<>();

        // Look for suitable passwords and add them to new list
        for (Password password : mPasswordList) {
            String serviceName = password.getServiceName().toLowerCase();

            if (serviceName.contains(s))
                newList.add(password);
        }

        // Set filter to display only password
        // that are suit to the search query
        mAdapter.setFilter(newList);

        return true;
    }

    /**
     * Disallow move to previous activity
     * on click to system back button
     * pauses current activity
     */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    /**
     * Handling what items from navigation view are selected
     *
     * @param item
     * @return successfully selected item
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // Identify what item was selected
        switch (id) {
            case R.id.nav_settings:
                // Create new intent of Settings activity
                Intent intent = new Intent(this, SettingsActivity.class);
                // Create new bundle to transfer data to new activity
                Bundle extras = new Bundle();
                // Add data to bundle
                extras.putSerializable("passwords", mPasswordList);
                extras.putSerializable("user", getUser());
                // Add bundle to intent
                intent.putExtras(extras);
                // Start new activity
                startActivityForResult(intent, 1101);
                break;
            case R.id.nav_logout:
                // Finnish current activity, go to the login activity
                LoginActivity login = (LoginActivity) getParent();
                login.mBinding.reveal.setVisibility(View.INVISIBLE);
                finish();
                return false;
        }
        // Close drawer
        mDrawerLayout.closeDrawers();
        return false;
    }

    /**
     * Handler floating action button click
     *
     * @param v view
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void addPassword(View v) {

        if (!mIsFabOpen) {
            openFabAnim();
        } else {
            closeFabAnim();
        }
    }

    /**
     * Reveal Animation opening add password fragment
     */
    private void openFabAnim() {
        Fragment fragment;
        // Animate floating action button, spin
        animateFab();

        mBinding.fab.setElevation(0f);
        mBinding.reveal.setVisibility(View.VISIBLE);

        // Coordinates from where start revelation
        int cx = mBinding.reveal.getWidth();
        int cy = mBinding.reveal.getHeight();

        // Coordinates till where continue revelation
        int x = (int) (mBinding.fab.getMeasuredHeight() / 2 + mBinding.fab.getX());
        int y = (int) (mBinding.fab.getMeasuredHeight() / 2 + mBinding.fab.getY());


        float finalRadius = Math.max(cx, cy) * 1.2f;

        // Initialize animation
        Animator reveal = ViewAnimationUtils.createCircularReveal(mBinding.reveal, x, y, 56, finalRadius);
        reveal.setDuration(350);
        reveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });

        // Start animation
        reveal.start();

        // Create new fragment
        fragment = new AddPasswordFragment();
        FragmentManager manager = getSupportFragmentManager();
        final FragmentTransaction transaction = manager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.fragment, fragment);

        // Perform delayed fragment opening in separate thread
        // to let the revelation animation end
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                transaction.commit();
                mBinding.reveal.setVisibility(View.INVISIBLE);
            }
        }, 300);
    }

    /**
     * Reveal Animation opening add password fragment
     */
    private void closeFabAnim() {
        Fragment fragment;
        // Animate floating action button, spin
        animateFab();

        mBinding.reveal.setVisibility(View.VISIBLE);

        // Coordinates till where continue revelation
        int x = mBinding.layoutContent.getRight();
        int y = mBinding.layoutContent.getBottom();

        // Coordinates from where start revelation
        int startRadius = Math.max(mBinding.layoutContent.getWidth(), mBinding.layoutContent.getHeight());
        int endRadius = 0;

        // Initialize animation
        Animator anim = ViewAnimationUtils.createCircularReveal(mBinding.reveal, x, y, startRadius, endRadius);
        anim.setDuration(350);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });

        // Start animation
        anim.start();

        // Create new fragment
        fragment = new DefaultFragment();
        FragmentManager manager = getSupportFragmentManager();
        final FragmentTransaction transaction = manager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();

        // Delayed setting view invisible
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBinding.reveal.setVisibility(View.INVISIBLE);
            }
        }, 300);
    }

    /**
     * Animate floating action button
     */
    public void animateFab() {
        if (mIsFabOpen)
            mFab.startAnimation(mRotateBackward);
        else
            mFab.startAnimation(mRotateForward);

        mIsFabOpen = !mIsFabOpen;
    }

    /**
     * @return mUser
     */
    public User getUser() {
        return mUser;
    }

    /**
     * Getting data from child activities
     * @param requestCode what code child activity was started with
     * @param resultCode what code child activity returned
     * @param data data from child activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 1101) {
            // Getting data using bundle
            Bundle extras = data.getExtras();
            // Getting new passwords, that was imported through xml file
            ArrayList<Password> _passwordList = (ArrayList<Password>) extras.getSerializable("imported");
            mDb = new DBHelper(this);
            int i = mPasswordList.size();

            // Adding new passwords to database
            for (; i < _passwordList.size(); i++) {
                mDb.addPassword(mUser, _passwordList.get(i));
            }
        }
    }
}