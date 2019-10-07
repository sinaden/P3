package com.example.p3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.content.BroadcastReceiver;
import android.content.Context;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Main extends AppCompatActivity {

    public String NICKNAME = Registration.NICKNAME;

    private ImageView mWifiSignal;
    private TextView mWifiSSID;
    private int level;
    private Thread wifiSignalCheck;
    private androidx.appcompat.widget.Toolbar toolbar;

    WifiManager wifiManager;
    WifiInfo wifiInfo;
    private final IntentFilter intentFilter = new IntentFilter();
    WifiP2pManager.Channel channel;
    WifiP2pManager wifiP2pManager;
    BroadcastReceiver broadcastReceiver;

    WifiP2pInfo groupInfo;

    List<WifiP2pDevice> peers = new ArrayList<>();
    String[] deviceNameArray;
    WifiP2pDevice[] deviceArray;

    ServerClass serverClass;
    ClientClass clientClass;
    SendReceive sendReceive;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWifiSignal = findViewById(R.id.wifi_signal);
        mWifiSSID = findViewById(R.id.wifi_ssid);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.toolbar_title));

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                0);

        wifiSignalCheck = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        setWifiSignal();
                        sleep(10000);
                    } catch (Exception e) {

                    }


                }
            }
        };
        wifiSignalCheck.start();


        ProfileDrawerItem profile = new ProfileDrawerItem().withName(NICKNAME).withIcon(R.drawable.default_avatar);
        AccountHeaderBuilder accountHeaderBuilder = new AccountHeaderBuilder()
                .withActivity(this)
                .withSelectionListEnabled(false)
                .addProfiles(profile)
                .withOnAccountHeaderProfileImageListener(new AccountHeader.OnAccountHeaderProfileImageListener() {
                    @Override
                    public boolean onProfileImageClick(@NotNull View view, @NotNull IProfile<?> iProfile, boolean b) {
                        Toast.makeText(Main.this, "Settings", Toast.LENGTH_LONG).show();
                        return false;
                    }

                    @Override
                    public boolean onProfileImageLongClick(@NotNull View view, @NotNull IProfile<?> iProfile, boolean b) {
                        Toast.makeText(Main.this, "Settings", Toast.LENGTH_LONG).show();
                        return false;
                    }
                });
        AccountHeader accountHeader = accountHeaderBuilder.build();

        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(0).withName(R.string.drawer_global_chat).withIcon(R.drawable.global_chat);
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.drawer_chat_rooms).withIcon(R.drawable.chat_rooms);
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.drawer_chat_roulette).withIcon(R.drawable.chat_roulette);
        SecondaryDrawerItem item4 = new SecondaryDrawerItem().withIdentifier(3).withName(R.string.drawer_friends).withIcon(R.drawable.friends);
        SecondaryDrawerItem item5 = new SecondaryDrawerItem().withIdentifier(4).withName(R.string.drawer_settings).withIcon(R.drawable.settings);

        DrawerBuilder drawerBuilder;
        drawerBuilder = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(accountHeader)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(false)
                .withTranslucentNavigationBar(true)
                .withActionBarDrawerToggle(true)
                .withMultiSelect(false)
                .withSelectedItem(-1)
                .addDrawerItems(item1, item2, item3, new DividerDrawerItem(), item4, item5)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(@Nullable View view, int i, @NotNull IDrawerItem<?> iDrawerItem) {
                        switch (i) {
                            case 1:

                                Toast.makeText(Main.this, "Global Chat", Toast.LENGTH_LONG).show();
                                break;
                            case 2:

                                Toast.makeText(Main.this, "Chat Rooms", Toast.LENGTH_LONG).show();
                                break;
                            case 3:

                                Toast.makeText(Main.this, "Chat Roulette", Toast.LENGTH_LONG).show();
                                break;
                            case 5:

                                Toast.makeText(Main.this, "Friends", Toast.LENGTH_LONG).show();
                                break;
                            case 6:

                                Toast.makeText(Main.this, "Settings", Toast.LENGTH_LONG).show();
                                break;
                        }
                        return false;
                    }
                });
        Drawer drawer = drawerBuilder.build();

        wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(this, getMainLooper(), null);
        broadcastReceiver = new WiFiDirectBroadcastReceiver(wifiP2pManager, channel, this);


        // Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff, 0, msg.arg1);
                    Toast.makeText(Main.this, tempMsg, Toast.LENGTH_LONG).show();
                    break;
            }

            return true;
        }
    });

    private void setWifiSignal() {
        Context context = getApplicationContext();
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int numberOfLevels = 5;
        wifiInfo = wifiManager.getConnectionInfo();
        level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);

        String ssid = wifiInfo.getSSID().trim().replaceAll("\"", "");
        if (ssid.equals("<unknown ssid>")) {
            ssid = "";
        }

        switch (level) {
            case 0:
                mWifiSignal.setImageResource(R.drawable.wifi_0);
                ssid = "No network :(";
                break;
            case 1:
                mWifiSignal.setImageResource(R.drawable.wifi_1);
                break;
            case 2:
                mWifiSignal.setImageResource(R.drawable.wifi_2);
                break;
            case 3:
                mWifiSignal.setImageResource(R.drawable.wifi_3);
                break;
            case 4:
                mWifiSignal.setImageResource(R.drawable.wifi_4);
                break;
        }
        mWifiSSID.setText(ssid);
    }

    public void globalChatButton(View view) {
        Toast.makeText(Main.this, "Global Chat", Toast.LENGTH_LONG).show();
        if (deviceArray == null || deviceArray.length == 0) {
            wifiP2pManager.createGroup(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.i("WiFi", "Group created");
                }

                @Override
                public void onFailure(int reason) {
                    Log.i("WiFi", "Group creation failed " + reason);
                }
            });
        } else {

            final WifiP2pDevice device = deviceArray[0];
            final WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = device.deviceAddress;

            wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.i("WiFi", "Group joined " + config.deviceAddress);
                }

                @Override
                public void onFailure(int reason) {
                    Log.i("WiFi", "Failed to join the group");
                }
            });
        }
    }

    public void chatRoomsButton(View view) {
        Toast.makeText(Main.this, "Chat Rooms", Toast.LENGTH_LONG).show();
    }

    public void chatRouletteButton(View view) {
        Toast.makeText(Main.this, "Chat Roulette", Toast.LENGTH_LONG).show();
        new Thread(){
            @Override
            public void run() {
                sendReceive.write(NICKNAME.getBytes());
            }
        }.start();

    }

    public void friendsButton(View view) {
        Toast.makeText(Main.this, "Friends", Toast.LENGTH_LONG).show();
        wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.i("WiFi", "Discovering peers");
            }

            @Override
            public void onFailure(int reason) {
                Log.i("WiFi", "Discovering peers, failed");
            }
        });
    }

    public void settingsButton(View view) {
        Toast.makeText(Main.this, "Settings", Toast.LENGTH_LONG).show();
    }

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peersList) {
            if (!peersList.getDeviceList().equals(peers)) {
                peers.clear();
                peers.addAll(peersList.getDeviceList());

                deviceNameArray = new String[peersList.getDeviceList().size()];
                deviceArray = new WifiP2pDevice[peersList.getDeviceList().size()];
                int index = 0;
                for (WifiP2pDevice device : peersList.getDeviceList()) {
                    deviceNameArray[index] = device.deviceName;
                    deviceArray[index] = device;
                    index++;
                    Log.i("WiFi", device.deviceName + device.deviceAddress);
                }


            }
            if (peers.size() == 0) {
                Log.i("WiFi", "No peers found");

            }

        }
    };

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            groupInfo = info;
            final InetAddress groupOwnerAddress = info.groupOwnerAddress;

            if (info.groupFormed && info.isGroupOwner) {
                Log.i("WiFi", "Host " + info.toString());
                serverClass = new ServerClass();
                serverClass.start();
            } else if (info.groupFormed) {
                Log.i("WiFi", "Client " + info.toString());
                clientClass = new ClientClass(groupOwnerAddress);
                clientClass.start();
            }
        }
    };


    public class ServerClass extends Thread {
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(8888);
                socket = serverSocket.accept();
                sendReceive = new SendReceive(socket);
                sendReceive.start();

            } catch (IOException e) {

            }
        }
    }

    private class SendReceive extends Thread {
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public SendReceive(Socket socket) {
            this.socket = socket;
            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (socket != null) {
                try {
                    bytes = inputStream.read(buffer);
                    if (bytes > 0) {
                        handler.obtainMessage(1, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                Log.i("WiFi", e.getMessage());
            }
        }
    }

    public class ClientClass extends Thread {
        Socket socket;
        String hostAddress;

        public ClientClass(InetAddress hostAddress) {
            this.hostAddress = hostAddress.getHostAddress();
            socket = new Socket();
        }

        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(hostAddress, 8888), 500);
                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (Exception e) {
                Log.i("WiFi", e.getMessage());
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.i("WiFi", "Group removed");
            }

            @Override
            public void onFailure(int reason) {
                Log.i("WiFi", "Failed to remove group " + reason);
            }
        });


    }
}
