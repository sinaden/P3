package com.example.p3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private Main main;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, Main main){
        this.manager = manager;
        this.channel = channel;
        this.main = main;
    }

    @Override
    public void onReceive(Context context, Intent intent){
        String action = intent.getAction();

        if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                Log.i("WiFi", "WIFI ON");
            }
            else{
                Log.i("WiFi", "WIFI OFF");
            }
        }
        else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
            if(manager != null){
                manager.requestPeers(channel, main.peerListListener);
            }
        }
        else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){

            if(manager == null){
                return;
            }

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()){
                manager.requestConnectionInfo(channel, main.connectionInfoListener);
                manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
                    @Override
                    public void onGroupInfoAvailable(WifiP2pGroup group) {
                        String ssid = group.getNetworkName();
                        String groupPassword = group.getPassphrase();
                    }
                });
            }
            else{
                Log.i("WiFi", "Device disconnected");
            }
        }
        else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){
            if(manager == null){
                return;
            }

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo!=null && networkInfo.isConnected()) {
                manager.requestConnectionInfo(channel, main.connectionInfoListener);
            }
        }
    }
}
