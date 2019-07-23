package com.example.application.lab3;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    // initialize the textViews
    private TextView availabilityContent;
    private TextView frequenceContent;
    private TextView bitRateContent;
    private TextView protocolContent;
    private TextView modulationContent;

    // initialize other stuff
    private WifiManager wifiManager;
    private WifiInfo wifiInfo;
    private float wifiFrequence;
    private float wifiSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize the textViews
        availabilityContent = findViewById(R.id.availabilityContent);
        frequenceContent = findViewById(R.id.frequenceContent);
        bitRateContent = findViewById(R.id.bitRateContent);
        protocolContent = findViewById(R.id.protocolContent);
        modulationContent = findViewById(R.id.modulationContent);

        //refresh
        refreshWifiInfo();
    }


    private void getAvailability() {
        if (wifiManager.is5GHzBandSupported()) {
            availabilityContent.setText(R.string.IsAvailable);
        } else {
            availabilityContent.setText(R.string.NotAvailable);
        }
    }

    private void getFrequence() {
        String TAG = "getFrequence";
        String frequenceContentText = wifiFrequence/1000+ " GHz";
//        Log.d(TAG, String.valueOf(wifiFrequence));
        frequenceContent.setText(frequenceContentText);
    }

    private void getBitRate() {
        String bitRateContentText =  wifiSpeed + " Mpps";
        bitRateContent.setText(bitRateContentText);
    }


    /*
     * calculate wifi protocol and modulation
     * scope: 802.11a/b/g/n/ac
     *     ref: https://store.google.com/au/product/pixel_3_specs
     *
     *     protocol    Frequency   Speed   Modulation
     *      802.11a     5           54      OFDM
     *      802.11b     2.4         11      DSSS
     *      802.11g     2.4         54      OFDM
     *      802.11n     2.4/5       600     MIMO-OFDM
     *      802.11ac    5           3466    MIMO-OFDM
     *
     *     ref: https://en.wikipedia.org/wiki/IEEE_802.11
     */
    private void getProtocol() {
        String protocol = "UNKNOWN";
        String modulation = "UNKNOWN";
        // since the frequency is more reliable, I will check it first
        float frequenceThreshold = (float) ((2.4 + 5) / 2);
        if (wifiFrequence < frequenceThreshold) {
            // 2.4GHz: 802.11n, 802.11g, 802.11b
            if (wifiSpeed > 600) {
                ;
            } else if (wifiSpeed > 54) {
                protocol = "802.11n";
                modulation = "MIMO-OFDM";
            } else if (wifiSpeed > 11) {
                protocol = "802.11g";
                modulation = "OFDM";
            } else {
                protocol = "802.11b";
                modulation = "DSSS";
            }
        } else {
            //5GHz: 802.11ac, 802.11n, 802.11a
            if (wifiSpeed > 3466) {
                ;
            } else if (wifiSpeed > 600) {
                protocol = "802.11ac";
                modulation = "MIMO-OFDM";
            } else if (wifiSpeed > 54) {
                protocol = "802.11n";
                modulation = "MIMO-OFDM";
            } else {
                protocol = "802.11a";
                modulation = "OFDM";
            }
        }
        protocolContent.setText(protocol);
        modulationContent.setText(modulation);
    }


    private void refreshWifiInfo() {
        // get basic wifi attribute
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiInfo = (WifiInfo) wifiManager.getConnectionInfo();
        wifiFrequence = (float) wifiInfo.getFrequency();
        wifiSpeed = (float) wifiInfo.getLinkSpeed();

        // check if wifi is enabled
        if (wifiManager.isWifiEnabled()) {
            Toast.makeText(this, R.string.toast_wifi_refreshed, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.toast_enable_wifi, Toast.LENGTH_SHORT).show();
            wifiManager.setWifiEnabled(true);
        }

        // show wifi information
        getAvailability();
        getFrequence();
        getBitRate();
        getProtocol();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        // bind the refresh button
        if (itemThatWasClickedId == R.id.refreshButton) {
            refreshWifiInfo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

