package com.example.poker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;


public class Client {

    public static final String TAG = "TrackingFlow";
    String mDiscoveryServiceName = "NSDDoEpicCodingDiscover";
    String serviceType = "_poker._tcp.";
    Context mContext;
    final Activity mActivity;
    NsdManager mNsdManager;
    DiscoveryListener mListener;
    String serverHost;
    RegexHandler regexHandler = new RegexHandler();
    int serverPort;
    SocketConnection connection;
    DISCOVERY_STATUS mCurrentDiscoveryStatus = DISCOVERY_STATUS.OFF;
    int balance = 1000;
    int allCoinsInRound = 0;
    int myCoinsInRound = 0;
    int toEven = 0;

    private enum DISCOVERY_STATUS{
        ON,
        OFF
    }

    public Client(Context context, DiscoveryListener listener, Activity activity) {
        this.mContext = context;
        this.mActivity = activity;
        this.mListener = listener;
        this.mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    public void setHandImg(String imageStr){
        String[] images = imageStr.split(" ");
        int resID1 = mActivity.getResources().getIdentifier(images[0], "drawable", mActivity.getPackageName());
        ((ImageView) mActivity.findViewById(R.id.hand1)).setImageResource(resID1);
        int resID2 = mActivity.getResources().getIdentifier(images[1], "drawable", mActivity.getPackageName());
        ((ImageView) mActivity.findViewById(R.id.hand2)).setImageResource(resID2);
    }

    public void setWageredCoins(String msg) {
        allCoinsInRound += Integer.parseInt(msg);
        ((TextView) mActivity.findViewById(R.id.wageredCoins)).setText(String.valueOf(allCoinsInRound));
    }

    public void setCoinsTxt(){
        ((TextView) mActivity.findViewById(R.id.toEven)).setText(String.valueOf(toEven));
        ((TextView) mActivity.findViewById(R.id.balance)).setText(String.valueOf(balance));
        ((TextView) mActivity.findViewById(R.id.coinsInRound)).setText(String.valueOf(myCoinsInRound));
    }

    public void setCoins(String str) {
        String [] splitStr = str.split(",");
        int myWage = Integer.parseInt(splitStr[0]);
        balance -= myWage;
        myCoinsInRound += myWage;
        allCoinsInRound += Integer.parseInt(splitStr[1]);
        toEven = Integer.parseInt(splitStr[2]) - myCoinsInRound;
        setCoinsTxt();
        ((TextView) mActivity.findViewById(R.id.wageredCoins)).setText(splitStr[1]);
    }

    public void setButtons(String msg, Boolean isVisible) {
        if(isVisible) {
            mActivity.findViewById(R.id.even).setVisibility(View.VISIBLE);
            mActivity.findViewById(R.id.raise).setVisibility(View.VISIBLE);
            mActivity.findViewById(R.id.pass).setVisibility(View.VISIBLE);

            if (msg.equals("wait")) {
                mActivity.findViewById(R.id.wait).setVisibility(View.VISIBLE);
            }
        }
        else {
            mActivity.findViewById(R.id.even).setVisibility(View.GONE);
            mActivity.findViewById(R.id.raise).setVisibility(View.GONE);
            mActivity.findViewById(R.id.pass).setVisibility(View.GONE);

            if (msg.equals("wait")) {
                mActivity.findViewById(R.id.wait).setVisibility(View.GONE);

            }
        }
    }

    public void evenCoins() {
        balance -= toEven;
        myCoinsInRound += toEven;
        toEven = 0;
        setCoinsTxt();
    }

    public void showToast(String message) {
        Toast.makeText(this.mContext, message, Toast.LENGTH_SHORT).show();
    }

    public void discoverServices() {
        if(mCurrentDiscoveryStatus == DISCOVERY_STATUS.ON) return;
        showToast("Discover SERVICES!");
        mCurrentDiscoveryStatus = DISCOVERY_STATUS.ON;
        mNsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    public void startConnection() {
        try {
            connection = new SocketConnection(serverHost, serverPort);
            Thread bindHandler = connection.bindHandler();
            bindHandler.start();
            bindHandler.join();
            connection.receiveHandler().start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) throws Exception {
        if (connection == null) {
            startConnection();
        }
        Thread sender = connection.sendHandler(msg.getBytes());
        sender.start();
        sender.join();
    }

    NsdManager.ResolveListener mResolveListener = new NsdManager.ResolveListener() {
        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            Log.e(TAG, "Resolve failed" + errorCode);
        }

        @Override
        public void onServiceResolved(NsdServiceInfo serviceInfo) {
            Log.e(TAG, "Resolve Succeeded. " + serviceInfo);

            if (serviceInfo.getServiceName().equals(mDiscoveryServiceName)) {
                Log.d(TAG, "Same IP.");
                return;
            }
            showToast("CONNECTION FOUND!");
            setHostAndPortValues(serviceInfo);
            if(mListener != null){
                mListener.serviceDiscovered(serverHost, serverPort);
            }
        }
    };

    private void setHostAndPortValues(NsdServiceInfo serviceInfo){
        serverHost = serviceInfo.getHost().getHostAddress();
        serverPort = serviceInfo.getPort();
    }

    private class SocketConnection {
        private Socket socket;
        private String host;
        private int port;
        private DataOutputStream out;
        private DataInputStream in;

        public SocketConnection(final String host, final int port) {
            this.host = host;
            this.port = port;
        }

        public Thread bindHandler() {
            return new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        socket = new Socket(host, port);
                        Log.e("TrackingFlow", "Trying to connect to: " + host);
                        out = new DataOutputStream(socket.getOutputStream());
                        in = new DataInputStream(socket.getInputStream());
                        Log.e("TrackingFlow", "Host connected!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        public Thread sendHandler(final byte[] message) {
            return new Thread(new Runnable() {
                @Override
                public void run() {
                    if (out != null) {
                        try {
                            Log.e("Client: msgGot", message.toString());
                            out.write(message);
                            out.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        public Thread receiveHandler() {
            return new Thread(new Runnable() {
                @Override
                public void run() {
                    if (in != null) {
                        try {
                            int bufferSize = 1024;
                            byte[] buffer = new byte[bufferSize];
                            while (true) {
                                int length = in.read(buffer);
                                final JSONObject receivedJson = new JSONObject(new String(buffer, 0, length));
                                regexHandler.decodeResponse(Client.this, receivedJson);
                                Log.e("Client: msgGot", receivedJson.toString());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private NsdManager.DiscoveryListener mDiscoveryListener = new NsdManager.DiscoveryListener() {

        @Override
        public void onDiscoveryStarted(String regType) {
            Log.d(TAG, "Service discovery started");
        }

        @Override
        public void onServiceFound(NsdServiceInfo service) {
            Log.d(TAG, "Service discovery success" + service);
            if (!service.getServiceType().equals(serviceType)) {
                Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
            } else if (service.getServiceName().equals(mDiscoveryServiceName)) {
                Log.d(TAG, "Same machine: " + mDiscoveryServiceName);
            } else {
                mNsdManager.resolveService(service, mResolveListener);
            }
        }

        @Override
        public void onServiceLost(NsdServiceInfo service) {
            Log.e(TAG, "service lost" + service);
        }

        @Override
        public void onDiscoveryStopped(String serviceType) {
            Log.i(TAG, "Discovery stopped: " + serviceType);
        }

        @Override
        public void onStartDiscoveryFailed(String serviceType, int errorCode) {
            Log.e(TAG, "Discovery failed: Error code:" + errorCode);
            mNsdManager.stopServiceDiscovery(this);
        }

        @Override
        public void onStopDiscoveryFailed(String serviceType, int errorCode) {
            Log.e(TAG, "Discovery failed: Error code:" + errorCode);
            mCurrentDiscoveryStatus = DISCOVERY_STATUS.OFF;
            mNsdManager.stopServiceDiscovery(this);
        }
    };

    /**
     * Registration Listener for our NDS Listen logic
     */
    private NsdManager.RegistrationListener mRegistrationListener = new NsdManager.RegistrationListener() {
        @Override
        public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
            mDiscoveryServiceName = NsdServiceInfo.getServiceName();
            Log.e("TrackingFlow", "This device has been registered to be discovered through NSD...");
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

    public void shutdown(){
        try {
            mNsdManager.stopServiceDiscovery(mDiscoveryListener);
        }catch(Exception e){e.printStackTrace();}
    }

    public interface DiscoveryListener {
        void serviceDiscovered(String host, int port);
    }
}
