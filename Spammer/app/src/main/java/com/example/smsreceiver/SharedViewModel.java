package com.example.smsreceiver;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// View Containing all Information shared between Fragments
public class SharedViewModel extends AndroidViewModel {
  private static final String PREFS_NAME = "MyAppPrefs";
  private static final String KEY_SPAM_MESSAGE = "spam_message";
  private static final String KEY_AUTO_MESSAGE = "auto_message";
  private static final String KEY_AUTO_REPLY = "auto_reply";
  private static final String KEY_AUTO_CONTACTS = "auto_contacts";
  
  private List<ContactsFragment.Contact> contacts;
  private final List<ContactsFragment.Contact> autoContacts = new ArrayList<>();
  private String spamMessage;
  private String autoMessage;
  private boolean autoReply;
  
  private final SharedPreferences sharedPreferences;
  private final ObjectMapper objectMapper;
  
  public SharedViewModel(@NonNull Application application) {
    super(application);
    sharedPreferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    objectMapper = new ObjectMapper();
    loadPreferences();
  }
  
  public String getSpamMessage() {
    return spamMessage;
  }
  
  public void setSpamMessage(String spamMessage) {
    this.spamMessage = spamMessage;
  }
  
  public String getAutoMessage() {
    return autoMessage;
  }
  
  public void setAutoMessage(String autoMessage) {
    this.autoMessage = autoMessage;
  }
  
  public List<ContactsFragment.Contact> getContacts() {
    return contacts;
  }
  
  public void setContacts(List<ContactsFragment.Contact> contacts) {
    this.contacts = contacts;
  }
  
  public List<ContactsFragment.Contact> getAutoContacts() {
    return autoContacts;
  }
  
  public boolean isAutoReply() {
    return autoReply;
  }
  
  public void setAutoReply(boolean autoReply) {
    this.autoReply = autoReply;
  }
  
  public void addAutoContacts(ContactsFragment.Contact contact) {
    autoContacts.add(contact);
  }
  
  public void removeAutoContacts(ContactsFragment.Contact contact) {
    autoContacts.remove(contact);
  }
  
  private void loadPreferences() {
    spamMessage = sharedPreferences.getString(KEY_SPAM_MESSAGE, "");
    autoMessage = sharedPreferences.getString(KEY_AUTO_MESSAGE, "");
    autoReply = sharedPreferences.getBoolean(KEY_AUTO_REPLY, false);
    String json = sharedPreferences.getString(KEY_AUTO_CONTACTS, "[]");
    try {
      List<ContactsFragment.Contact> contactsList = objectMapper.readValue(json, new TypeReference<List<ContactsFragment.Contact>>() {});
      autoContacts.addAll(contactsList);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
