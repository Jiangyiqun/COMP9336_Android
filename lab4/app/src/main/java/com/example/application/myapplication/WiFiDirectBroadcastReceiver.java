package com.example.application.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;

import com.example.application.myapplication.MainActivity;

/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private MainActivity mainActivity;

    public WiFiDirectBroadcastReceiver(WifiP2pManager wifiP2pManager, WifiP2pManager.Channel channel,
                                       MainActivity mainActivity) {
        super();
        this.wifiP2pManager = wifiP2pManager;
        this.channel = channel;
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (wifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
        } else if (wifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            if (wifiP2pManager != null) {
                wifiP2pManager.requestPeers(channel, mainActivity.peerListListener);
            }
        } else if (wifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
        } else if (wifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }

}
