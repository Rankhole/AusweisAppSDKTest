package com.example.ausweisappsdktest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import androidx.appcompat.app.AppCompatActivity;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.governikus.ausweisapp2.IAusweisApp2Sdk;
import com.governikus.ausweisapp2.IAusweisApp2SdkCallback;

public class MainActivity extends AppCompatActivity {
    IAusweisApp2Sdk mSdk;
    LocalCallback mCallback = new LocalCallback();
    ForegroundDispatcher foregroundDispatcher;

    Button myButton;
    Button nfcButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myButton = findViewById(R.id.button);
        nfcButton = findViewById(R.id.button2);
        nfcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foregroundDispatcher.enable();
            }
        });
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!mSdk.connectSdk(mCallback)) {
                        Log.i("error", "connection to sdk failed");
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Log.i("error", "we failed");
                }

                String cmd = "{\"cmd\": \"GET_INFO\"}";
                try
                {
                    if (!mSdk.send(mCallback.mSessionID, cmd))
                    {
                        Toast.makeText(MainActivity.this, "We fucked up", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (RemoteException e)
                {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "We done goofed", Toast.LENGTH_SHORT).show();
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
        foregroundDispatcher = new ForegroundDispatcher(this);

    }

    void handleIntent(Intent intent) {
        final Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            try {
                mSdk.updateNfcTag(mCallback.mSessionID, tag);
            } catch (RemoteException e) {
                // ...
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        foregroundDispatcher.enable();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        foregroundDispatcher.disable();
    }
}
