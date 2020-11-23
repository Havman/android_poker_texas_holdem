package com.example.poker;

import android.app.Activity;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;


public class NSDListen {
<<<<<<< HEAD
    private static final String TAG = "TrackingFlow";
    private NsdManager mNsdManager;
    public String mDiscoveryServiceName = "NSDDoEpicCodingListener";
    public String serviceType = "_poker._tcp.";
    private Context mContext;
    private Activity mActivity;
    private int mSelectedPort = -1;
    private SocketServerConnection mSocketServerConnection;
    private ServerSocket mDiscoverableServerSocket;
    private REGISTRATION_STATUS mCurrentRegistrationStatus = REGISTRATION_STATUS.NON_REGISTERED;
    private Card card = new Card();
=======
    static final String TAG = "TrackingFlow";
    NsdManager mNsdManager;
    String mDiscoveryServiceName = "PokerServer";
    String serviceType = "_poker._tcp.";
    Context mContext;
    Activity mActivity;
    int serverPort = 5701;
    ServerSocket serverSocket;
    REGISTRATION_STATUS mCurrentRegistrationStatus = REGISTRATION_STATUS.NON_REGISTERED;
    RegexHandler regexHandler = new RegexHandler();
    Set<DataInputStream> inputs = new HashSet<>();
    Set<DataOutputStream> outputs = new HashSet<>();

>>>>>>> c1b7c08

    private enum REGISTRATION_STATUS{
        REGISTERED,
        NON_REGISTERED
    }

    public NSDListen(Context context, Activity activity){
        this.mContext = context;
        this.mActivity = activity;
        this.mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);

        //Start a thread with the server socket ready to receive connections...
<<<<<<< HEAD
        mSocketServerConnection = new SocketServerConnection();
        mSocketServerConnection.openConnection();
=======
        try {
           openConnectionThread();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showToast(String message) {
        Toast.makeText(this.mContext, message, Toast.LENGTH_SHORT).show();
>>>>>>> c1b7c08
    }

    /**
     * This method should be triggered after createServerThread has been executed...
     */
    public void registerDevice(){
        if(mCurrentRegistrationStatus == REGISTRATION_STATUS.REGISTERED)return;

        if(serverPort > -1) {
            setupDeviceRegistration();
        } else {
            Log.d(TAG, "No Socket available..., make sure this method is called after createServerThread has been executed...");
        }
    }

    private void setupDeviceRegistration(){
        NsdServiceInfo serviceInfo  = new NsdServiceInfo();
        serviceInfo.setPort(serverPort);
        serviceInfo.setServiceName(mDiscoveryServiceName);
        serviceInfo.setServiceType(serviceType);

        mCurrentRegistrationStatus = REGISTRATION_STATUS.REGISTERED;
        mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
    }

    private NsdManager.RegistrationListener mRegistrationListener = new NsdManager.RegistrationListener() {
        @Override
        public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
<<<<<<< HEAD
=======
            mDiscoveryServiceName = NsdServiceInfo.getServiceName();

            Toast.makeText(mContext, "Registered DEVICE!", Toast.LENGTH_LONG).show();
            android.util.Log.e("TrackingFlow", "This device has been registered to be discovered through NSD...:" + mDiscoveryServiceName);
>>>>>>> c1b7c08
        }

        @Override
        public void onRegistrationFailed(NsdServiceInfo arg0, int arg1) {
        }

        @Override
        public void onServiceUnregistered(NsdServiceInfo arg0) {
        }

        @Override
        public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
        }

    };

    public void shutdown() {
        try {
<<<<<<< HEAD
            if(mSocketServerConnection != null){
                mSocketServerConnection.release();
            }
        }catch (Exception e){e.printStackTrace();}
=======
            mNsdManager.unregisterService(mRegistrationListener);
        } catch (Exception e){e.printStackTrace();}
>>>>>>> c1b7c08
    }


    public void openConnectionThread() throws IOException {
        serverSocket = new ServerSocket(serverPort);
        Thread connectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Log.e("TrackingFlow", "Waiting for connection...");
                        Socket socket = serverSocket.accept();
                        Log.e("TrackingFlow", "Connection found...");
                        final DataInputStream socketInput;
                        socketInput = new DataInputStream(socket.getInputStream());
                        inputs.add(socketInput);
                        final DataOutputStream socketOutput = new DataOutputStream(socket.getOutputStream());
                        outputs.add(socketOutput);
                        Log.e("Socket IP", socket.getInetAddress().toString());
                        Log.e("Socket port", String.valueOf(socket.getPort()));
                        Thread clientHandler = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    int bufferSize = 1024;
                                    byte[] buffer = new byte[bufferSize];

                                    while (true) {
                                        int length = socketInput.read(buffer);
                                        final JSONObject receivedJson = new JSONObject(new String(buffer, 0, length));
                                        String msgType =  (String)receivedJson.get("Type");
                                        Log.e("Server: msgGot", receivedJson.toString());
                                        String msg = regexHandler.decodeMsg(NSDListen.this, receivedJson);
                                        Log.e("Server: msgSent", receivedJson.toString());
                                        if (msgType.equals("Solo")){
                                            socketOutput.write(msg.getBytes());
                                        }
                                        else{
                                            for (DataOutputStream output : outputs) {
                                                output.write(msg.getBytes());
                                                output.flush();
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        clientHandler.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        connectionThread.start();
    }
}