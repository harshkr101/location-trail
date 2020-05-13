package com.example.locationmapper;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


// this activity discover nearby bluetooth devices

public class DeviceFinderActivity extends AppCompatActivity {


    private ListView listView;  // list view to show devices
    private ArrayList<String> mDeviceList = new ArrayList<String>(); // device list
    private BluetoothAdapter mBluetoothAdapter; // bluetooth adapter


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_finder);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // check if device supports bluetooth
        if(mBluetoothAdapter==null){
            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }


        // check for bluetooth permission
        if (!mBluetoothAdapter.isEnabled()) {
            bluetoothAlert();;
        }


        listView =  findViewById(R.id.listView);
        mBluetoothAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                assert device != null;
                mDeviceList.add(device.getName() + "\n" + device.getAddress());
                Log.i("BT", device.getName() + "\n" + device.getAddress());
                listView.setAdapter(new ArrayAdapter<String>(context,
                        android.R.layout.simple_list_item_1, mDeviceList));
            }
        }

    };

    private void bluetoothAlert(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Bluetooth is disabled in your device please enable it.")
                .setCancelable(false)
                .setPositiveButton("Enable Bluetooth",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

}
