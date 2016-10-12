package com.example.administrator.hellowbaidumap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by Administrator on 2016/10/7.
 */
public class SMSBroadcastReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Object[] pdus = (Object[]) bundle.get("pdus");
        SmsMessage[] messages = new SmsMessage[pdus.length];
        for(int i = 0; i < messages.length; i++){
            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
        }
        final String address = messages[0].getOriginatingAddress();

        String fullMessage = "";
        for (SmsMessage message : messages) {
            fullMessage += message.getMessageBody();
        }

        if (fullMessage.contains("where are you")) {
            String myLocation = String.valueOf(MainActivity.myLatitude) + "/" +
                    String.valueOf(MainActivity.myLongitude);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(address, null, myLocation, null, null);
        }

        boolean if_from_my_friends = false;
        SomeBody someBody=null;
        for(SomeBody sb:SomeBody.friends){
            if(sb.getPhoneNumber().equals(address)){
                someBody = sb;
                if_from_my_friends = true;
                break;
            }
        }
        if (!if_from_my_friends) {
            for (SomeBody sb:SomeBody.enemies) {
                if (sb.getPhoneNumber().equals(address)) {
                    someBody = sb;
                    break;
                }
            }
        }

        if(someBody!= null&&fullMessage.matches("\\d+[.]\\d+/\\d+[.]\\d+")) {
                String[] ll = fullMessage.split("/");
                LatLng latLng = new LatLng(Double.parseDouble(ll[0]), Double.parseDouble(ll[1]));
                someBody.setLatLng(latLng);
        }
    }
}
