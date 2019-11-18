package com.example.p3;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ConnectionHandler  extends Thread{
    private static final String TAG = "WiFi Conn/Handler";
    Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff, 0, msg.arg1);
                   // makeToastMessage(tempMsg);
                    //Toast.makeText(Main.this, tempMsg, Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    byte[] readBuff2 = (byte[]) msg.obj;
                    String tempMsg2 = new String(readBuff2, 0, msg.arg1);
                    Log.d(TAG, "Message: "+ tempMsg2);
                    break;
            }

            return true;
        }
    });

    public ConnectionHandler(Socket socket){
        this.socket = socket;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() { // I added a try and catch to be able to close inputStream which konrad did not implement in SendReceive Class
        try {
            Log.i(TAG, "Client Accepted");
            byte[] buffer = new byte[1024];
            int bytes;

            while (socket != null) {
                try {
                    bytes = inputStream.read(buffer);
                    if (bytes > 0) {
                        handler.obtainMessage(2, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "ConnectionHandler Exception: " + e.getMessage() );
                    //e.printStackTrace();
                    break;

                }
            }

            inputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "Exception" + e.getMessage() );
        }finally {
            if(socket!=null && !socket.isClosed()) {
                try {
                    socket.close();
                    inputStream.close();

                }catch (IOException e) {
                    Log.e(TAG, "exception" + e.getMessage() );
                }
            }
        }
    }
    public boolean write(byte[] bytes) {
        try {
            outputStream.write(bytes);

        } catch (IOException e) {
            Log.i(TAG, "Cannot write,"+ e.getMessage());
            return false;
        }
        return true;
    }
}
