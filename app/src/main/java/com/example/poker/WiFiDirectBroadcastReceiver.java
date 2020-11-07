package com.example.poker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import static android.os.Looper.getMainLooper;

/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private Rooms activity;
    private WifiP2pManager.PeerListListener myPeerListListener;
    WifiP2pDevice device;
    WifiP2pConfig config = new WifiP2pConfig();

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       Rooms activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    public void showToast(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();


        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

            if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                Log.d("WiFiDirectBroadcastReceiver - onReceive", "WiFi Enabled!");
                showToast("WiFi Enabled!");
            }
            else {
                Log.d("WiFiDirectBroadcastReceiver - onReceive", "WiFi Disabled!");
                showToast("WiFi Disabled!");
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            ConnectivityManager cm_a = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork_a = cm_a.getActiveNetworkInfo();
            if (activeNetwork_a != null) {
                Log.d("WiFiDirectBroadcastReceiver - onReceive", "No active network, requestPeers");
                if (manager != null) {
                    try {
                        manager.requestPeers(channel, activity.peerListListener);
                    } catch (SecurityException e) {
                        Log.d("WiFiDirectBroadcastReceiver - onReceive", "NO PERMISSION");
                    }
                }
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if(manager == null){
                Log.d("WiFiDirectBroadcastReceiver - onReceive", "manager = null");
                return;
            }
            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            Log.d("NETWORK INFO", networkInfo.toString());
            if (networkInfo.isConnected()) {
                manager.requestConnectionInfo(channel, connectionInfoListener);
            }
//            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//            if (activeNetwork != null) {
//                Log.d("WiFiDirectBroadcastReceiver - onReceive", "networkInfo - Connected");
//
//                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
//                    manager.requestConnectionInfo(channel, connectionInfoListener);
//                    Log.d("WiFiDirectBroadcastReceiver - onReceive", "networkInfo - Connected - WiFi");
//
//                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
//                    Log.d("WiFiDirectBroadcastReceiver - onReceive", "networkInfo - Connected - MobileData");
//                }
//            } else {
//                Log.d("WiFiDirectBroadcastReceiver - onReceive", "networkInfo - Not Connected");
//            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            Log.d("WiFiDirectBroadcastReceiver - onReceive", "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
        }

    }

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(final WifiP2pInfo info) {
            ConnectivityManager cm = (ConnectivityManager) ((Context) activity).getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            final InetAddress groupOwnerAddress = info.groupOwnerAddress;
            Log.d("Rooms - onConnectionInfoAvailable", info.toString());
            if(info.groupFormed && info.isGroupOwner){
                Log.d("onConnectionInfoAvailable","Client");
//                msgText.setText("Host");
            }
            else if (info.groupFormed){
//                msgText.setText("Client");
                Log.d("onConnectionInfoAvailable","Client");
            }

        }
    };

}