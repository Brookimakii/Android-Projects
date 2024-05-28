package com.example.smsreceiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    Bundle bundle = intent.getExtras();
    if (bundle != null) {
      Object[] pdus = (Object[]) bundle.get("pdus");
      if (pdus != null) {
        for (Object pdu : pdus) {
          SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
          String sender = smsMessage.getDisplayOriginatingAddress();
          String messageBody = smsMessage.getMessageBody();
          
          // Start the AutoResponseService to handle the automatic response
          Intent serviceIntent = new Intent(context, SendMessageService.class);
          serviceIntent.putExtra("sender", sender);
          serviceIntent.putExtra("messageBody", messageBody);
          
          context.startService(serviceIntent);
        }
      }
    }
  }
}
