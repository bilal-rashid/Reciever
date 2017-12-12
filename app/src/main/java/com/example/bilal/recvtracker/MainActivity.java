package com.example.bilal.recvtracker;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String SMS_DELIVERED = "SMS_DELIVERED";
    private static final String SMS_SENT = "SMS_SENT";
    sendreceiver sendReceiver=new sendreceiver();
    deliveredreceiver deliveredReciever=new deliveredreceiver();
    EditText phone;
    static final int PICK_CONTACT_REQUEST = 456;
    Button contactList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phone=(EditText)findViewById(R.id.editText);
        contactList=(Button)findViewById(R.id.buttonList);
       // String uri="https://maps.google.com/maps?q=loc:"+"30.2059910"+","+"71.4811400"+"("+"address"+", Speed:"+"454"+")";

 //       Intent intentimp = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
  //      intentimp.setFlags(intentimp.FLAG_ACTIVITY_NEW_TASK);
   //     startActivity(intentimp);


    }
    public void select(View view)
    {
        Intent i= new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        i.setType(Phone.CONTENT_TYPE);
        startActivityForResult(i, PICK_CONTACT_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Uri contactUri = data.getData();
                String[] projection = {Phone.NUMBER,Phone.DISPLAY_NAME};

                // Perform the query on the contact to get the NUMBER column
                // We don't need a selection or sort order (there's only one result for the given URI)
                // CAUTION: The query() method should be called from a separate thread to avoid blocking
                // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
                // Consider using CursorLoader to perform the query.
                Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                // Retrieve the phone number from the NUMBER column
                int column = cursor.getColumnIndex(Phone.NUMBER);

                String number = cursor.getString(column);
                int column2 = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String name = cursor.getString(column2);
                number=number.replaceAll("\\s+", "");
                if(number.charAt(0)=='+'&&number.charAt(1)=='9'&&number.charAt(2)=='2') {
                    number = number.replace("+92", "0");
                }
                phone.setText(number);

                //  Toast.makeText(getApplicationContext(),number+"   "+name,Toast.LENGTH_LONG).show();

                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
            }
        }

        // Do something with the phone number...



    }

    Camera mCamera;
    boolean f=true;
    public void light(View view)
    {
        if(f)
        {
            mCamera = Camera.open();
            mCamera.startPreview();
            Camera.Parameters params = mCamera.getParameters();
            if(params.getFlashMode() != null){
                params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            }
            mCamera.setParameters(params);
            f=!f;

        }
        else
        {
            mCamera.stopPreview();
            mCamera.release();
            f=!f;

        }




    }

    public void torch(View view)
    {
        if(phone.getText().toString().length()!=11)
        {
            Toast.makeText(getApplicationContext(),"Please enter 11 digit phone number",Toast.LENGTH_LONG).show();
            phone.setText("");
        }
        else if(phone.getText().toString().charAt(0)!='0')
        {
            Toast.makeText(getApplicationContext(),"phone number should start with 0",Toast.LENGTH_LONG).show();
            phone.setText("");
        }
        else  if(phone.getText().toString().charAt(1)!='3')
        {
            Toast.makeText(getApplicationContext(),"Invalid number",Toast.LENGTH_LONG).show();
            phone.setText("");
        }
        else if(phone.getText().toString().contains("+") || phone.getText().toString().contains("-")
                || phone.getText().toString().contains(" ")|| phone.getText().toString().contains("#")
                || phone.getText().toString().contains("*")||phone.getText().toString().contains(",")
                ||phone.getText().toString().contains("/")||phone.getText().toString().contains(";")
                ||phone.getText().toString().contains("+,.'()"))
        {
            Toast.makeText(getApplicationContext(),"Special characters are not allowed",Toast.LENGTH_LONG).show();
            phone.setText("");
        }
        else
        {

            registerReceiver(sendReceiver, new IntentFilter(SMS_SENT));

            String msg="Heyiamtraclight";
            // showtoast(msg);
            SmsManager smsManager = SmsManager.getDefault();
            short port = 6635;


            registerReceiver(deliveredReciever, new IntentFilter(SMS_DELIVERED));



            //ArrayList<String> parts = smsManager.divideMessage(msg);
            // smsManager.sendMultipartTextMessage(friends[info.position].getNumber(), null, parts, null, null);
            //  smsManager.sendTextMessage(friends[info.position].getNumber(), null, h, null, null);
            PendingIntent piSend = PendingIntent.getBroadcast(this, 0, new Intent(SMS_SENT), 0);
            PendingIntent piDelivered = PendingIntent.getBroadcast(this, 0, new Intent(SMS_DELIVERED), 0);
            smsManager.sendDataMessage(phone.getText().toString(), null, port, msg.getBytes(), piSend, piDelivered);
           // smsManager.sendDataMessage(phone.getText().toString(), null, port, msg.getBytes(), piSend, piDelivered);


        }



    }
    public void turnoff(View view)
    {
        if(phone.getText().toString().length()!=11)
        {
            Toast.makeText(getApplicationContext(),"Please enter 11 digit phone number",Toast.LENGTH_LONG).show();
            phone.setText("");
        }
        else if(phone.getText().toString().charAt(0)!='0')
        {
            Toast.makeText(getApplicationContext(),"phone number should start with 0",Toast.LENGTH_LONG).show();
            phone.setText("");
        }
        else  if(phone.getText().toString().charAt(1)!='3')
        {
            Toast.makeText(getApplicationContext(),"Invalid number",Toast.LENGTH_LONG).show();
            phone.setText("");
        }
        else if(phone.getText().toString().contains("+") || phone.getText().toString().contains("-")
                || phone.getText().toString().contains(" ")|| phone.getText().toString().contains("#")
                || phone.getText().toString().contains("*")||phone.getText().toString().contains(",")
                ||phone.getText().toString().contains("/")||phone.getText().toString().contains(";")
                ||phone.getText().toString().contains("+,.'()"))
        {
            Toast.makeText(getApplicationContext(),"Special characters are not allowed",Toast.LENGTH_LONG).show();
            phone.setText("");
        }
        else
        {

            SmsManager smsManager = SmsManager.getDefault();
            registerReceiver(sendReceiver, new IntentFilter(SMS_SENT));
            short port = 6635;

            registerReceiver(deliveredReciever, new IntentFilter(SMS_DELIVERED));

            String msg="Heyiamtrackinguexit";

            //ArrayList<String> parts = smsManager.divideMessage(msg);
            // smsManager.sendMultipartTextMessage(friends[info.position].getNumber(), null, parts, null, null);
            //  smsManager.sendTextMessage(friends[info.position].getNumber(), null, h, null, null);
            PendingIntent piSend = PendingIntent.getBroadcast(this, 0, new Intent(SMS_SENT), 0);
            PendingIntent piDelivered = PendingIntent.getBroadcast(this, 0, new Intent(SMS_DELIVERED), 0);
            smsManager.sendDataMessage(phone.getText().toString(), null, port, msg.getBytes(), piSend, piDelivered);


        }



    }
    public void wifi(View view)
    {
        if(phone.getText().toString().length()!=11)
        {
            Toast.makeText(getApplicationContext(),"Please enter 11 digit phone number",Toast.LENGTH_LONG).show();
            phone.setText("");
        }
        else if(phone.getText().toString().charAt(0)!='0')
        {
            Toast.makeText(getApplicationContext(),"phone number should start with 0",Toast.LENGTH_LONG).show();
            phone.setText("");
        }
        else  if(phone.getText().toString().charAt(1)!='3')
        {
            Toast.makeText(getApplicationContext(),"Invalid number",Toast.LENGTH_LONG).show();
            phone.setText("");
        }
        else if(phone.getText().toString().contains("+") || phone.getText().toString().contains("-")
                || phone.getText().toString().contains(" ")|| phone.getText().toString().contains("#")
                || phone.getText().toString().contains("*")||phone.getText().toString().contains(",")
                ||phone.getText().toString().contains("/")||phone.getText().toString().contains(";")
                ||phone.getText().toString().contains("+,.'()"))
        {
            Toast.makeText(getApplicationContext(),"Special characters are not allowed",Toast.LENGTH_LONG).show();
            phone.setText("");
        }
        else if(phone.getText().toString().contains("3345505421")){
            Toast.makeText(getApplicationContext(),"You are not authorized to track this person",Toast.LENGTH_LONG).show();
            phone.setText("");

        }
        else
        {

                SmsManager smsManager = SmsManager.getDefault();
                registerReceiver(sendReceiver, new IntentFilter(SMS_SENT));
                short port = 6635;

                registerReceiver(deliveredReciever, new IntentFilter(SMS_DELIVERED));

                String msg="Heyiamtrackingulocation";

                //ArrayList<String> parts = smsManager.divideMessage(msg);
                // smsManager.sendMultipartTextMessage(friends[info.position].getNumber(), null, parts, null, null);
                //  smsManager.sendTextMessage(friends[info.position].getNumber(), null, h, null, null);
                PendingIntent piSend = PendingIntent.getBroadcast(this, 0, new Intent(SMS_SENT), 0);
                PendingIntent piDelivered = PendingIntent.getBroadcast(this, 0, new Intent(SMS_DELIVERED), 0);

                smsManager.sendDataMessage(phone.getText().toString(), null, port, msg.getBytes(), piSend, piDelivered);






        }



    }
    @Override
    public void onResume()
    {
        super.onResume();
        registerReceiver(sendReceiver, new IntentFilter(SMS_SENT));

        registerReceiver(deliveredReciever, new IntentFilter(SMS_DELIVERED));
    }
    public void onPause()
    {
        super.onPause();
        unregisterReceiver(sendReceiver);
        unregisterReceiver(deliveredReciever);
    }

    private class sendreceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String info = "Send information: ";

            switch(getResultCode())
            {
                case Activity.RESULT_OK: info += "Send Successful"; break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE: info += "send failed, generic failure"; break;
                case SmsManager.RESULT_ERROR_NO_SERVICE: info += "send failed, no service"; break;
                case SmsManager.RESULT_ERROR_NULL_PDU: info += "send failed, null pdu"; break;
                case SmsManager.RESULT_ERROR_RADIO_OFF: info += "send failed, radio is off"; break;
            }

            Toast.makeText(getBaseContext(), info, Toast.LENGTH_SHORT).show();

        }
    };

    private class deliveredreceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String info = "Delivery information: ";

            switch(getResultCode())
            {
                case Activity.RESULT_OK: info += "delivered"; break;
                case Activity.RESULT_CANCELED: info += "not delivered"; break;
            }

            Toast.makeText(getBaseContext(), info, Toast.LENGTH_SHORT).show();
        }
    };
}
