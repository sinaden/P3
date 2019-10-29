package com.example.p3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.content.BroadcastReceiver;
import android.content.Context;


import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

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
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class Main extends AppCompatActivity {

    private static final String TAG = "WiFi";
    private static final String SERVICE_TYPE = "_wi-chat._tcp.";
    public String NICKNAME = Registration.NICKNAME;

    private ImageView mWifiSignal;
    private TextView mWifiSSID;
    private int level;
    private Thread wifiSignalCheck;
    private androidx.appcompat.widget.Toolbar toolbar;

    WifiManager wifiManager;
    WifiInfo wifiInfo;
    String macAddress;

    int localPort;
    NsdManager.RegistrationListener registrationListener;
    String serviceName;
    NsdManager nsdManager;
    NsdManager.DiscoveryListener discoveryListener;
    NsdManager.ResolveListener resolveListener;

    List<Device> devices = new ArrayList<>();


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
        macAddress = getMacAddr();


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

        initializeRegistrationListener();
        registerService(8888);
        initializeResolveListener();
        initializeDiscoveryListener();
        nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);

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

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
        }
        return "";
    }

    public void globalChatButton(View view) {
        Toast.makeText(Main.this, "Global Chat", Toast.LENGTH_LONG).show();
        serverClass = new ServerClass();
        serverClass.start();
    }

    public void chatRoomsButton(View view) {
        Toast.makeText(Main.this, "Chat Rooms", Toast.LENGTH_LONG).show();
       // Log.i(TAG, NICKNAME);
        Log.e(TAG, "My nickname is " + NICKNAME );

    }

    public void chatRouletteButton(View view) {
        Toast.makeText(Main.this, "Chat Roulette", Toast.LENGTH_LONG).show();


        new Thread() {
            @Override
            public void run() {
                sendReceive.write(NICKNAME.getBytes());
            }
        }.start();
        }


    }

    public void friendsButton(View view) {
        Toast.makeText(Main.this, "Friends", Toast.LENGTH_LONG).show();
        clientClass = new ClientClass(devices.get(0).inetAddress);
        clientClass.start();
    }

    public void settingsButton(View view) {
        Toast.makeText(Main.this, "Settings", Toast.LENGTH_LONG).show();
    }


    public class ServerClass extends Thread {
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(8888);
                localPort = serverSocket.getLocalPort();
                socket = serverSocket.accept();
                sendReceive = new SendReceive(socket);
                sendReceive.start();

            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
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
                Log.i(TAG, e.getMessage());
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
                Log.i(TAG, e.getMessage());
            }
        }

    }

    //Registers name of the service in the local network
    public void registerService(int port) {
        NsdServiceInfo serviceInfo = new NsdServiceInfo();

        serviceInfo.setServiceName("Wi-Chat " + macAddress);
        serviceInfo.setServiceType(SERVICE_TYPE);
        serviceInfo.setPort(port);

        nsdManager = (NsdManager) getApplicationContext().getSystemService(NSD_SERVICE);

        nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener);

    }

    //Alerts on success or failure of registering the service
    public void initializeRegistrationListener() {
        registrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                serviceName = serviceInfo.getServiceName();
                Log.i(TAG, "Service registered: " + serviceName);
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.i(TAG, "Registration failed, error code: " + errorCode);
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
                Log.i(TAG, "Service successfully unregistered");
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.i(TAG, "Service unregistering failed, error code: " + errorCode);
            }
        };

    }

    public void initializeDiscoveryListener() {
        discoveryListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onDiscoveryStarted(String serviceType) {
                Log.i(TAG, "Service discovery started " + serviceType);
            }

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                Log.i(TAG, "Service discovery success " + serviceInfo.toString());
                if(!serviceInfo.getServiceType().equals(SERVICE_TYPE)){
                    Log.i(TAG, "Unknown Service Type: " + serviceInfo.getServiceType());
                } else if(serviceInfo.getServiceName().equals(serviceName)){
                    Log.i(TAG, "Same machine: " + serviceName);
                }else if(serviceInfo.getServiceName().contains("Wi-Chat")){
                    nsdManager.resolveService(serviceInfo, resolveListener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                Log.i(TAG, "Service lost: " + serviceInfo.toString());
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code: " + errorCode);
                nsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code: " + errorCode);
                nsdManager.stopServiceDiscovery(this);
            }
        };

    }

    public void initializeResolveListener(){
        resolveListener = new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "Resolve failed: " +errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "Resolve Succeeded. " + serviceInfo.toString());

                if (serviceInfo.getServiceName().equals(serviceName)){
                    Log.i(TAG, "Same IP.");
                    return;
                }

                int port = serviceInfo.getPort();
                InetAddress host = serviceInfo.getHost();
                String hostMacAddress = serviceInfo.getServiceName().replaceFirst("Wi-Chat", "").trim();

                devices.add(new Device(hostMacAddress, host));
                Log.i(TAG, devices.get(devices.size()-1).macAddress + devices.get(devices.size()-1).inetAddress);

            }
        };
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}

class Device{
    String macAddress;
    InetAddress inetAddress;


    public Device (String macAddress, InetAddress inetAddress){
        this.macAddress = macAddress;
        this.inetAddress = inetAddress;
    }
}
