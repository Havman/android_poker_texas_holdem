package com.example.poker;

import android.app.Activity;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;


public class NSDDiscover {

    public static final String TAG = "TrackingFlow";
    public String mDiscoveryServiceName = "NSDDoEpicCodingDiscover";
    public String serviceType = "_poker._tcp.";
    private Context mContext;
    private Activity mActivity;
    private NsdManager mNsdManager;
    private DiscoveryListener mListener;
    private String mHostFound;
    private int mPortFound;
    private DISCOVERY_STATUS mCurrentDiscoveryStatus = DISCOVERY_STATUS.OFF;
    private Card card = new Card();

    private enum DISCOVERY_STATUS{
        ON,
        OFF
    }

    public NSDDiscover(Context context, DiscoveryListener listener, Activity activity) {
        this.mContext = context;
        this.mActivity = activity;
        this.mListener = listener;
        this.mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
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

    public void sayHello(){
        if(mHostFound == null || mPortFound <= 0){
            showToast("Device not found");
            return;
        }
        new SocketConnection().sayHello(mHostFound, mPortFound);

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
            mNsdManager.stopServiceDiscovery(mDiscoveryListener); //TODO: You can remove this line if necessary, that way the discovery process continues...
            setHostAndPortValues(serviceInfo);
            if(mListener != null){
                mListener.serviceDiscovered(mHostFound, mPortFound);
            }
        }
    };

    private void setHostAndPortValues(NsdServiceInfo serviceInfo){
        mHostFound = serviceInfo.getHost().getHostAddress();
        mPortFound = serviceInfo.getPort();
    }

    private class SocketConnection {
        private Socket mSocket;
        public void sayHello(final String host, final int port){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mSocket = new Socket();
                    SocketAddress address = new InetSocketAddress(host, port);
                    try {
                        android.util.Log.e("TrackingFlow", "Trying to connect to: " + host);
                        mSocket.connect(address);
                        DataOutputStream os = new DataOutputStream(mSocket.getOutputStream());
                        DataInputStream is = new DataInputStream(mSocket.getInputStream());
                        //Send a message...
                        card = card.getNextCard();
                        os.write(card.toString().getBytes());
                        os.flush();
                        android.util.Log.e("TrackingFlow", "Message SENT!!!");

                        //Read the message
                        int bufferSize = 1024;
                        byte[] buffer = new byte[bufferSize];
                        StringBuilder sb = new StringBuilder();
                        int length = Integer.MAX_VALUE;
                        try {
                            while (length >= bufferSize) {
                                length = is.read(buffer);
                                sb.append(new String(buffer, 0, length));
                            }
                            final String receivedMessage = sb.toString();
                            //TODO:Send message on the main thread, Note: We don't need to create a thread every time, this is just for prototyping...
                            new Handler(mContext.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, "Message received: " + receivedMessage, Toast.LENGTH_LONG).show();
                                    String [] splittedReceivedMessage = receivedMessage.split(" " );
                                    int resID = mActivity.getResources().getIdentifier(splittedReceivedMessage[splittedReceivedMessage.length - 1] , "drawable", mActivity.getPackageName());
                                    ((ImageView) mActivity.findViewById(R.id.cardPic)).setImageResource(resID);
                                }
                            });
                        } catch (Exception e) {e.printStackTrace();}
                        os.close();
                        is.close();

                    } catch (IOException e) {e.printStackTrace();}
                }
            }).start();
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
