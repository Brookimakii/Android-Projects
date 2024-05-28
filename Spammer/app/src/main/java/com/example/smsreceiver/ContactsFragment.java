package com.example.smsreceiver;

import android.Manifest;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment {
  private SharedViewModel viewModel;
  private RecyclerView recyclerView;
  private ContactAdapter contactAdapter;
  private List<Contact> contactList;
  
  
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    
    // Fetch contacts and update ViewModel
    List<Contact> fetchedContacts = getContacts();
    viewModel.setContacts(fetchedContacts);
  }
  
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_contacts, container, false);
    recyclerView = view.findViewById(R.id.recycler_view_contacts);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    contactList = viewModel.getContacts();
    contactAdapter = new ContactAdapter(contactList, this);
    recyclerView.setAdapter(contactAdapter);
    
    return view;
  }
  
  private List<Contact> getContacts() {
    List<Contact> contacts = new ArrayList<>();
    Cursor cursor = getActivity().getContentResolver().query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
    while (cursor.moveToNext()) {
      String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
      String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
      Contact contact = new Contact(name, phoneNumber);
      contacts.add(contact);
    }
    cursor.close();
    return contacts;
  }
  
  public static class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private final List<Contact> contactList;
    private final ContactsFragment fragment;
    
    public ContactAdapter(List<Contact> contactList, ContactsFragment fragment) {
      this.contactList = contactList;
      this.fragment = fragment;
    }
    
    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View itemView = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_contact, parent, false);
      return new ContactViewHolder(itemView);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
      Contact contact = contactList.get(position);
      holder.nameTextView.setText(contact.getName());
      holder.phoneTextView.setText(contact.getPhoneNumber());
      holder.checkBox.setChecked(contact.isSelected());
      holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
        contact.setSelected(isChecked);
        if (isChecked) {
          fragment.viewModel.addAutoContacts(contact);
        } else {
          fragment.viewModel.removeAutoContacts(contact);
        }
      });
    }
    
    @Override
    public int getItemCount() {
      return contactList.size();
    }
    
    public static class ContactViewHolder extends RecyclerView.ViewHolder {
      public TextView nameTextView;
      public TextView phoneTextView;
      public CheckBox checkBox;
      
      public ContactViewHolder(View itemView) {
        super(itemView);
        nameTextView = itemView.findViewById(R.id.text_view_name);
        phoneTextView = itemView.findViewById(R.id.text_view_phone);
        checkBox = itemView.findViewById(R.id.checkbox_contact);
      }
    }
  }
  
  
  public static class Contact {
    private final String name;
    private final String phoneNumber;
    
    public void setSelected(boolean selected) {
      isSelected = selected;
    }
    
    private boolean isSelected;
    
    public Contact(String name, String phoneNumber) {
      this.name = name;
      this.phoneNumber = phoneNumber;
      this.isSelected = false;
    }
    
    public String getName() {
      return name;
    }
    
    public String getPhoneNumber() {
      return phoneNumber;
    }
    
    public boolean isSelected() {
      return isSelected;
    }
    
    // Getters and setters
  }
}
