package edu.khai.csn.abondar.passwordmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class PasswordDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_details);

        TextView tvService = findViewById(R.id.passwordDetailsService);
        String str = getIntent().getStringExtra("service");
        tvService.setText(str);
    }
}
