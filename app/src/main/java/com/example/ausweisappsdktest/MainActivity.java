package com.example.ausweisappsdktest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.governikus.ausweisapp2.IAusweisApp2Sdk;
import com.governikus.ausweisapp2.IAusweisApp2SdkCallback;

public class MainActivity extends AppCompatActivity {
    IAusweisApp2Sdk mSdk;
    LocalCallback mCallback = new LocalCallback();

    Button myButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myButton = findViewById(R.id.button);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    Log.i("before try", "try");
                    if (!mSdk.connectSdk(mCallback))
                    {
                        Log.i("error", "connection to sdk failed");
                    }
                }
                catch (RemoteException e)
                {
                    Log.i("error", "we failed");
                }
            }
        });

        ServiceConnection mConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                try {
                    Log.i("onServiceConnected", "Connected");
                    mSdk = IAusweisApp2Sdk.Stub.asInterface(service);

                } catch (ClassCastException e) {
                    // ...
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName className) {
                Log.i("onServiceDisconnected", "disconnected");
                mSdk = null;
            }


        };

        String pkg = "com.governikus.ausweisapp2";

        boolean useIntegrated = true; // use external or integrated
        if (useIntegrated)
            pkg = getApplicationContext().getPackageName();

        String name = "com.governikus.ausweisapp2.START_SERVICE";
        Intent serviceIntent = new Intent(name);
        serviceIntent.setPackage(pkg);
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);


    }
}
