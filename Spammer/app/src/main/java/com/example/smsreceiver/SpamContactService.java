package com.example.smsreceiver;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import java.util.Objects;

public class SpamContactService extends Service {
  private SharedViewModel viewModel;
  
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    // Get the selected contact and other necessary data from the intent extras
    String selectedContact = intent.getStringExtra("selectedContact");
    String message = intent.getStringExtra("message");
    ;
    String number = selectedContact.substring(selectedContact.indexOf(":") + 1).trim();
    // Perform the spamming operation
    spamContact(number, message);
    
    // Stop the service once the operation is completed
    stopSelf();
    
    return START_NOT_STICKY;
  }
  
  private void spamContact(String contact, String message) {
    Log.d("PrebuiltMessages","Trying to send a '" + message +"' to " + contact + ".");
    try {
      SmsManager smsManager = SmsManager.getDefault();
      smsManager.sendTextMessage(contact, null, message, null, null);
      Toast.makeText(this, "Spam message sent to " + contact, Toast.LENGTH_SHORT).show();
    } catch (Exception e) {
      Toast.makeText(this, "Failed to send spam message to " + contact, Toast.LENGTH_SHORT).show();
      e.printStackTrace();
    }
  }
  
  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
