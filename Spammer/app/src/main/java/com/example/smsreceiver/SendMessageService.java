package com.example.smsreceiver;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.telephony.SmsManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;

public class SendMessageService extends Service {
  private static final String PREFS_NAME = "MyAppPrefs";
  private static final String KEY_AUTO_MESSAGE = "auto_message";
  private static final String KEY_AUTO_REPLY = "auto_reply";
  private static final String KEY_AUTO_CONTACTS = "auto_contacts";
  
  private SharedPreferences sharedPreferences;
  private ObjectMapper objectMapper;
  
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    objectMapper = new ObjectMapper();
    
    // Get the sender and message body from the intent extras
    String sender = intent.getStringExtra("sender");
    String messageBody = intent.getStringExtra("messageBody");
    
    boolean autoReplyEnabled = sharedPreferences.getBoolean(KEY_AUTO_REPLY, false);
    if (!autoReplyEnabled) {
      stopSelf();
      return START_NOT_STICKY;
    }
    
    // Get the list of auto contacts
    String json = sharedPreferences.getString(KEY_AUTO_CONTACTS, "[]");
    List<ContactsFragment.Contact> autoContacts = new ArrayList<>();
    try {
      autoContacts = objectMapper.readValue(json, new TypeReference<List<ContactsFragment.Contact>>() {});
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    
    boolean senderIsAutoContact = false;
    for (ContactsFragment.Contact contact : autoContacts) {
      if (contact.getPhoneNumber().equals(sender)) {
        senderIsAutoContact = true;
        break;
      }
    }
    
    if (senderIsAutoContact) {
      // Get the auto message
      String autoMessage = sharedPreferences.getString(KEY_AUTO_MESSAGE, "");
      
      // Send the auto reply
      sendAutoReply(sender, autoMessage);
    }
    // Stop the service once the operation is completed
    stopSelf();
    
    return START_NOT_STICKY;
  }
  
  private static boolean isContactAmongAutoReplyList(String sender, List<ContactsFragment.Contact> contacts) {
    for (ContactsFragment.Contact contact : contacts){
      if (sender.equals(contact.getPhoneNumber())){
        return true;
      }
    }
    
    return false;
  }
  
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
  
  private void sendAutoReply(String contact, String message) {
    try {
      SmsManager smsManager = SmsManager.getDefault();
      smsManager.sendTextMessage(contact, null, message, null, null);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

