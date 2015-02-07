package com.ulluna.braillemessaging;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

/**
 * Created by Tomasz Czernuszenko on 17.02.14.
 * Klasa uruchamiająca ReadSMSActivity po otrzymaniu SMSa
 */
public class SmsListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String str = "";
        if (bundle != null)
        {
            //przetwarzanie wiadomości SMS
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i=0; i<msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                str += msgs[i].getMessageBody().toString();
            }

            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
            //otwarcie ReadSMSActivity i przekazanie mu zawartości SMSa
            Intent open = new Intent(context, ReadSMSActivity.class);
            open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            open.putExtra("key", str);
            context.startActivity(open);



        }

    }
}
