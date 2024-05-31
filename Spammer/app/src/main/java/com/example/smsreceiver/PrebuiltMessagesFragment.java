package com.example.smsreceiver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class PrebuiltMessagesFragment extends Fragment {
  private static final String PREF_NAME = "prebuilt_messages_pref";
  private static final String PREF_MESSAGES = "prebuilt_messages";
  
  private SharedViewModel viewModel;
  private PrebuiltMessageAdapter messageAdapter;
  private List<PrebuiltMessage> messageList;
  private EditText newMessageEditText;
  private SharedPreferences sharedPreferences;
  
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
  }
  
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_prebuilt_messages, container, false);
    
    sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    
    // Creating the recycle view for the message.
    RecyclerView recyclerView = view.findViewById(R.id.recycler_view_prebuilt_messages);
    
    newMessageEditText = view.findViewById(R.id.edit_text_new_message);
    Button addButton = view.findViewById(R.id.button_add_message);
    Log.d("PrebuiltMessages", "Attempt loading Message");
    messageList = loadMessages();
    // Initialize messageList with prebuilt messages
    
    messageAdapter = new PrebuiltMessageAdapter(messageList, this);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(messageAdapter);
    
    addButton.setOnClickListener(v -> addMessage());
    
    return view;
  }
  
  // Add a message
  private void addMessage() {
    String newMessage = newMessageEditText.getText().toString().trim();
    if (!newMessage.isEmpty()) {
      PrebuiltMessage message = new PrebuiltMessage(newMessage);
      messageList.add(message);
      saveMessages(messageList);
      messageAdapter.notifyDataSetChanged();
      newMessageEditText.setText("");
    }
  }
  
  // Save Messages to Keep
  private void saveMessages(List<PrebuiltMessage> messages) {
    SharedPreferences.Editor editor = sharedPreferences.edit();
    ObjectMapper mapper = new ObjectMapper();
    try {
      // Convert the object to json
      String json = mapper.writeValueAsString(messages);
      editor.putString(PREF_MESSAGES, json);
      editor.apply();
      Log.d("PrebuiltMessages", "Messages saved successfully: " + json);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }
  // Load previous message at the loading of the page.
  private List<PrebuiltMessage> loadMessages() {
    String json = sharedPreferences.getString(PREF_MESSAGES, null);
    List<PrebuiltMessage> messages = new ArrayList<>();
    if (json != null) {
      ObjectMapper mapper = new ObjectMapper();
      try {
        // Convert JSON to object
        messages = mapper.readValue(json, new TypeReference<List<PrebuiltMessage>>() {});
        Log.d("PrebuiltMessages", "Messages loaded successfully: " + json);
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
    }
    return messages;
  }
  
  
  // Method to uncheck other "Spam" checkboxes except last checked one
  private void uncheckOtherSpamCheckboxes(int currentPosition) {
    for (int i = 0; i < messageList.size(); i++) {
      if (i != currentPosition && messageList.get(i).isSpam()) {
        messageList.get(i).setSpam(false);
      }
    }
  }
  
  // Method to uncheck other "Automated Response" checkboxes except last checked one
  private void uncheckOtherAutomatedResponseCheckboxes(int currentPosition) {
    for (int i = 0; i < messageList.size(); i++) {
      if (i != currentPosition && messageList.get(i).isAutomatedResponse()) {
        messageList.get(i).setAutomatedResponse(false);
      }
    }
  }
  
  
  public static class PrebuiltMessageAdapter extends RecyclerView.Adapter<PrebuiltMessageAdapter.MessageViewHolder> {
    private final List<PrebuiltMessage> messageList;
    private final PrebuiltMessagesFragment fragment;
    
    public PrebuiltMessageAdapter(List<PrebuiltMessage> messageList, PrebuiltMessagesFragment fragment) {
      this.messageList = messageList;
      this.fragment = fragment;
    }
    
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View itemView = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_prebuilt_message, parent, false);
      return new MessageViewHolder(itemView);
    }
    
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
      PrebuiltMessage message = messageList.get(position);
      holder.messageTextView.setText(message.getMessage());
      holder.spamCheckBox.setChecked(message.isSpam());
      holder.automatedResponseCheckBox.setChecked(message.isAutomatedResponse());
      
      if (message.isSpam()){
        fragment.viewModel.setSpamMessage(message.getMessage());
      }
      if (message.isAutomatedResponse()){
        fragment.viewModel.setAutoMessage(message.getMessage());
      }
      
      // Setup delete button
      holder.deleteButton.setOnClickListener(v -> {
        // Show confirmation dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
        builder.setMessage("Are you sure you want to delete this message?")
            .setPositiveButton("Yes", (dialog, which) -> {
              // Delete the message and if it was check remove the message from the viewModel
              if (holder.spamCheckBox.isChecked()){
                fragment.viewModel.setSpamMessage(null);
              }
              if (holder.automatedResponseCheckBox.isChecked()){
                fragment.viewModel.setAutoMessage(null);
              }
              messageList.remove(position);
              fragment.saveMessages(messageList);
              notifyDataSetChanged();
            })
            .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
            .show();
      });
      
      // Spam Check box
      holder.spamCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
        if (isChecked) {
          holder.automatedResponseCheckBox.setChecked(false);
          message.setSpam(true);
          message.setAutomatedResponse(false);
          
          fragment.viewModel.setSpamMessage(String.valueOf(holder.messageTextView.getText()));
          fragment.uncheckOtherSpamCheckboxes(position);
          fragment.saveMessages(messageList);
          notifyDataSetChanged();
        }
      });
      
      // Automated Response box
      holder.automatedResponseCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
        if (isChecked) {
          holder.spamCheckBox.setChecked(false);
          // Update message as automated response
          message.setSpam(false);
          message.setAutomatedResponse(true);
          fragment.viewModel.setAutoMessage(String.valueOf(holder.messageTextView.getText()));
          fragment.uncheckOtherAutomatedResponseCheckboxes(position);
          fragment.saveMessages(messageList);
          
          notifyDataSetChanged();
        }
      });
    }
    
    @Override
    public int getItemCount() {
      return messageList.size();
    }
    
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
      public TextView messageTextView;
      public Button deleteButton;
      public CheckBox spamCheckBox;
      public CheckBox automatedResponseCheckBox;
      
      public MessageViewHolder(View itemView) {
        super(itemView);
        messageTextView = itemView.findViewById(R.id.text_view_message);
        deleteButton = itemView.findViewById(R.id.button_delete);
        spamCheckBox = itemView.findViewById(R.id.checkbox_spam);
        automatedResponseCheckBox = itemView.findViewById(R.id.checkbox_automated_response);
      }
    }
  }
  
  public static class PrebuiltMessage {
    private String message;
    private boolean isSpam;
    private boolean isAutomatedResponse;
    
    public PrebuiltMessage(String message) {
      this.message = message;
      this.isSpam = false;
      this.isAutomatedResponse = false;
    }
    
    public PrebuiltMessage() {
    }
    
    public String getMessage() {
      return message;
    }
    
    public void setMessage(String message) {
      this.message = message;
    }
    
    public boolean isSpam() {
      return isSpam;
    }
    
    public void setSpam(boolean spam) {
      isSpam = spam;
    }
    
    public boolean isAutomatedResponse() {
      return isAutomatedResponse;
    }
    
    public void setAutomatedResponse(boolean automatedResponse) {
      isAutomatedResponse = automatedResponse;
    }
  }
}
