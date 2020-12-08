package com.example.poker;

import android.util.Log;
import android.widget.ImageView;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
                case "NextRound":
                    boolean nextRound = true;
                    int roundNumber;

                    for (Map.Entry<DataOutputStream, Boolean> entry : server.isEvenMap.entrySet()) {
                        if (!entry.getValue())
                            nextRound = false;
                    }
                    if(nextRound){
                        jsonResponse.remove("Message");
                        jsonResponse.remove("About");
                        roundNumber = server.roundNumber;
                        Log.e("roundNum", "" + roundNumber);
                        if(roundNumber == 0) {
                            jsonResponse.put("About", "ThreeCards");
                            jsonResponse.put("Message", server.threeCardMsg);
                        }
                        if(roundNumber == 1){
                            jsonResponse.put("About", "FourthCard");
                            jsonResponse.put("Message", server.fourthCard.getImage());
                        }
                        if(roundNumber == 2){
                            jsonResponse.put("About", "FifthCard");
                            jsonResponse.put("Message", server.fifthCard.getImage());
                        }
                        if(roundNumber == 3){
                            jsonResponse.put("About", "LastRound");
                            jsonResponse.put("Message", "Msg");
                        }
                        server.roundNumber = (server.roundNumber + 1)%4;
                        for (Map.Entry<DataOutputStream, Boolean> entry : server.isEvenMap.entrySet()) {
                            server.isEvenMap.put(entry.getKey(), false);
                        }
                    }
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

    public void decodeResponse(final Client client, JSONObject jsonResponse){
        final String msg;
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
                            client.setWageredCoins(msg);
                        }
                    });
                    break;
                case "Visibility":
                    client.mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            client.setButtons(Boolean.valueOf(msg));
                        }
                    });
                    break;
                case "ThreeCards":
                    client.mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            client.setThreeCardsImg(msg);
                            client.setNextRound();
                        }
                    });
                    break;
                case "FourthCard":
                    client.mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            client.setFourthCard(msg);
                            client.setNextRound();
                        }
                    });
                    break;
                case "FifthCard":
                    client.mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            client.setFifthCard(msg);
                            client.setNextRound();
                        }
                    });
                    break;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
