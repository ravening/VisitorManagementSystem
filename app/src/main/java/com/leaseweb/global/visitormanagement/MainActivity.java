package com.leaseweb.global.visitormanagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void adminController(View view) {
        Intent adminAction = new Intent(this, LoginActivity.class);
        startActivity(adminAction);
    }

    public void guestController(View view) {
        Intent visitorAction = new Intent(this, GuestController.class);
        startActivity(visitorAction);
    }

}
