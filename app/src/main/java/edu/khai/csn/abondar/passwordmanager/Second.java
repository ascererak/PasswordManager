package edu.khai.csn.abondar.passwordmanager;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import edu.khai.csn.abondar.passwordmanager.Model.Entities.DBHelper;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.Password;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.User;

public class Second extends AppCompatActivity implements SearchView.OnQueryTextListener, NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle Toggle;
    private ActionBar actionBar;
    private android.support.v7.widget.Toolbar mToolBar;
    private DBHelper db;
    private ArrayList<Password> passwordList;
    private FloatingActionButton fab;
    private Animation rotate_backward, rotate_forward;
    private Boolean isFabOpen = false;
    private User user;
    //private String[] c_services = {"Twitter", "Google", "Instagram"};
    //private String[] accountLogins = {"@ascererak", "a.bondar@student.csn.khai.edu", "@ascererak"};
    //private int[] accountImages = {R.drawable.ic_icon_twitter, R.drawable.ic_icon_google, R.drawable.ic_icon_instagram};
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Password> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Bundle extras = getIntent().getExtras();
        if(extras!=null)
            user = (User) extras.getSerializable("user");

        db = new DBHelper(this);
        passwordList = db.getPassword(user.getUsername());
        mToolBar = findViewById(R.id.navAction);
        setSupportActionBar(mToolBar);
        fab = findViewById(R.id.fab);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);

        mDrawerLayout = findViewById(R.id.drawerLayout);
        Toggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(Toggle);
        Toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0f0c29")));

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        //int count = 0;
//
        //for(Password name : passwordList) {//c_services) {
        //    arrayList.add(new Service(name, accountLogins[count], accountImages[count]));
        //    count++;
        //}


        adapter = new RecyclerAdapter(passwordList);
        recyclerView.setAdapter(adapter);

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        passwordList = db.getPassword(user.getUsername());
        adapter = new RecyclerAdapter(passwordList);
        recyclerView.setAdapter(adapter);
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
        for(Password password : arrayList){
            String serviceName = password.getServiceName().toLowerCase();

            if(serviceName.contains(s))
                newList.add(password);
        }
        adapter.setFilter(newList);
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
                fragment = new AccountFragment();
                break;
            case R.id.nav_import_export:
                fragment = new ImportExportFragment();
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                break;
            case R.id.nav_logout:
                finish();
                return false;
        }

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
        mDrawerLayout.closeDrawers();
        return false;
    }

    public void addPassword(View v){
        animateFab();
        Intent intent = new Intent(this, AddPasswordActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable("user", user);
        intent.putExtras(extras);
        startActivityForResult(intent, 2);
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
}
