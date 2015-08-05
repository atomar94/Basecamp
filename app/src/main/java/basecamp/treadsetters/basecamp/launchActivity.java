package basecamp.treadsetters.basecamp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.gimbal.android.Communication;
import com.gimbal.android.CommunicationListener;
import com.gimbal.android.GimbalDebugger;
import com.gimbal.android.Place;
import com.gimbal.android.CommunicationManager;
import com.gimbal.android.Gimbal;
import com.gimbal.android.Push;
import com.gimbal.android.Visit;
import com.gimbal.android.BeaconManager;
import com.gimbal.android.BeaconEventListener;
import com.gimbal.android.BeaconSighting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class launchActivity extends Activity {

    String GIMBAL_API_KEY = "d42fc552-3d2c-48f7-b247-b74924a98a13";
    String GCM_SERVER_API_KEY = "AIzaSyBxX-uxp032vR2vtAvkk7p0P_9TLM-Ez80";
    String GOOGLE_API_PROJECT_NUMBER = "299726784845";
    private CommunicationListener commListen;
    private BeaconEventListener beaconEventListener;
    private BluetoothAdapter bta;
    final int REQUEST_ENABLE_BT = 10; //random number needed for bt enable callback verification
    ArrayList<String> discoveredItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        bta = BluetoothAdapter.getDefaultAdapter();
        if(bta == null) {
            Log.e("Basecamp Log", "No Bluetooth adapter found on this device");
        }
        else if(!bta.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        displayPairedDevices();
        discoveredItems = new ArrayList<String>(); //array holding all discovered items' names
        discoveredItems.add("Discovered item test");
        //bta.startDiscovery();
    }

    private final BroadcastReceiver mReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.v("Basecamp Log", "Discovered paired device");
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                discoveredItems.add(device.getName());
                ListView discoveredLV = (ListView) findViewById(R.id.discoveredLV);
                ListAdapter adapter = new ArrayAdapter(getApplication(), android.R.layout.simple_list_item_1, discoveredItems);
                discoveredLV.setAdapter(adapter);
            }
        }
    };

    void displayPairedDevices() {
        Set<BluetoothDevice> btDevices = bta.getBondedDevices();
        /*
        if(btDevices.size() > 0) {
            Log.v("Basecamp log", "found " + btDevices.size() + " paired devices");
            ArrayList<String> Items = new ArrayList<String>();

            //populate list that will be displayed
            for(BluetoothDevice device : btDevices) {
                Items.add(device.getName());
            }
            Items.add("TEST_INDEX_0");
            Items.add("TEST_INDEX_1");
            Items.add("TEST_INDEX_2");
            //our launcher list view
            ListView pairedLV = (ListView) findViewById(R.id.pairedLV);
            ListAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, Items);
            pairedLV.setAdapter(adapter);
        }
        */
        for(BluetoothDevice device : btDevices) {
            if(device.getName().equals("otownsend92")) {
                Toast.makeText(getApplicationContext(), "Found bike, opening garage door!",Toast.LENGTH_LONG);
            }
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ENABLE_BT) {
            if(resultCode == RESULT_OK) {
                Log.v("Basecamp Log", "bluetooth enabled on device cb");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_launch, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
