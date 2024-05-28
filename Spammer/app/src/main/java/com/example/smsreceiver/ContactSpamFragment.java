package com.example.smsreceiver;

import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import java.util.stream.Collectors;

public class ContactSpamFragment extends Fragment {
  private SharedViewModel viewModel;
  private Spinner contactSpinner;
  private Button spamButton;
  private CheckBox automatedResponseCheckBox;
  
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
  }
  
  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState
  ) {
    View view = inflater.inflate(R.layout.fragment_contact_spam, container, false);
    
    contactSpinner = view.findViewById(R.id.spinner_contacts);
    spamButton = view.findViewById(R.id.button_spam);
    automatedResponseCheckBox = view.findViewById(R.id.checkbox_automated_response);
    
    // Populate the spinner with dummy data (replace with your actual contact list)
    ArrayAdapter<String> adapter =
        new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item,
            viewModel.getContacts().stream()
                .map(contact -> contact.getName() + ": " + contact.getPhoneNumber())
                .collect(Collectors.toList())
        );
    contactSpinner.setAdapter(adapter);
    
    spamButton.setOnClickListener(v -> {
      // Start the service for spamming contacts
      // Pass the selected contact and other necessary data to the service
      startSpamService();
    });
    
    automatedResponseCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
      viewModel.setAutoReply(isChecked);
      if (isChecked) {
        String autoMessage = viewModel.getAutoMessage();
        if (autoMessage.isEmpty()) {
          viewModel.setAutoMessage("Default automated response message");
        }
      }
    });
    
    return view;
  }
  
  private void startSpamService() {
    // Get the selected contact from the spinner
    String selectedContact = contactSpinner.getSelectedItem().toString();
    
    // Create an Intent to start the SpamContactService
    Intent serviceIntent = new Intent(getActivity(), SpamContactService.class);
    // Pass the selected contact and any other necessary data to the service using extras
    serviceIntent.putExtra("selectedContact", selectedContact);
    serviceIntent.putExtra("message", viewModel.getSpamMessage());    // Start the service
    getActivity().startService(serviceIntent);
  }
}
