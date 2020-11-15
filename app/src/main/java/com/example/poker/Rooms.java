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
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Rooms extends Activity {

    private NSDListen mNSDListener;
    private NSDDiscover mNSDDiscover;

    private Button mRegisterBtn;
    private Button mDiscoverBtn;
    private Button mSayHelloBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nsd_activity);

        mNSDListener = new NSDListen(this, this);
        mNSDDiscover = new NSDDiscover(this, mDiscoveryListener, this);

        mRegisterBtn = (Button)findViewById(R.id.register);
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNSDListener.registerDevice();
                mRegisterBtn.setText(getString(R.string.hosting));
            }
        });

        mDiscoverBtn = (Button)findViewById(R.id.discover);
        mDiscoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNSDDiscover.discoverServices();
            }
        });

        mSayHelloBtn = (Button)findViewById(R.id.sayHello);
        mSayHelloBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNSDDiscover.sayHello();
            }
        });

        //Show selection alert dialog...
        new AlertDialog.Builder(this)
                .setMessage("Select if you want to host or join a game")
                .setPositiveButton(getString(R.string.host), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDiscoverBtn.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.join), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mRegisterBtn.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private NSDDiscover.DiscoveryListener mDiscoveryListener = new NSDDiscover.DiscoveryListener() {
        @Override
        public void serviceDiscovered(String host, int port) {
            //This callback is on a worker thread...
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.sayHello).setVisibility(View.VISIBLE);
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