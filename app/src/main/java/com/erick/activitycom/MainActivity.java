package com.erick.activitycom;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    ImageButton getPairedButton;
    TextView connectStatusId;
    Button onButton;
    Button offButton;
    SeekBar brightnessBar;
    TextView brightnessText;

    BluetoothAdapter myAdapter;

    Set <BluetoothDevice> pairedDevices;

    // Arrays lists
    ArrayList <String> listOfPairedDevices = new ArrayList<>();
    ArrayList <String> listOfPairedAddress = new ArrayList<>();

    SendData sendData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPairedButton = (ImageButton) findViewById(R.id.imageButtonId);
        connectStatusId = (TextView) findViewById(R.id.statusTextId);
        onButton = (Button) findViewById(R.id.onButtonId);
        offButton = (Button) findViewById(R.id.offButtonId);
        brightnessBar = (SeekBar) findViewById(R.id.seekBarId);
        brightnessText = (TextView) findViewById(R.id.brightnessNum);

        myAdapter = BluetoothAdapter.getDefaultAdapter();

        Toast.makeText(MainActivity.this, "Tap bluetooth image to get paired devices or scan for new devices!"
            , Toast.LENGTH_SHORT).show();

        brightnessBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                        try {
                            if (myAdapter.isEnabled()) {
                                brightnessText.setText(String.valueOf(progress));

                                sendData = new SendData(ConnectThread.mSocket);

                                // Sets brightness of LED (From 0 - 253)
                                sendData.writeInt(progress);
                            }
                        } catch (Exception e) {
                            Toast t = Toast.makeText(MainActivity.this, "Must connect to a device first",
                                    Toast.LENGTH_SHORT);
                            t.setDuration(Toast.LENGTH_SHORT);
                            t.show();
                            t.cancel();
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );

        // =================================== Beginning of on and off button clicks ==============================================

        onButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (myAdapter.isEnabled()) {
                                sendData = new SendData(ConnectThread.mSocket);

                                // Turn LED on (Sends the number 254 to Hc-05)
                                // Arduino is programmed to turn LED on if it
                                // receives the number 254
                                sendData.writeInt(254);
                            }
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Connect to a device first", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        offButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (myAdapter.isEnabled()) {
                                sendData = new SendData(ConnectThread.mSocket);

                                // Turn LED off (Sends the number 255 to Hc-05)
                                // Arduino is programmed to turn LED off if it
                                // receives the number 255
                                sendData.writeInt(255);
                            }
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Connect to a device first", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // ======================================== End of on and off button clicks ==============================================
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layout, menu);

        MenuItem switchItem = menu.findItem(R.id.switchFromMenuId);
        switchItem.setActionView(R.layout.switch_layout);

        View view = switchItem.getActionView().findViewById(R.id.switchFromLayoutId);
        Switch mySwitch = (Switch) view.findViewById(R.id.switchFromLayoutId);

        mySwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (isChecked) {
                            if (myAdapter == null) {
                                Toast.makeText(MainActivity.this, "Bluetooth not available on this device",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Intent turnBtOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivity(turnBtOnIntent);
                            }
                        }

                        if (!isChecked) {
                            try {
                                myAdapter.disable();
                            } catch (Exception e) {
                                Log.d("Ignore", "Seriously, ignore");
                            }
                        }
                    }
                }
        );

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.closeOptionId:
                try {
                    SendData.close();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Connect to a device to close a connection",
                            Toast.LENGTH_SHORT).show();
                }

                return true;

            case R.id.checkConnectionOptionId:
                try {
                    if (SendData.checkConnection()) {
                        Toast.makeText(MainActivity.this, "You are connected!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "You are not connected", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "You are not connected", Toast.LENGTH_SHORT).show();
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getPairedList(View view) {
        if (myAdapter.isEnabled()) {
            pairedDevices = myAdapter.getBondedDevices();

            for (BluetoothDevice device: pairedDevices) {

                String pairedDeviceName = device.getName();
                String pairedDeviceAddress = device.getAddress();

                if (!listOfPairedDevices.contains(pairedDeviceName)) {
                    listOfPairedDevices.add(pairedDeviceName);
                }

                if (!listOfPairedAddress.contains(pairedDeviceAddress)) {
                    listOfPairedAddress.add(pairedDeviceAddress);
                }

                /* pairedAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1,
                        listOfPairedDevices); */
            }

            Intent openListOfPairedIntent = new Intent(MainActivity.this, ListActivity.class);
            openListOfPairedIntent.putStringArrayListExtra("listOfPairedDevices", listOfPairedDevices);
            openListOfPairedIntent.putStringArrayListExtra("listOfPairedAddress", listOfPairedAddress);
            startActivity(openListOfPairedIntent);
        } else {
            Toast.makeText(MainActivity.this, "Bluetooth must be enabled first", Toast.LENGTH_SHORT).show();
        }
    }
}