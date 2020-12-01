package com.example.poker;

import android.util.Log;
import android.widget.ImageView;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegexHandler {

    public String decodeMsg(Server server, JSONObject jsonResponse){
        String msg;
        try{
            msg = (String) jsonResponse.get("Message");
            switch ( (String) jsonResponse.get("About") ){
                case "Info":
                    Log.e("RegexHandler", msg);
                    break;
                case "StartGame":
                    int numOfClients = server.outputs.size();
                    String newMsg = "";
                    for (int i=0; i<numOfClients; i++){
                        Card[] hand = server.deck.getHand();
                        newMsg += hand[0].getImage() + " " + hand[1].getImage() + ";";
                    }
                    jsonResponse.remove("Message");
                    jsonResponse.put("Message", newMsg);
                    return jsonResponse.toString();
                default:
                    return jsonResponse.toString();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public String decodeResponse(final Client client, JSONObject jsonResponse){
        final String msg;
        String[] splitMessage;
        final int communityCard1ResID, communityCard2ResID, communityCard3ResID,
                    communityCard4ResID, communityCard5ResID;
        try{
            msg = (String) jsonResponse.get("Message");
            switch ( (String) jsonResponse.get("About") ){
                case "Info":
                    client.showToast(msg);
                    break;
                case "StartGame":
                    client.mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("MSG", msg);
                            String [] tmpMsg = msg.split(";");
                            client.setHandImg(tmpMsg[0]);
                            client.setCoins(tmpMsg[1]);
                        }
                    });
                    break;
                case "Even":
                    client.mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("MSG", msg);
                            client.setWageredCoins(msg);
                        }
                    });
                    break;
                case "Visibility":
                    client.mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            client.setButtons(msg, true);
                        }
                    });
                    break;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
}
