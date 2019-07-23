package jack.jiang.hellowifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.content.IntentFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;


import android.util.Log;


public class MainActivity extends AppCompatActivity implements ExampleDialog.ExampleDialogListener {

    // declaration
    private WifiManager mWifiManager;
    private Button scanBtn;
    private ListView wifiList;
    private ArrayList<String> arrayList = new ArrayList<String>();
//    private ArrayList<String> ssidList = new ArrayList<String>();
    private ArrayAdapter adapter;
    private List<ScanResult> results;
//    private String ssid;
    private ArrayList<ScanResult> wifi_info_list = new ArrayList<ScanResult>();
    private ScanResult wifi_info;
    private static final String TAG = "MyActivity";

    // main function
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // define the items
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        scanBtn = findViewById(R.id.scanBtn);
        wifiList = findViewById(R.id.wifiList);

        // define button function
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanWifi();
            }
        });

        // check if wifi is enabled
        if (mWifiManager.isWifiEnabled()) {
            Toast.makeText(this, "WiFi is enabled, good start.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "WiFi is disabled, enabling...",
                    Toast.LENGTH_LONG).show();
            mWifiManager.setWifiEnabled(true);
        }

        // Show wifi list
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        wifiList.setAdapter(adapter);
        wifiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
//                ssid = ssidList.get(position);
                wifi_info = wifi_info_list.get(position);
                openDialog();
            }

        });

    }


    // scan wifi and show on the list
    private void scanWifi() {
        arrayList.clear();
//        ssidList.clear();
        wifi_info_list.clear();
        registerReceiver(mReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mWifiManager.startScan();
        Toast.makeText(this, "Scanning WiFi ...", Toast.LENGTH_SHORT).show();

    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = mWifiManager.getScanResults();
            unregisterReceiver(this);

            // sort the wifi by signal strength
            Comparator<ScanResult> comparator = new Comparator<ScanResult>() {
                @Override
                public int compare(ScanResult lhs, ScanResult rhs) {
                    return (lhs.level >rhs.level ? -1 : (lhs.level==rhs.level ? 0 : 1));
                }
            };
            Collections.sort(results, comparator);

            // show wifi list
            Hashtable<String, Integer> strongest_wifi = new Hashtable<String, Integer>();
            Integer count = 0;
            for (ScanResult result : results) {
                // only show the top 4
//                count.putIfAbsent(result.SSID, 0);
//                count.put(result.SSID, count.get(result.SSID) + 1);
//                if (count.get(result.SSID) > 4) {
//                    continue;
//                }
                // skip the repetitive SSID
                strongest_wifi.putIfAbsent(result.SSID, 0);
                if (strongest_wifi.contains(result.SSID)) {
                    continue;
                } else {
                    count++;
                }

                if (count > 4) {
                    break;
                }

                // show Open or protected
                if (result.capabilities.length() == 5) {
                    arrayList.add(result.level + " " + result.SSID + result.BSSID + " " + "Open");
//                    ssidList.add(result.SSID);
                    wifi_info_list.add(result);
                } else {
                    arrayList.add(result.level + " " + result.SSID + result.BSSID + " " + "Protected");
//                    ssidList.add(result.SSID);
                    wifi_info_list.add(result);
                }
                adapter.notifyDataSetChanged();
            }
        }
    };


    // open dialog to enter username and password
    public void openDialog() {
        ExampleDialog exampleDialog = new ExampleDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    // connect to wifi
    @Override
    public void connectToWifi(String username, String password) {
        if (wifi_info.capabilities.length() == 5) {
            // open network
            Log.d(TAG, "connect to open network");
            WifiConfiguration config = new WifiConfiguration();
            config.SSID = "\"" + wifi_info.SSID + "\"";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.status = WifiConfiguration.Status.ENABLED;
            int netId = mWifiManager.addNetwork(config);
            mWifiManager.enableNetwork(netId, true);

        } else {
            Log.d(TAG, "ssid:" + wifi_info.SSID + " username:" + username + " auth" + wifi_info.capabilities);
            // password protected network
            WifiConfiguration config = new WifiConfiguration();
            config.SSID = "\"" + wifi_info.SSID + "\"";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
            config.status = WifiConfiguration.Status.ENABLED;

            // enterprise config
            WifiEnterpriseConfig enterpriseConfig = new WifiEnterpriseConfig();
            enterpriseConfig.setIdentity(username);
            enterpriseConfig.setPassword(password);
            enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.PEAP);

            // combine the config
            config.enterpriseConfig = enterpriseConfig;
            int netId = mWifiManager.addNetwork(config);
            mWifiManager.enableNetwork(netId, true);
        }
        mWifiManager.reconnect();
        while (!mWifiManager.getConnectionInfo().getSSID().contains(wifi_info.SSID)
                || mWifiManager.getConnectionInfo().getIpAddress() == 0) {
            try
            {
                Thread.sleep(1000);
            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
        }
//        Log.d(TAG, String.valueOf(mWifiManager.getConnectionInfo().getIpAddress()));
        Toast.makeText(this,
                mWifiManager.getConnectionInfo().getSSID() + " IP:" +
                        Formatter.formatIpAddress(mWifiManager.getConnectionInfo().getIpAddress()),
                Toast.LENGTH_LONG).show();




    }



}
