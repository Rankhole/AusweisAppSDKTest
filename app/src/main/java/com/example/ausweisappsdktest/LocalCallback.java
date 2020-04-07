package com.example.ausweisappsdktest;

import android.os.RemoteException;
import android.util.Log;

import com.governikus.ausweisapp2.IAusweisApp2SdkCallback;

class LocalCallback extends IAusweisApp2SdkCallback.Stub {
    public String mSessionID = null;

    @Override
    public void sessionIdGenerated(
            String pSessionId, boolean pIsSecureSessionId) throws RemoteException {
        mSessionID = pSessionId;
    }

    @Override
    public void receive(String pJson) throws RemoteException {
        Log.i("myjson", pJson);
    }

    @Override
    public void sdkDisconnected() throws RemoteException {
        mSessionID = null;
    }
}
