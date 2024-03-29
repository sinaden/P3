package com.example.p3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;


import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
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
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class Main extends AppCompatActivity {

    private static final String TAG = "WiFi";
    private static int portNo;

    //private static final int registrationPort = 9000;
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

    private ImageView btnRoom;
    private ImageView btnGlobal;
    private ImageView btnRoulette;
    private ImageView btnFriends;
    private ImageView btnSettings;

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
    ConnectionHandler sendReceive2;
    //Thread peerConnection;

    // Sina:
    private StatePagerAdapter mStatePagerAdapter;
    private ViewPager mViewPager;
    static boolean beenToOnCreate = false;

    static boolean cleaner = false;
    static int mainActivityPauses = 0;

    List<ConnectionHandler> clients = new ArrayList<ConnectionHandler>();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWifiSignal = findViewById(R.id.wifi_signal);
        mWifiSSID = findViewById(R.id.wifi_ssid);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.toolbar_title));

        btnRoom = findViewById(R.id.chat_rooms);
        btnGlobal =  findViewById(R.id.global_chat);
        btnRoulette = findViewById(R.id.chat_roulette);
        btnFriends =  findViewById(R.id.friends);
        btnSettings = findViewById(R.id.settings);

        // Hide all the buttons They will be shown in the base fragment instead:
        btnRoom.setVisibility(View.GONE);
        btnGlobal.setVisibility(View.GONE);
        btnRoulette.setVisibility(View.GONE);
        btnFriends.setVisibility(View.GONE);
        btnSettings.setVisibility(View.GONE);


        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                0);


        // https://stackoverflow.com/questions/5161951/android-only-the-original-thread-that-created-a-view-hierarchy-can-touch-its-vi
        // AsyncTask is suggested in android instead of working with threads


       // new WifiSignalCheckBG().execute();

        //https://stackoverflow.com/questions/31957815/android-asynctask-not-executing

        WifiSignalCheckBG w = new WifiSignalCheckBG();
        w.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);



        AliveSignal g = new AliveSignal();
        g.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        //new AliveSignal().execute();

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
                                changeView(1);
                                break;
                            case 2:

                                Toast.makeText(Main.this, "Chat Rooms", Toast.LENGTH_LONG).show();
                                changeView(2);
                                break;
                            case 3:

                                Toast.makeText(Main.this, "Chat Roulette", Toast.LENGTH_LONG).show();
                                changeView(3);
                                break;
                            case 5:

                                Toast.makeText(Main.this, "Friends", Toast.LENGTH_LONG).show();
                                changeView(4);
                                break;
                            case 6:

                                Toast.makeText(Main.this, "Settings", Toast.LENGTH_LONG).show();
                                changeView(5);
                                break;
                        }
                        return false;
                    }
                });
        Drawer drawer = drawerBuilder.build();



        beenToOnCreate = true;

        portNo = findFreePort();
        Log.i(TAG, "onCreate: port no" + portNo);




        initializeRegistrationListener();
        registerService(portNo);
     //   initializeResolveListener();
    //    initializeDiscoveryListener();
    //    nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);
    // move to onRegister so that it would look after registration is done.


        // Sina: Fragments
        mStatePagerAdapter = new StatePagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);

        setupViewPager(mViewPager);

        mViewPager.beginFakeDrag(); // prevents swiping
    }



    private class WifiSignalCheckBG extends AsyncTask<Void,Void,Void> {


        protected Void doInBackground(Void... params) {
            while (true) {


                try {
                    Log.i(TAG, "doInBackground: Scanning");
                    setWifiSignal();
                    Thread.sleep(10000);

                } catch (Exception e) {
                    Log.e(TAG, "run (terminate me): " + e.getMessage());
                }


            }
        }

    }

    // Make a signal to all the connected peers ever 1 and half second. (Says its location in the app)
    private class AliveSignal extends AsyncTask<Void,Void,Void> {


        protected Void doInBackground(Void... params) {
            while (true) {


                try {
                    Log.i(TAG, "I'm alive");
                    //setWifiSignal();
                    imAlive();
                    Thread.sleep(1500);

                } catch (Exception e) {
                    Log.e(TAG, "AliveSignal Exception : " + e.getMessage());
                }


            }
        }

    }


    // Will analyze each alive signal recieved from other peers. dev
    public void updateList(String signal) {
        int i = signal.indexOf("my Mac ad:") + 10;
        int ii = signal.indexOf(",at: ");
        int iii = signal.indexOf("#");


        String newMac = signal.substring(i, ii);

        //Log.e(TAG, "newMac " + newMac );
        String se = signal.substring(ii + 5, iii);
        int section = Integer.parseInt(se);
       // Log.e(TAG, "section: "+ section );


        int i1 = signal.indexOf("I'm: ") + 4;

        String peerName = signal.substring(i1, i - 10);
        if (section == mViewPager.getCurrentItem()) {
            // enable messaging.
            Log.e(TAG, "We are in the same room, lets chat" );
            makeToastMessage(peerName);
        }
    }

    public void imAlive() {


        for (int i = 0; i < clients.size(); i++) {
            try {
                final int index = i;
                new Thread("aliveMessage") {
                    @Override
                    public void run() {
                        //clients.get(index).socket;

                        sendReceive2 = clients.get(index);

                        String M = "I'm: " + NICKNAME + ",my Mac ad:" + macAddress + " ,at: " + mViewPager.getCurrentItem() + "#";

                        boolean intactPipe = sendReceive2.write(M.getBytes());
                        if (!intactPipe) {
                            clients.remove(index);


                        }

                    }
                }.start();
            }catch (Exception e) {
                Log.e(TAG, "imAlive Exception "+ e.getMessage() );
            }
        }


    }

    // Sine : find free port
    private static int findFreePort() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            socket.setReuseAddress(true);
            int port = socket.getLocalPort();
            try {
                socket.close();
            } catch (IOException e) {
                // Ignore IOException on close()
            }
            return port;
        } catch (IOException e) {
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
        throw new IllegalStateException("Could not find a free TCP/IP port to start embedded Jetty HTTP Server on");
    }
    // Sina: At this level it is only Global chat fragment.
    private void setupViewPager(ViewPager viewPager) {
        //StatePagerAdapter adapter = new StatePagerAdapter(getSupportFragmentManager());

        mStatePagerAdapter.addFragment(new BaseFragment(), "BaseFragment");
        mStatePagerAdapter.addFragment(new GlobalChatFragment(), "GlobalChatFragment");
        mStatePagerAdapter.addFragment(new ChatRoomsFragment(), "ChatRoomsFragment");
        mStatePagerAdapter.addFragment(new ChatRouletteFragment(), "ChatRouletteFragment");
        mStatePagerAdapter.addFragment(new FriendsListFragment(), "FriendsListFragment");
        mStatePagerAdapter.addFragment(new SettingsFragment(), "SettingFragment");
        //adapter.addFragment(new Fragment(), "GlobalChatFragment");
        viewPager.setAdapter(mStatePagerAdapter);
       // viewPager.ontou;


    }

    public void makeToastMessage(String tempMsg) {
        Toast.makeText(Main.this, tempMsg, Toast.LENGTH_LONG).show();
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
                case 2:
                    byte[] readBuff2 = (byte[]) msg.obj;
                    String tempMsg2 = new String(readBuff2, 0, msg.arg1);
                    updateList(tempMsg2);
                    Log.e(TAG, "Message: "+ tempMsg2);
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


    public void beServer() {
        Log.i(TAG, "beServer: "+" I am a server now");
        serverClass = new ServerClass();
        serverClass.start();
        serverClass.setName("ServerClass Thread");

    }
    public void beClientToDevice(int index) {
        int i = index;


        while (!devices.get(i).isConnected()) {

            Log.i(TAG,
                    "beClient: Gonna be a client to " + devices.get(i).macAddress
                            +" port:" + devices.get(i).port
                            + " host ad " + devices.get(i).inetAddress );

            try {
                clientClass = new ClientClass(devices.get(i).inetAddress, devices.get(i).port, i);
                clientClass.start();

            }catch (Exception e) {
                Log.i(TAG, "Exception beClient: "+ e.getMessage());
            }

            while (!devices.get(i).isThreadEnded()) {
                //
            }
            devices.get(i).restartThread();
        }
        Log.e(TAG, "beClientToDevice: Is connected" );
    }




    public void chatRoomsButton(View view) {
        Toast.makeText(Main.this, "Chat Rooms", Toast.LENGTH_LONG).show();
       // Log.i(TAG, NICKNAME);
        Log.e(TAG, "My nickname is " + NICKNAME );

        //mViewPager.setCurrentItem(0);

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

    public void friendsButton(View view) {
        Toast.makeText(Main.this, "Friends", Toast.LENGTH_LONG).show();
        clientClass = new ClientClass(devices.get(0).inetAddress, devices.get(0).port, 1);
        clientClass.start();
    }

    public void settingsButton(View view) {
        Toast.makeText(Main.this, "Settings", Toast.LENGTH_LONG).show();
    }

    // Useful for debugging, shows all the threads
    public void seeThreads(){
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        for (Thread t : threadSet){
            Log.i(TAG, "seeThreads: " + t);

        }
    }


    public class ServerClass extends Thread {
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run() {
            try {
                Looper.prepare();
                Log.e(TAG, "ServerClass: port no is " + portNo);
                serverSocket = new ServerSocket(portNo);
                localPort = serverSocket.getLocalPort();


                int numConnections = 0;
                while(numConnections < 1000){
                    Log.e(TAG, "numCon "+ numConnections );
                    //socket = server.accept();

                    socket = serverSocket.accept();


                //    Log.e(TAG, "This client inetAd: "+ socket.getInetAddress());
                //    Log.e(TAG, "This client port: "+ socket.getPort());
                //    Log.e(TAG, "This client local port: "+ socket.getLocalPort());
                //    Log.e(TAG, "This client local ad: "+ socket.getLocalAddress());

                    sendReceive2 = new ConnectionHandler(socket);
                    sendReceive2.start();
                    clients.add(sendReceive2);
                    numConnections++;
                }

            } catch (IOException e) {

            }finally {
                Log.e(TAG, "close server socket");

                if (serverSocket != null && !serverSocket.isClosed()) {
                    try {
                        serverSocket.close();
                    } catch (IOException e2)
                    {
                        e2.printStackTrace(System.err);
                    }
                }

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
                Log.e(TAG, "SendReceive constructor Exception: " + e.getMessage());
                e.printStackTrace();
                return;
            }

        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            try {
                while (socket != null) {
                    try {
                        bytes = inputStream.read(buffer);
                        if (bytes > 0) {
                            handler.obtainMessage(2, bytes, -1, buffer).sendToTarget();


                        }
                        else {
                            inputStream.close();
                            throw new IOException("It's out of function");

                        }
                    } catch (IOException e) {

                        Log.e(TAG, "Exception in SendReceive: " + e.getMessage());
                     //   return;
                        break;
                    }
                }
                inputStream.close();
            }catch (IOException e) {

                Log.e(TAG, "Exception "+ e.getMessage() );
            }finally {
                if (socket.isClosed()) {
                    Log.e(TAG, "it's closed" );
                }

                if (socket != null && !socket.isClosed()) {
                    try {
                        Log.e(TAG, "closing the socket " );
                        socket.close();
                        inputStream.close();
                    } catch (IOException e2)
                    {
                        e2.printStackTrace(System.err);
                    }
                }

            }
        }

        public void write(byte[] bytes) {
            Log.e(TAG, "in write: ");
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                Log.i(TAG, e.getMessage());
                return;
            }
            Log.e(TAG, "gonna get out of write ");
        }
    }

    public class ClientClass extends Thread {
        Socket socket;
        String hostAddressString;
        InetAddress hostAddressInet;
        int port;
        int index;

        public ClientClass(InetAddress hostAddress, int port, int index) {
            this.hostAddressString = hostAddress.getHostAddress();
            this.hostAddressInet = hostAddress;
            this.port = port;
            this.index = index;
            socket = new Socket();
        }

        @Override
        public void run(){
            boolean success = true;
            try {

                Log.e(TAG, "ClientClass hostAdInet " + hostAddressInet );
                Log.e(TAG, "ClientClass hostAdString " + hostAddressString );
                Log.e(TAG, "ClientClass port " + port );


                InetSocketAddress isa = new InetSocketAddress(hostAddressInet, port);

                socket.connect(isa, 500);
                sendReceive = new SendReceive(socket);
                sendReceive.start();
                sendReceive.setName("sendRecieve/fromClient");

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                Log.e(TAG, "Denied Connection: " + this.hostAddressInet + " port " + this.port);
                success = false;

            }finally {
                if (success)
                    Log.e(TAG, "Successfully connected to :" + this.hostAddressInet + " port " + this.port);

                devices.get(this.index).changeStatus(success);
                devices.get(this.index).endThread();
                return;
            }
        }

    }

    //Registers name of the service in the local network
    public void registerService(int port) {
        Log.e(TAG, "registerService: WELCOME " + portNo );
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
                InetAddress hostToShow = serviceInfo.getHost();
                serviceInfo.setServiceType(SERVICE_TYPE);
                Log.i(TAG, "Service registered: name " + serviceName + " host ad:" + hostToShow);
                Log.e(TAG, "onServiceRegistered: ServiceInfo " + serviceInfo.toString()  );
                initializeResolveListener();
                initializeDiscoveryListener();
                nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);

                Log.i(TAG, "Finished NSD tasks, Now sets up the server. ");
                beServer();
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "Registration failed, error code: " + errorCode);
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "Service successfully unregistered");
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "Service unregistering failed, error code: " + errorCode);
            }
        };

    }

    public void initializeDiscoveryListener() {
        discoveryListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onDiscoveryStarted(String serviceType) {
                Log.e(TAG, "Service discovery started " + serviceType);
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


                devices.add(new Device(hostMacAddress, host, port));
                int index = devices.size() - 1;
                Log.i(TAG, devices.get(devices.size()-1).macAddress + devices.get(devices.size()-1).inetAddress);

                // Send a client request
                beClientToDevice(index);
            }
        };
    }
    private void tearDownNSD() {

        try {
            nsdManager.unregisterService(registrationListener);
            nsdManager.stopServiceDiscovery(discoveryListener);
            discoveryListener = null;

            registrationListener = null;
            nsdManager = null;

            Log.e(TAG, "NSD resources removed");


        } catch (Exception e) {
            Log.e(TAG, "tearDownNSD: " + e.getMessage() );
        }finally {
            finish();
        }


    }
    public void tearDownChecker(int switch1) {
        if (switch1 == 3 && cleaner) { // Means it has been to at least one fragment before
            tearDownNSD();
        }
        else if(switch1 != 3) { // It's a call from the main activity pause.
            cleaner = true;
        }

    }
    public void showDevices() {
        int cnt = 0;
        for(Device i : devices) {
            String k = i.macAddress;
            String x = Integer.toString(cnt);
            InetAddress kk = i.inetAddress;
            int port = i.port;
            Log.i(TAG, x + "mcAd" + k + " ,host " +kk.toString()+ " , port " + port);
            cnt += 1;
        }
    }
    public void changeView(int pos) {
        mViewPager.setCurrentItem(pos);
    }
    public void reportOnFragments() {
        int count = mStatePagerAdapter.getCount();
        Log.d(TAG, "no. fragments" + count);
    }



    @Override
    public void onBackPressed() {

        changeView(0); // Base Fragment (Main menu)

        int count = mStatePagerAdapter.getCount();

        if (count == 0)
            super.onBackPressed();
        else {

        }
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume: ");
    //   tearDownChecker(0);
        super.onResume();


    }

    @Override
    protected void onPause() {

        mainActivityPauses += 1;
        Log.e(TAG, "onPause: " + mainActivityPauses);
        if (mainActivityPauses >= 2) {
            tearDownChecker(1);
        }


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
    int port;
    private boolean isConnected;
    private boolean threadEnded;


    public Device (String macAddress, InetAddress inetAddress, int port){
        this.macAddress = macAddress;
        this.inetAddress = inetAddress;
        this.port = port;
        this.isConnected = false;
        this.threadEnded = false;
    }
    public void changeStatus(boolean status) {
        this.isConnected = status;

    }
    public boolean isThreadEnded() {
        return this.threadEnded;
    }

    public void endThread() {
        this.threadEnded = true;
    }
    public void restartThread() {
        this.threadEnded = false;
    }

    public boolean isConnected() {
        return isConnected;
    }
}
