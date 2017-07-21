package com.erick.activitycom;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

class ConnectThread extends Thread {
    private String TAG = "ConnectThread";

    BluetoothDevice remoteDevice;
    public static BluetoothSocket mSocket;

    Context context;

    String uuidString = "00001101-0000-1000-8000-00805f9b34fb";

    ConnectThread(Context c, BluetoothDevice device) {
        remoteDevice = device;
        context = c;

        BluetoothSocket tmp = null;

        try {
            tmp = remoteDevice.createRfcommSocketToServiceRecord(UUID.fromString(uuidString));
            Toast.makeText(c, "Rfcomm was successful!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "ConnectThread: ");
        } catch (IOException e) {
            Log.d(TAG, "ConnectThread: Failed to create Rfcomm");
        }

        mSocket = tmp;
    }

    public void run() {
        Looper.prepare();

        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

        try {
            mSocket.connect();
            Log.d(TAG, "ConnectThread: Connection success!");
            Toast.makeText(context, "Connection successful!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.d(TAG, "ConnectThread: Failed to connect - mSocket.connect() failed");

            try {
                mSocket.close();
            } catch (IOException e1) {
                Log.d(TAG, "ConnectThread: Failed to close socket resulting from connect() fail");
                Toast.makeText(context, "ConnectThread: Failed to close socket resulting from connect() fail",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void checkConnection(Context c) {
        context = c;
        if (mSocket.isConnected()) {
            Toast.makeText(c, "You are indeed connected!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(c, "You are not connected", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancel() {
        // Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        // Log.d(TAG, "ConnectThread: ");

        try {
            mSocket.close();
        } catch (IOException e) {
            Log.d(TAG, "ConnectThread: close() from cancel() failed");
            Toast.makeText(context, "ConnectThread: close() from cancel() failed", Toast.LENGTH_SHORT).show();
        }
    }
}



















