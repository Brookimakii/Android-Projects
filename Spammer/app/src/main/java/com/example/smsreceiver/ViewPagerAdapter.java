package com.example.smsreceiver;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
  public ViewPagerAdapter(FragmentActivity fa) {
    super(fa);
  }
  
  @NonNull
  @Override
  public Fragment createFragment(int position) {
    // Switch to control witch fragment is active
    switch (position) {
      case 1:
        return new PrebuiltMessagesFragment();
      case 2:
        return new ContactSpamFragment();
      case 0:
      default:
        return new ContactsFragment();
    }
  }
  
  @Override
  public int getItemCount() {
    return 3;
  }
}

