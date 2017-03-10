package pers.ownsky.lightcontrol;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.lang.ref.WeakReference;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

public class ControlActivity extends AppCompatActivity {

    String m_title;
    InetAddress m_IP;
    int m_port;

    SeekBar bBar, tBar;
    ToggleButton uButton;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Control Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public static class SocketDealer {
        public static void sendUDPMsg(byte[] msg, InetAddress addr, int port) throws Exception {
            DatagramSocket udpSocket = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(msg, msg.length, addr, port);
            udpSocket.send(packet);
            udpSocket.close();
        }
        public static byte[] recvUDPMsg(int port) throws Exception {
            DatagramSocket udpSocket= new DatagramSocket(port);
            udpSocket.setSoTimeout(5000);
            byte[] recvBuf = new byte[100];
            DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
            udpSocket.receive(packet);
            udpSocket.close();
            return recvBuf;
        }
    }
//TODO: some handlers
//    private static class FromDeviceHandler extends Handler {
//        private final WeakReference<Activity> m_activity;
//
//        private FromDeviceHandler(Activity activity) {
//            m_activity = new WeakReference<>(activity);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            Activity activity = m_activity.get();
//            //TODO: add funcs.
//            if (activity == null) return;
//            int temp, bright;
//        }
//    }
//
//    private static class ToDeviceHandler extends Handler {
//        private final WeakReference<Activity> m_activity;
//
//        private ToDeviceHandler(Activity activity) {
//            m_activity = new WeakReference<>(activity);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            Activity activity = m_activity.get();
//            //TODO: add funcs.
//            if (activity == null) return;
//            int temp, bright;
//        }
//    }

    public class SendTask extends AsyncTask<ArrayList<Integer>, Void, Void> {

        @Override
        protected Void doInBackground(ArrayList<Integer>... params) {
            ArrayList<Integer> info = params[0];
            byte[] msg = new byte[5];
            if (info.get(0) == 0) {
                msg[0] = 1;
                msg[1] = (byte) (info.get(1) / 256);
                msg[2] = (byte) (info.get(1) - msg[1]);
                msg[3] = (byte) (info.get(2) / 256);
                msg[4] = (byte) (info.get(2) - msg[3]);
                try {
                    SocketDealer.sendUDPMsg(msg, m_IP, m_port);
                } catch (Exception e) {
                    //TODO:
                    e.printStackTrace();
                }
            }
            else {
                msg[0] = 2;
                try {
                    SocketDealer.sendUDPMsg(msg, m_IP, m_port);
                } catch (Exception e) {
                    //TODO:
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        setTitle("Control Panel");
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        TextView titleView = (TextView) findViewById(R.id.titleTextView);
        TextView infoView = (TextView) findViewById(R.id.infoTextView);
        Bundle infoBundle = getIntent().getExtras();
        try {
            setDataInfo(infoBundle);
        } catch (Exception e) {
            String msg = e.getMessage();
            switch (msg) {
                case "ip err":
                    setResult(2);
                    break;
                case "port err":
                    setResult(3);
                    break;
                default:
                    setResult(4);
                    break;
            }
            finish();
            return;
        }
        titleView.setText(m_title);
        infoView.setText(infoBundle.getString("ip") + ":" + m_port);
        bBar = (SeekBar) findViewById(R.id.brightnessBar);
        tBar = (SeekBar) findViewById(R.id.temperatureBar);
        uButton = (ToggleButton) findViewById(R.id.ultraButton);
        bBar.setMax(1023);
        tBar.setMax(1023);
        bBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ArrayList<Integer> info = new ArrayList<Integer>();
                info.add(uButton.isChecked()?1:0);info.add(progress);info.add(tBar.getProgress());
                new SendTask().execute(info);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        tBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ArrayList<Integer> info = new ArrayList<Integer>();
                info.add(uButton.isChecked()?1:0);info.add(bBar.getProgress());info.add(progress);
                new SendTask().execute(info);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        uButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ArrayList<Integer> info = new ArrayList<Integer>();
                info.add(isChecked?1:0);info.add(bBar.getProgress());info.add(tBar.getProgress());
                new SendTask().execute(info);
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    protected void setDataInfo(Bundle info) throws Exception {
        m_title = info.getString("title");
        String ip = info.getString("ip");
        String port = info.getString("port");
        try {
            m_IP = InetAddress.getByName(ip);
        } catch (Exception e) {
            throw new Exception("ip err");
        }
        try {
            m_port = Integer.parseInt(port);
            if (m_port < 0 || m_port > 65535) throw new Exception();
        } catch (Exception e) {
            throw new Exception("port err");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
