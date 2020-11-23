//package com.example.poker;
//
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.Button;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//public class Rooms extends AppCompatActivity {
//
//    private Button discoverButton;
//    private TextView msgText;
//
//    private ListView listView;
//
//    private NSDListen mNSDListener;
//    private NSDDiscover mNSDDiscover;
//
//    private Button mRegisterBtn;
//    private Button mDiscoverBtn;
//    private Button mSayHelloBtn;
//
//    public void showToast(String message) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
//    }
//
//    private void initialize() {
//        discoverButton = findViewById(R.id.discoverButton);
//        listView = findViewById(R.id.peerListView);
//        msgText = findViewById(R.id.readMsg);
//
//        mNSDListener = new NSDListen(this);
//        mNSDDiscover = new NSDDiscover(this, mDiscoveryListener);
//
//    }
//
//    private NSDDiscover.DiscoveryListener mDiscoveryListener = new NSDDiscover.DiscoveryListener() {
//        @Override
//        public void serviceDiscovered(String host, int port) {
//            //This callback is on a worker thread...
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    showToast("VISIBLE");
//                }
//            });
//        }
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.nsd_activity);
//        initialize();
//
//        discoverButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mNSDDiscover.sayHello();
//            }
//        });
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//        });
//
//        //Show selection alert dialog...
//        new AlertDialog.Builder(this)
//            .setMessage("select_mode_dlg_msg")
//            .setPositiveButton(getString(R.string.app_name), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    mDiscoverBtn.setVisibility(View.GONE);
//                    dialog.dismiss();
//                }
//            })
//            .setNegativeButton(getString(R.string.app_name), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    mRegisterBtn.setVisibility(View.GONE);
//                    dialog.dismiss();
//                }
//            })
//            .setCancelable(false)
//            .show();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mNSDListener.shutdown();
//        mNSDDiscover.shutdown();
//    }
//}
package com.example.poker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.wifi.p2p.WifiP2pManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.Collections;

public class Rooms extends Activity {

    private NSDListen mNSDListener;
    private NSDDiscover mNSDDiscover;

    private Context context = this;
    private Activity activity = this;

    private Button mRegisterBtn;
    private Button mDiscoverBtn;
    private Button mSayHelloBtn;
    private Button mStartGameBtn;
    private Button mDealCardsBtn;

    private String msg;

    private JSONObject reader;

    private ImageView mCard1;
    private ImageView mCard2;

    private ImageView mCard;

    private Card card = new Card();

    private Card card1 = new Card();
    private Card card2 = new Card();

    private Deck deck = new Deck();

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void setImageByCard(final ImageView imgView, final Card card) {
        String img = card.getImage();
        int resID = getResources().getIdentifier(img , "drawable", getPackageName());
        imgView.setImageResource(resID);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nsd_activity);

        deck.shuffleDeck();

        new AlertDialog.Builder(this)
                .setMessage("Select if you want to host or join a game")
                .setPositiveButton(getString(R.string.host), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mNSDListener = new NSDListen(context, activity);
                        mDiscoverBtn.setVisibility(View.GONE);
                        mCard.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.join), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mNSDDiscover = new NSDDiscover(context, mDiscoveryListener, activity);
                        mRegisterBtn.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();

        mRegisterBtn = findViewById(R.id.register);
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNSDListener.registerDevice();
                mRegisterBtn.setText(getString(R.string.hosting));
                findViewById(R.id.startGame).setVisibility(View.VISIBLE);
            }
        });

        mDiscoverBtn = findViewById(R.id.discover);
        mDiscoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNSDDiscover.discoverServices();
            }
        });

        mSayHelloBtn = findViewById(R.id.sayHello);
        mSayHelloBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    msg = "{'Type': 'Multi', 'About': 'NextCard', 'Message': '" + card.getNextCard().toString() + "'}";
                    mNSDDiscover.sayHello(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



        mCard1 = findViewById(R.id.hand1);
        mCard2 = findViewById(R.id.hand2);

        mDealCardsBtn = findViewById(R.id.dealCards);
        mDealCardsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card1 = deck.dealCard();
                card2 = deck.dealCard();
                try {
                    msg = "{'Type': 'Solo', 'About': 'DealCard', 'Message': '" + card1.toString() + " " + card2.toString() + "'}" ;
                    mNSDDiscover.sayHello(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mCard = findViewById(R.id.cardPic);

        //Show selection alert dialog...


        mStartGameBtn = findViewById(R.id.startGame);
        mStartGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCard.setVisibility(View.VISIBLE);
                mDiscoverBtn.setVisibility(View.VISIBLE);
                mDealCardsBtn.setVisibility(View.VISIBLE);
                mStartGameBtn.setVisibility(View.GONE);
                mRegisterBtn.setVisibility(View.GONE);
                mNSDDiscover = new NSDDiscover(context, mDiscoveryListener, activity);
                mNSDDiscover.discoverServices();
            }
        });
    }

    private NSDDiscover.DiscoveryListener mDiscoveryListener = new NSDDiscover.DiscoveryListener() {
        @Override
        public void serviceDiscovered(String host, int port) {
            //This callback is on a worker thread...
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDealCardsBtn.setVisibility(View.VISIBLE);
                    mSayHelloBtn.setVisibility(View.VISIBLE);
                    mDiscoverBtn.setVisibility(View.GONE);
                    showToast("Connected");
                    try {
                        msg = "{'Type': 'Solo', 'About': 'Info', 'Message': 'Connected'}";
                        mNSDDiscover.sayHello(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNSDListener.shutdown();
        mNSDDiscover.shutdown();
    }
}