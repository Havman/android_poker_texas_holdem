package com.example.poker;

import android.util.Log;
import android.widget.ImageView;

import org.json.JSONObject;

public class RegexHandler {

    public String decodeMsg(NSDListen listener, JSONObject jsonResponse){
        String msg;
        try{
            msg = (String) jsonResponse.get("Message");
            switch ( (String) jsonResponse.get("About") ){
                case "Info":
                    Log.e("RegexHandler", msg);
                    break;
                default:
                    return jsonResponse.toString();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public String decodeResponse(final NSDDiscover discover, JSONObject jsonResponse){
        String msg;
        String[] splitMessage;
        final int resID, resID1, resID2;
        try{
            msg = (String) jsonResponse.get("Message");
            switch ( (String) jsonResponse.get("About") ){
                case "Info":
                    discover.showToast(msg);
                    break;
                case "NextCard":
                    splitMessage = msg.split(" ");
                    resID = discover.mActivity.getResources().getIdentifier(splitMessage[splitMessage.length - 1], "drawable", discover.mActivity.getPackageName());
                    discover.mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((ImageView) discover.mActivity.findViewById(R.id.cardPic)).setImageResource(resID);
                        }
                    });
                    break;
                case "DealCard":
                    splitMessage = msg.split(" ");
                    resID1 = discover.mActivity.getResources().getIdentifier(splitMessage[splitMessage.length - 7], "drawable", discover.mActivity.getPackageName());
                    resID2 = discover.mActivity.getResources().getIdentifier(splitMessage[splitMessage.length - 1], "drawable", discover.mActivity.getPackageName());
                    Log.e("ESSA", String.valueOf( resID1));
                    discover.mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((ImageView) discover.mActivity.findViewById(R.id.hand1)).setImageResource(resID1);
                            ((ImageView) discover.mActivity.findViewById(R.id.hand2)).setImageResource(resID2);
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
