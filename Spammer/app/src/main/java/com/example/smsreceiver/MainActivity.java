package com.example.smsreceiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
  private static final int PERMISSIONS_REQUEST_CODE = 1;
  private String[] permissions = {
      Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS,
      Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS
  };
  
  // Check if the permissions are granted.
  private boolean isPermissionGranted() {
    
    for (String perm : permissions) {
      if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
        // If a permission isn't granted return false.
        return false;
      }
    }
    return true;
    
  }
  
  private void requestPermissions() {
    ActivityCompat.requestPermissions(this, permissions, 0);
  }
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    setContentView(R.layout.activity_main);
    Log.d("Permission Status", String.valueOf(isPermissionGranted()));
    if (!isPermissionGranted()) {
      requestPermissions();
    }
    // Creating the View
    ViewPager2 viewPager = findViewById(R.id.viewPager);
    TabLayout tabLayout = findViewById(R.id.tabLayout);
    
    // Set the Adapter that control the fragments
    viewPager.setAdapter(new ViewPagerAdapter(this));
    
    new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
      switch (position) {
        case 0:
          tab.setText("Contacts");
          break;
        case 1:
          tab.setText("Responses");
          break;
        case 2:
          tab.setText("Send/Auto-Reply");
          break;
      }
    }).attach();
  }
  
}