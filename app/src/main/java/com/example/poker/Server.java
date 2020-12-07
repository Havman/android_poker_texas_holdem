package com.example.poker;

import android.app.Activity;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Server {
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

    Map<DataOutputStream, Boolean> isEvenMap = new HashMap<>();

    Deck deck = new Deck();
    int clientTurnID = 0;
    int startingCoins = 1000;
    int coinsToEven = 0;
    int allWageredCoins = 0;
    Card[] communityCards;
    Card fourthCard;
    Card fifthCard;

    String threeCardMsg = "";
    String fourthCardMsg = "";
    String fifthCardMsg = "";


    int smallBlind = 5;
    int bigBlind = 10;

    int roundNumber = 0;

    private enum REGISTRATION_STATUS{
        REGISTERED,
        NON_REGISTERED
    }

    public Server(Context context, Activity activity){
        deck.shuffleDeck();
        communityCards = deck.getThreeCards();
        fourthCard = deck.pullCard();
        fifthCard = deck.pullCard();
        this.mContext = context;
        this.mActivity = activity;
        this.mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        for(Card c : communityCards){
            threeCardMsg += c.getImage() + ";";
        }
        fourthCardMsg += fourthCard.getImage();
        fifthCardMsg += fifthCard.getImage();
        //Start a thread with the server socket ready to receive connections...
        try {
           openConnectionThread();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showToast(String message) {
        Toast.makeText(this.mContext, message, Toast.LENGTH_SHORT).show();
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
            mDiscoveryServiceName = NsdServiceInfo.getServiceName();

            Toast.makeText(mContext, "Registered DEVICE!", Toast.LENGTH_LONG).show();
            android.util.Log.e("TrackingFlow", "This device has been registered to be discovered through NSD...:" + mDiscoveryServiceName);
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
            mNsdManager.unregisterService(mRegistrationListener);
        } catch (Exception e){e.printStackTrace();}
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
                                    ArrayList<DataOutputStream> outputsList;
                                    while (true) {
                                        int length = socketInput.read(buffer);
                                        final JSONObject receivedJson = new JSONObject(new String(buffer, 0, length));
                                        String msgType =  (String)receivedJson.get("Type");
                                        Log.e("Server: msgGot", receivedJson.toString());
                                        String msg = regexHandler.decodeMsg(Server.this, receivedJson);
                                        Log.e("Server: msgSent", receivedJson.toString());
                                        if (msgType.equals("Solo")){
                                            socketOutput.write(msg.getBytes());
                                        }
                                        else if(msgType.equals("Server")){
                                            roundNumber = 0;
                                            outputsList = new ArrayList<>(outputs);
                                            int i;
                                            coinsToEven = 10;
                                            String tmpMsg = (String) receivedJson.get("Message");
                                            String[] tmpHand = tmpMsg.split(";");
                                            for (i=0; i<outputsList.size(); i++) {
                                                if(i != 1)
                                                    isEvenMap.put(outputsList.get(i), false);
                                                if (i == 0)
                                                    msg = msg.replace(tmpMsg, tmpHand[i] + ';' + smallBlind + ",15,10");
                                                else if (i == 1) {
                                                    isEvenMap.put(outputsList.get(i), true);
                                                    msg = msg.replace(tmpMsg, tmpHand[i] + ';' + bigBlind + ",15,10");
                                                }
                                                else
                                                    msg = msg.replace(tmpMsg, tmpHand[i] + ";0,15,10");
                                                tmpMsg = tmpHand[i];
                                                outputsList.get(i).write(msg.getBytes());
                                                outputsList.get(i).flush();
                                            }
                                            clientTurnID = (i)%outputs.size();
                                            outputsList.get(clientTurnID).write("{'Type': 'Solo', 'About': 'Visibility', 'Message': 'noWait'}".getBytes());
                                            outputsList.get(clientTurnID).flush();
                                        }
                                        else{
                                            outputsList = new ArrayList<>(outputs);
                                            clientTurnID = (clientTurnID+outputs.size())%outputs.size();
                                            isEvenMap.put(outputsList.get(clientTurnID), true);
                                            clientTurnID += 1;
                                            clientTurnID %= outputs.size();
                                            for (DataOutputStream output : outputs) {
                                                output.write(msg.getBytes());
                                                output.flush();
                                            }
                                            outputsList.get(clientTurnID).write("{'Type': 'Solo', 'About': 'Visibility', 'Message': ''}".getBytes());
                                            outputsList.get(clientTurnID).flush();
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