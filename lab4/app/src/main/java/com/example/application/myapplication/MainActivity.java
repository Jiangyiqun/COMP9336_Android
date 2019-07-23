package com.example.application.myapplication;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;

public class MainActivity extends AppCompatActivity {

    // create views
    private Button btn_enable_wifi;
    private Button btn_check_direct;
    private Button btn_discover_peer;
    private TextView textView;
    private ListView listView;
    // create other varibles
    private WifiManager wifiManager;
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private WiFiDirectBroadcastReceiver receiver;
    private IntentFilter intentFilter;
    private ArrayAdapter<String> adapter;
    private Collection<WifiP2pDevice> previousPeers;
    private ArrayList<String> peerNames = new ArrayList<String>();
    private ArrayList<String> deviceAddresses = new ArrayList<String>();
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // initialze button and views
        btn_enable_wifi = findViewById(R.id.btn_enable_wifi);
        btn_check_direct= findViewById(R.id.btn_check_direct);
        btn_discover_peer= findViewById(R.id.btn_discover_peer);
        textView = findViewById(R.id.textView);
        listView = findViewById(R.id.listView);
        // initialize other varibles
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiP2pManager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(this, getMainLooper(), null);
        // initialze receiver and intentFilter
        receiver = new WiFiDirectBroadcastReceiver(wifiP2pManager, channel, this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(wifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(wifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(wifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(wifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        // initialize adapter
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, peerNames);
        listView.setAdapter(adapter);
        // initialize on click listeners
        btn_enable_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableWifi();
            }
        });
        btn_check_direct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWifiDirectState();
            }
        });
        btn_discover_peer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWifiDirectPeer();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //obtain a peer from the WifiP2pDeviceList
                final String deviceAddress = (String) deviceAddresses.toArray()[position];
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = deviceAddress;
                wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {

                        textView.setText("connecting to: " + deviceAddress + " succeed");
                    }

                    @Override
                    public void onFailure(int reason) {
                        textView.setText("connecting to: " + deviceAddress + " failed");
                    }
                });

            }
        });
    }

    private void enableWifi () {
        if (wifiManager.isWifiEnabled()) {
            textView.setText("Wifi is already enabled");
        } else {
            wifiManager.setWifiEnabled(true);
            textView.setText("Wifi is enabled now");
        }
    }

    private void getWifiDirectState() {
        if (wifiP2pManager.WIFI_P2P_STATE_ENABLED == 2) {
            textView.setText("Wifi-Direct is available");
        } else {
            textView.setText("Wifi-Direct is not available");
        }
    }

    private void getWifiDirectPeer() {
        peerNames.clear();
        wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                textView.setText("Peer Discovery Succeed");
            }

            @Override
            public void onFailure(int reasonCode) {
                textView.setText("Peer Discovery Failed");
            }
        });

    }

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener () {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peers) {
            Collection<WifiP2pDevice> currentPeers = peers.getDeviceList();
            int numberOfDevices = currentPeers.size();
            if (numberOfDevices == 0) {
//                textView.setText("No Device Found");
            } else if (currentPeers.equals(previousPeers)) {
            } else {
                peerNames.clear();
                deviceAddresses.clear();
//                textView.setText(numberOfDevices + " Devices Found");
                for (WifiP2pDevice peer : currentPeers) {
                    peerNames.add(peer.deviceName + ": " + peer.deviceAddress);
                    deviceAddresses.add(peer.deviceAddress);
                }
                previousPeers = currentPeers;
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        registerReceiver(receiver, intentFilter);
    }
}
