package basecamp.treadsetters.basecamp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.gimbal.android.BeaconEventListener;
import com.gimbal.android.CommunicationListener;

import java.util.ArrayList;
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
    ListView listview;

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

        Context c = getApplicationContext();


        listview = (ListView) findViewById(R.id.appliances_listview);
        MyRowAdapter rowAdapter = new MyRowAdapter(this,
                new String[] { "Garage Door", "Coffee","Air Conditioning", "Tiger Cage" },
                new String[] { "Open", "On", "On", "Open" },
                new String[] { "Closed", "Off", "Off", "Closed" });
        listview.setAdapter(rowAdapter);
        rowAdapter.notifyDataSetChanged();


        // Check for bike
        boolean found = displayPairedDevices(c);
        //execute IoT commands.
        if(found) {
            ToggleButton button0 = (ToggleButton) rowAdapter.getItem(0);
            ToggleButton button1 = (ToggleButton) rowAdapter.getItem(1);
            ToggleButton button2 = (ToggleButton) rowAdapter.getItem(2);
            ToggleButton button3 = (ToggleButton) rowAdapter.getItem(3);
        }

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
            }
        }
    };


    public boolean displayPairedDevices(Context c) {
        Set<BluetoothDevice> btDevices = bta.getBondedDevices();

        Log.d("MYTAG", "Searching for devices");
        for (BluetoothDevice device : btDevices) {
            Log.d("MYTAG", "Found: " + device.getName());
            if (device.getName().equals("otownsend92")) {
                Log.d("MYTAG", "Found Bike!");
                //Toast.makeText(c, "Found bike, opening garage door!",Toast.LENGTH_LONG);
                return true;
            }
        }
        return false;
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

/*
 Row adapter class.
 */
class MyRowAdapter extends BaseAdapter {

    Context context;
    String[] data;
    String[] buttonOnData;
    String[] buttonOffData;
    private static LayoutInflater inflater = null;

    public MyRowAdapter(Context context, String[] data, String[] buttonOnData, String[] buttonOffData) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        this.buttonOffData = buttonOffData;
        this.buttonOnData = buttonOnData;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.garage_door_row, null);
        TextView row_text = (TextView) vi.findViewById(R.id.row_text);
        row_text.setText(data[position]);
        ToggleButton toggle = (ToggleButton) vi.findViewById(R.id.row_button);
        toggle.setTextOn(buttonOnData[position]);
        toggle.setTextOff(buttonOffData[position]);
        return vi;
    }
}