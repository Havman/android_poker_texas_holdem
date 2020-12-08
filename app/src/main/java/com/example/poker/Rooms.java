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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.UiThread;

public class Rooms extends Activity {

    private Server mNSDListener;
    private Client mClient;

    private Context context = this;
    private Activity activity = this;

    private Button mRegisterBtn;
    private Button mDiscoverBtn;
    private Button joinAsClientBtn;
    private Button startGameBtn;

    private Button waitBtn;
    private Button evenBtn;
    private Button raiseBtn;
    private Button passBtn;

    private TextView balanceTxt;
    private TextView toEven;
    private TextView coinsInRound;

    private int balance = 1000;

    private String msg;

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

        new AlertDialog.Builder(this)
                .setMessage("Select if you want to host or join a game")
                .setPositiveButton(getString(R.string.host), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mNSDListener = new Server(context, activity);
                        mDiscoverBtn.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.join), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mClient = new Client(context, mDiscoveryListener, activity);
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
                findViewById(R.id.joinAsClient).setVisibility(View.VISIBLE);
            }
        });

        mDiscoverBtn = findViewById(R.id.discover);
        mDiscoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClient.discoverServices();
            }
        });

        balanceTxt = findViewById(R.id.balance);
        startGameBtn = findViewById(R.id.startGame);
        startGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    msg = "{'Type': 'Server', 'About': 'StartGame', 'Message': ''}" ;
                    mClient.sendMessage(msg);
                    startGameBtn.setVisibility(View.GONE);
                    balanceTxt.setText(String.valueOf(balance));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        joinAsClientBtn = findViewById(R.id.joinAsClient);
        joinAsClientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDiscoverBtn.setVisibility(View.VISIBLE);
                joinAsClientBtn.setVisibility(View.GONE);
                mRegisterBtn.setVisibility(View.GONE);
                startGameBtn.setVisibility(View.VISIBLE);
                mClient = new Client(context, mDiscoveryListener, activity);
                mClient.discoverServices();
            }
        });

        final String checkNextMsg = "{'Type': 'NextRound', 'About': 'NextRound', 'Message': ''}";


        toEven = findViewById(R.id.toEven);
        coinsInRound = findViewById(R.id.coinsInRound);
        evenBtn = findViewById(R.id.even);
        evenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    msg = "{'Type': 'Multi', 'About': 'Even', 'Message': '" + toEven.getText() + "'}";
                    mClient.evenCoins();
                    mClient.sendMessage(msg);
                    mClient.sendMessage(checkNextMsg);
                    mClient.setButtons(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        waitBtn = findViewById(R.id.wait);
        waitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    msg = "{'Type': 'Multi', 'About': 'Wait', 'Message': ''}";
                    mClient.sendMessage(msg);
                    mClient.sendMessage(checkNextMsg);
                    mClient.setButtons(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        raiseBtn = findViewById(R.id.raise);
        raiseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        passBtn = findViewById(R.id.pass);
        passBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Client.DiscoveryListener mDiscoveryListener = new Client.DiscoveryListener() {
        @Override
        public void serviceDiscovered(String host, int port) {
            //This callback is on a worker thread...
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDiscoverBtn.setVisibility(View.GONE);
                    showToast("Connected");
                    try {
                        msg = "{'Type': 'Solo', 'About': 'Info', 'Message': 'Connected'}";
                        mClient.sendMessage(msg);
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
        mClient.shutdown();
    }
}