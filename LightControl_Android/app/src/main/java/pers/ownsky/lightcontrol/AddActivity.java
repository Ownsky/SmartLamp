package pers.ownsky.lightcontrol;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.net.InetAddress;

public class AddActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    EditText titleText;
    EditText ipText;
    EditText portText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        setTitle("Add Device");
        android.support.v7.app.ActionBar actionBar = this.getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        titleText = (EditText) findViewById(R.id.titleEditText);
        ipText = (EditText) findViewById(R.id.ipEditText);
        portText = (EditText) findViewById(R.id.portEditText);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

    public void onOKClick(View button) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("title", titleText.getText().toString());
        bundle.putString("ip", ipText.getText().toString());
        bundle.putString("port", portText.getText().toString());
        try {
            checkDataInfo(bundle);
        } catch (Exception e) {
//            e.printStackTrace();
            return;
        }
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    protected void checkDataInfo(Bundle info) throws Exception {
        String title = info.getString("title");

        String ip = info.getString("ip");
        String port = info.getString("port");
        try {
            InetAddress IP = InetAddress.getByName(ip);
        }
        catch (Exception e) {
            Toast.makeText(this, "Invalid IP", Toast.LENGTH_SHORT).show();
            throw e;
        }
        try {
            int Port = Integer.parseInt(port);
            if (Port < 0 || Port > 65535) throw new Exception();
        }
        catch (Exception e) {
            Toast.makeText(this, "Invalid Port", Toast.LENGTH_SHORT).show();
            throw e;
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Add Page") // TODO: Define a title for the content shown.
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
}
