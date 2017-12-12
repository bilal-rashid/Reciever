package com.example.bilal.recvtracker;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by asimh on 25-Mar-16.
 */
public class notificationReceiver extends BroadcastReceiver {
    Context context;

    private String TAG = notificationReceiver.class.getSimpleName();

    public notificationReceiver(){
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            this.context = context;

            Bundle bundle = intent.getExtras();

            SmsMessage[] msgs = null;

            String message = "";


            if (bundle != null) {
                // Retrieve the Binary SMS data
                Object[] pdus = (Object[]) bundle.get("pdus");
                msgs = new SmsMessage[pdus.length];
//                Log.d("hello", pdus.length + "");

                // For every SMS message received (although multipart is not supported with binary)
                msgs[0] = SmsMessage.createFromPdu((byte[]) pdus[0]);


               // Log.d("hello", msgs[0].getUserData() + "");
                String str="";
                byte[] data = null;

                data=msgs[0].getUserData();


                for(int index=0; index<data.length; ++index)
                {
                    str += Character.toString((char)data[index]);


                }
//                Log.d("hello",str+"");


                // Return the User Data section minus the
                // User Data Header (UDH) (if there is any UDH at all)
                // Return the User Data section minus the
                // User Data Header (UDH) (if there is any UDH at all)
               // message = msgs[0].getMessageBody();
               // Log.d("helloi", message);
                Toast.makeText(context, "" + message, Toast.LENGTH_LONG).show();
                String[] attributes = str.split("-");
                double lat = Double.parseDouble(attributes[0]);
                double longi = Double.parseDouble(attributes[1]);
                double speed = Double.parseDouble(attributes[2]);
                Geocoder gc = new Geocoder(context);
                String addrr = "";
                try {
                    List<Address> list = gc.getFromLocation(lat, longi, 1);
                    Address add = list.get(0);
               /* addrr; */
//                    Log.d("adreess", "Admin Area " + add.getAdminArea());
//                    Log.d("adreess", "Country code " + add.getCountryCode());
//                    Log.d("adreess", "Country name " + add.getCountryName());
//                    Log.d("adreess", "feature name " + add.getFeatureName());
//                    Log.d("adreess", "phone " + add.getPhone());
//                    Log.d("adreess", "postal code " + add.getPostalCode());
//                    Log.d("adreess", "premises " + add.getPremises());
//                    Log.d("adreess", "Sub admin " + add.getSubAdminArea());
//                    Log.d("adreess", "Sub locality " + add.getSubLocality());
//                    Log.d("adreess", "subThoroughfare " + add.getSubThoroughfare());
//                    Log.d("adreess", "Thoroughfare  " + add.getThoroughfare());
                    addrr = addrr + add.getLocality() + "," + add.getSubLocality() + "," + add.getFeatureName();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                //  String uri = String.format(Locale.ENGLISH, "geo:%f,%f", lat,
                //   longi);
                String uri = "";


                if (Connectivitycheck.isNetworkAvailable(context)) {
                    Toast.makeText(context, addrr, Toast.LENGTH_LONG).show();
                    uri = uri + "https://maps.google.com/maps?q=loc:" + lat + "," + longi + "(" + addrr + ", Speed:" + speed + ")";
                    //   uri=uri+"geo:<"+"lat"+">,<"+"long"+">?q=<"+lat+">,<"+longi+">("+addrr+", Speed:"+speed+")";

                } else {
                    uri = uri + "https://maps.google.com/maps?q=loc:" + lat + "," + longi + "(" + "Unknown Location" + ", Speed:" + speed + ")";
                    //  uri="geo:<"+"lat"+">,<"+"long"+">?q=<"+lat+">,<"+longi+">("+"Unknown Location, Speed:"+speed+")";
                }

                Intent intentimp = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intentimp.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentimp);
//                Log.d("locattt", "" + str);



            }
        }catch (Exception e){
            Toast.makeText(context,"Couldn't Track",Toast.LENGTH_SHORT).show();

        }
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public String concat(String [] strArr)
    {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < strArr.length; i++) {
            strBuilder.append(strArr[i]);
        }
        return strBuilder.toString();
    }
}