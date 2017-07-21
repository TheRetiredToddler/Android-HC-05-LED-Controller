package com.erick.activitycom;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    ListView pairedList;
    ListView foundList;
    Button searchButton;
    TextView pairedText;

    ArrayAdapter <String> pairedAdapter;
    ArrayAdapter <String> foundAdapter;

    ArrayList <String> listOfPairedAddress;
    ArrayList <String> listOfFoundDevices = new ArrayList<>();
    ArrayList <String> listOfFoundAddress = new ArrayList<>();

    BluetoothAdapter myAdapter;

    BluetoothDevice foundDeviceSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        pairedList = (ListView) findViewById(R.id.pairedListId);
        foundList = (ListView) findViewById(R.id.foundListId);
        searchButton = (Button) findViewById(R.id.searchButtonId);
        pairedText = (TextView) findViewById(R.id.pairedTextId);

        myAdapter = BluetoothAdapter.getDefaultAdapter();

        searchButton.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        myAdapter.cancelDiscovery();
                        Toast.makeText(ListActivity.this, "Discovery canceled!", Toast.LENGTH_SHORT).show();

                        return true;
                    }
                }
        );

        Intent intent = getIntent();
        ArrayList <String> listOfPairedDevices = intent.getStringArrayListExtra("listOfPairedDevices");
        listOfPairedAddress = intent.getStringArrayListExtra("listOfPairedAddress");

        pairedAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listOfPairedDevices);
        pairedList.setAdapter(pairedAdapter);

        pairedAdapter.notifyDataSetChanged();

        pairedList.setOnItemClickListener(pairedListener);
        foundList.setOnItemClickListener(foundListener);

        registerReceiver(deviceFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(bondedDeviceReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
    }

    public void startDiscovery(View view) {
        myAdapter.startDiscovery();
    }

    BroadcastReceiver deviceFoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice deviceFound = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                String deviceFoundName = deviceFound.getName();
                String deviceFoundAddress = deviceFound.getAddress();

                if (!listOfFoundDevices.contains(deviceFoundName)) {
                    listOfFoundDevices.add(deviceFoundName);
                }

                if (!listOfFoundAddress.contains(deviceFoundAddress)) {
                    listOfFoundAddress.add(deviceFoundAddress);
                }

                foundAdapter = new ArrayAdapter<>(ListActivity.this, android.R.layout.simple_list_item_1,
                        listOfFoundDevices);
                foundList.setAdapter(foundAdapter);

                foundAdapter.notifyDataSetChanged();
            }
        }
    };

    BroadcastReceiver bondedDeviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String bondingDeviceName = foundDeviceSelected.getName();

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
                Log.d("Bond state changed", "Bond state changed to " + state);

                if (state == BluetoothDevice.BOND_BONDING) {
                    Log.d("Bonding", "BOND_BONDED - Bonding with: " + String.valueOf(bondingDeviceName));
                    Toast.makeText(ListActivity.this, "Bonding with: " + String.valueOf(bondingDeviceName),
                            Toast.LENGTH_SHORT).show();
                }

                if (state == BluetoothDevice.BOND_BONDED) {
                    Log.d("BOND_BONDED", "Paired with: " + String.valueOf(bondingDeviceName));
                    Toast.makeText(ListActivity.this, "Bonded with: " + String.valueOf(bondingDeviceName),
                            Toast.LENGTH_SHORT).show();
                }

                if (state == BluetoothDevice.BOND_NONE) {
                    Log.d("BOND_NONE", "Unpaired with: " + String.valueOf(bondingDeviceName));
                    Toast.makeText(ListActivity.this, "Unpaired with: " + String.valueOf(bondingDeviceName),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    AdapterView.OnItemClickListener pairedListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            BluetoothDevice deviceLongSelected = myAdapter.getRemoteDevice(listOfPairedAddress.get(position));

            ConnectThread deviceOne = new ConnectThread(ListActivity.this, deviceLongSelected);
            deviceOne.start();
        }
    };

    AdapterView.OnItemClickListener foundListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            foundDeviceSelected = myAdapter.getRemoteDevice(listOfFoundAddress.get(position));

            ConnectThread deviceTwo = new ConnectThread(ListActivity.this, foundDeviceSelected);
            deviceTwo.start();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(deviceFoundReceiver);
        unregisterReceiver(bondedDeviceReceiver);
    }
}