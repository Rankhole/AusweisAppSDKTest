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

import com.governikus.ausweisapp2.IAusweisApp2Sdk;
import com.governikus.ausweisapp2.IAusweisApp2SdkCallback;

public class MainActivity extends AppCompatActivity {
    IAusweisApp2Sdk mSdk;
    LocalCallback mCallback = new LocalCallback();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ServiceConnection mConnection = new ServiceConnection() {


            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                try {
                    mSdk = IAusweisApp2Sdk.Stub.asInterface(service);
                } catch (ClassCastException e) {
                    // ...
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName className) {
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

        try
        {
            if (!mSdk.connectSdk(mCallback))
            {
                Log.i("error", "connection to sdk failed");
            }
        }
        catch (RemoteException e)
        {
            Log.i("error", "we failed");
        }


        String cmd = "{\"cmd\": \"GET_INFO\"}";
        try
        {
            if (!mSdk.send(mCallback.mSessionID, cmd))
            {
                Log.i("error", "connection to sdk failed");
            }
        }
        catch (RemoteException e)
        {
            Log.i("error", "we failed");
        }
    }
}
