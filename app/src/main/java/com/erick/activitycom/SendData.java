package com.erick.activitycom;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;

import static android.content.ContentValues.TAG;

public class SendData {
    static BluetoothSocket mSocket;
    OutputStream mOutputStream;
    String mBuffer;
    int intBuffer;

    Context context;

    public SendData(BluetoothSocket connectedSocket) {
        mSocket = connectedSocket;

        OutputStream tmpOut = null;

        try {
            tmpOut = connectedSocket.getOutputStream();
        } catch (IOException e) {
            Log.d(TAG, "SendDataClass: connectedSocket.getOutputStream() failed");
        }

        mOutputStream = tmpOut;
    }

    public void write(String bytes) {
        mBuffer = bytes;
        try {
            mOutputStream.write(mBuffer.getBytes());
        } catch (Exception e) {
            Log.d(TAG, "mOutputStream.write() failed - Failed to write");
        }
    }

    public static boolean checkConnection() {
        try {
            if (mSocket.isConnected()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.d(TAG, "checkConnection: failed");
        }

        return false;
    }

    public void writeInt(int bytes) {
        intBuffer = bytes;

        try {
            mOutputStream.write(bytes);
        } catch (IOException e) {
            Log.d(TAG, "mOutputStream.writeInt() failed - Failed to write");
        }
    }

    public static void close() {
        try {
            mSocket.close();
        } catch (IOException e) {
            Log.d(TAG, "Failed to close");
        }
    }
}
