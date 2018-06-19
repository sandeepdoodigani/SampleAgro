package com.example.a1a.sujhav.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.a1a.sujhav.ConnectionRecevier;
import com.example.a1a.sujhav.DataService;
import com.example.a1a.sujhav.DbHelp;
import com.example.a1a.sujhav.Manifest;
import com.example.a1a.sujhav.R;
import com.example.a1a.sujhav.Sujhav;
import com.example.a1a.sujhav.adapter.SampleList;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * Created by Sandeep Doodigani on 4-12-2017.
 */

public class SampleSubmit extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ConnectionRecevier.ConnectivityReceiverListener
        {

private BluetoothAdapter mBtAdapter;
    Button mConnect,add,submit,toggleButton;
    Handler bluetoothIn;
    String uid;
    ProgressBar progressBar;
    ImageView status;
    EditText phManual,ecManual,mosManual;
    final int handlerState = 0;
    AlertDialog a;
    ListView deviceListview,recordlist, available;
    TextView deviceName,sample,lat,longitude,ph,ec,moisture,note;
    double latitude,longitude1;
    private ConnectedThread mConnectedThread;
    String phValue, ecValue,data,AccountId,mosValue;
    int state;
    Boolean check;
    private StringBuilder recDataString = new StringBuilder();
    GoogleApiClient mGoogleApiClient;
    private ArrayAdapter<String> mdevicelist;
            private ArrayAdapter<String> deviceAvalibleList;
    BluetoothDevice mBluetoothDevice;
    private BluetoothSocket btSocket = null;
            LocationRequest mLocationRequest;
            ArrayList samp,lat1,lon,ph1,ec1,mos;
            DbHelp db;

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    @SuppressLint("HandlerLeak")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        samp=new ArrayList();
        lat1=new ArrayList();
        lon=new ArrayList();
        ph1=new ArrayList();
        ec1=new ArrayList();
        mos=new ArrayList();
        db=new DbHelp(getActivity());
        db.deleteAll();
        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
        AccountId = sharedpreferences.getString("AccountId", null);
        Log.i("Account Id",AccountId);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        View v=inflater.inflate(R.layout.fragment_sample_submit, container, false);
        statusCheck();
        final ToggleButton toggle = (ToggleButton) v.findViewById(R.id.fromDevice);
        toggle.setTextOff("From Device");
        recordlist=(ListView)v.findViewById(R.id.recordList);
        sample=(TextView)v.findViewById(R.id.sample);
        lat=(TextView)v.findViewById(R.id.latitude);
        longitude=(TextView)v.findViewById(R.id.longitude);
        ph=(TextView)v.findViewById(R.id.ph);
        note=(TextView)v.findViewById(R.id.note);
        ec=(TextView)v.findViewById(R.id.ec);
        add=(Button)v.findViewById(R.id.add);
        submit=(Button)v.findViewById(R.id.submit);
        status=(ImageView)v.findViewById(R.id.status);
        phManual=(EditText)v.findViewById(R.id.ph_Manual);
        ecManual=(EditText)v.findViewById(R.id.ec_Manual);
        moisture=(TextView)v.findViewById(R.id.moisture);
        mosManual=(EditText) v.findViewById(R.id.mo_Manual);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    toggle.setTextOn("Enter Manually");

                    note.setText("Click \"ENTER MANUALLY\" to manually enter the data.");
                    mConnect.setVisibility(View.VISIBLE);
                    status.setVisibility(View.VISIBLE);
                    mosManual.setVisibility(View.INVISIBLE);
                    ecManual.setVisibility(View.INVISIBLE);
                    phManual.setVisibility(View.INVISIBLE);


                }
                else {
                    toggle.setTextOff("From Device");
                    toggle.setText("From Device");
                    note.setText("Click \"FROM DEVICE\" to get data from the Sujhav device.");
                    mConnect.setVisibility(View.INVISIBLE);
                    status.setVisibility(View.INVISIBLE);
                    mosManual.setVisibility(View.VISIBLE);
                    ecManual.setVisibility(View.VISIBLE);
                    phManual.setVisibility(View.VISIBLE);
                }
            }
        });



        checkBTState();
        getUid();

        createLocationRequest();
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        mBtAdapter.startDiscovery();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(mReceiver, filter);
        IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        getActivity().registerReceiver(mPairReceiver, intent);
        mdevicelist = new ArrayAdapter<String>(getActivity(),R.layout.device_name);
        deviceAvalibleList = new ArrayAdapter<String>(getActivity(),R.layout.device_name);
        mConnect=(Button)v.findViewById(R.id.connect);
        mConnect.setVisibility(View.VISIBLE);
        status.setVisibility(View.VISIBLE);
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        // Add previosuly paired devices to the array
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mdevicelist.add(device.getName() + "\n" + device.getAddress());
            }
        } else
            {
            String noDevices = "No Devices Found";
            mdevicelist.add(noDevices);
        }

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {                                        //if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);                                    //keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string

                        int dataLength = dataInPrint.length();
                        if (recDataString.charAt(0) == '#')    //get length of data received
                        {
                            StringTokenizer tokens = new StringTokenizer(dataInPrint, ",");
                            phValue = tokens.nextToken();// this will contain "Fruit"
                            ecValue = tokens.nextToken();
                            mosValue=tokens.nextToken();
                            int i = phValue.length();
                            String ss = phValue.substring(1, i);
                            phValue = ss;
                            Log.i("Value",phValue+","+ecValue);//update the textviews with sensor values
                            ph.setText("pH value: "+phValue);
                            ec.setText("EC value:"+ ecValue);
                            moisture.setText("Moisture:"+mosValue);


                        }


                        recDataString.delete(0, recDataString.length());                    //clear all string data
                        // strIncom =" ";
                        dataInPrint = " ";
                    }
                }
            }
        };
        mConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(state==0) {
                    AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                    LayoutInflater l = getActivity().getLayoutInflater();
                    View v = l.inflate(R.layout.device_list, null);
                    deviceListview = (ListView) v.findViewById(R.id.deviceList);
                    deviceListview.setAdapter(mdevicelist);
                    available = (ListView) v.findViewById(R.id.deviceAvalible);
                    available.setAdapter(deviceAvalibleList);
                    progressBar=(ProgressBar)v.findViewById(R.id.progress);
                    progressBar.setProgress(100);

                    available.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String info = ((TextView) view).getText().toString();
                            String address = info.substring(info.length() - 17);
                            mBluetoothDevice = mBtAdapter.getRemoteDevice(address);
                            if (mBluetoothDevice.getBondState() == 10) {
                                pairDevice(mBluetoothDevice);
                            } else if (mBluetoothDevice.getBondState() == 12) {
                                connectDevice(mBluetoothDevice.getAddress());
                            }
                        }
                    });
                    deviceListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            String info = ((TextView) view).getText().toString();
                            String address = info.substring(info.length() - 17);
                            mBluetoothDevice = mBtAdapter.getRemoteDevice(address);
                            Log.i("Address", address);
                            connectDevice(address);
                        }
                    });
                    b.setView(v);
                    a = b.create();
                    a.show();
                }
                else if(state==1){
                    try {
                        btSocket.close();
                        state=0;
                        mConnect.setText("Connect");
                        deviceName.setText("Disconnected sucessfully");
                        status.setImageDrawable(getResources().getDrawable(R.drawable.red));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(phManual.getText().toString()) && !TextUtils.isEmpty(ecManual.getText().toString()) &&!TextUtils.isEmpty(mosManual.getText().toString())){

                    phValue=phManual.getText().toString();
                    ecValue=ecManual.getText().toString();
                    mosValue=mosManual.getText().toString();

                }
                DecimalFormat decimalFormat=new DecimalFormat("#.#####");
                samp.add(uid);
                lat1.add(decimalFormat.format(latitude));
                lon.add(decimalFormat.format(longitude1));
                ph1.add(phValue);
                ec1.add(ecValue);
                mos.add(mosValue);
                recordlist.setAdapter(new SampleList(getActivity(), samp, lat1,lon,ph1,ec1,mos));
                getUid();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if (checkConnection()) {
                        JSONArray jsonArray = new JSONArray();
                        for (int i = 0; i < samp.size(); i++) {
                            JSONObject j = new JSONObject();
                            try {

                                j.put("FarmerID", AccountId);
                                j.put("pH",ph1.get(i));
                                j.put("Ec", ec1.get(i));
                                j.put("SampleNo", samp.get(i));
                                j.put("Longitude", lon.get(i));
                                j.put("Latitutude", lat1.get(i));
                                j.put("Moisture",mos.get(i));
                                jsonArray.put(j);

                            } catch (JSONException e ) {
                                e.printStackTrace();
                            }
                            catch (NullPointerException e ) {
                                e.printStackTrace();
                            }

                        }
                        Log.i("SampleSubmitJson", jsonArray.toString());
                        try {
                            data = URLEncoder.encode("json_data", "UTF-8")
                                    + "=" + URLEncoder.encode(jsonArray.toString(), "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        if (!TextUtils.isEmpty(data)) {
                            sendData sendData = new sendData();
                            sendData.execute();
                        }

                    }

                    else {
                        Toast.makeText(getActivity(), "No internet, Saving Data to local db", Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < samp.size(); i++) {
                            db.addData(AccountId,samp.get(i).toString(),lat1.get(i).toString(),lon.get(i).toString(),"11","12");
                        }

                        getActivity().startService(new Intent(getActivity(), DataService.class));
                    }


            }
        });
        return v;


    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("SoilManagement");
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //discovery starts, we can show progress dialog or perform other tasks
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //discovery finishes, dismis progress dialog
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                try {
                    //bluetooth device found
                    BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.i("Found", device.getName()+ "\n" + device.getAddress());
                    deviceAvalibleList.add(device.getName()+ "\n" + device.getAddress());

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    };
            private void pairDevice(BluetoothDevice device) {
                try {
                    Method method = device.getClass().getMethod("createBond", (Class[]) null);
                    method.invoke(device, (Object[]) null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();

                    if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                        final int state        = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                        final int prevState    = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                        if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                          Log.i("Paired","paried");
                          a.dismiss();

                          connectDevice(mBluetoothDevice.getAddress());
                        } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
                            Log.i("Not","not");

                        }

                    }
                }
            };
    private void checkBTState() {
        // Check device has Bluetooth and that it is turned on
        mBtAdapter= BluetoothAdapter.getDefaultAdapter(); // CHECK THIS OUT THAT IT WORKS!!!
        if(mBtAdapter==null) {
            Toast.makeText(getActivity(), "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            if (mBtAdapter.isEnabled()) {
                Log.d("Ok", "...Bluetooth ON...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);

            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        getActivity().unregisterReceiver(mReceiver);
        getActivity().unregisterReceiver(mPairReceiver);
    }
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }
    void connectDevice(String address){
       Connect connect=new Connect();
       connect.execute(address);
    }

            @Override
            public void onConnected(@Nullable Bundle bundle) {
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest,this
                );
            }

            @Override
            public void onConnectionSuspended(int i) {

            }

            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

            }

            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude1 = location.getLongitude();

                lat.setText("Latitude: " + latitude);
                longitude.setText("Longitude: "+ longitude1);

            }

            @Override
            public void onNetworkConnectionChanged(boolean isConnected) {

            }

            private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);        	//read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }


        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
//                Toast.makeText(getActivity(), "Connection Failure", Toast.LENGTH_LONG).show();
                //finish();

            }
        }
    }
    public class Connect extends AsyncTask<String,Void,String>{
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("Connecting...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... s) {
            BluetoothDevice device=mBtAdapter.getRemoteDevice(s[0]);

            try {
                btSocket = createBluetoothSocket(device);

            } catch (IOException e) {
                Log.i("Tag","Socket creation failed");
            }
            try {
                btSocket.connect();

            } catch (IOException e) {
                try {
                    btSocket.close();
                } catch (IOException e2) {
                    //insert code to deal with this
                }
            }
            mConnectedThread = new ConnectedThread(btSocket);
            mConnectedThread.start();
            mConnectedThread.write("x");
            return s[0];
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            BluetoothDevice device=mBtAdapter.getRemoteDevice(s);
            if(btSocket.isConnected()){
            a.dismiss();
            mConnect.setText("Disconnect");
            status.setImageDrawable(getResources().getDrawable(R.drawable.green));
            state=1;
            deviceName.setText("Connected to "+device.getName());
            progressDialog.dismiss();
            }
            else {
                a.dismiss();
                status.setImageDrawable(getResources().getDrawable(R.drawable.red));
                mConnect.setText("Connect");
                deviceName.setText("Connection failed");
                progressDialog.dismiss();
            }
        }
    }
            protected void createLocationRequest() {
                mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(1000*10);
                mLocationRequest.setFastestInterval(1000*5);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            }
            public void onStart() {
                mGoogleApiClient.connect();
                super.onStart();
            }
            public void onStop() {
                mGoogleApiClient.disconnect();
                super.onStop();
            }
            public void getUid(){
                Long tsLong = System.currentTimeMillis()/1000;
                 uid = tsLong.toString();
                sample.setText("Sample Id:" + uid);
            }
            public void statusCheck() {
                final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps();

                }
            }
            private void buildAlertMessageNoGps() {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                dialog.cancel();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            }
            class sendData extends AsyncTask<Void, Void, String> {
            ProgressDialog pDialog;

                protected void onPreExecute() {
                    pDialog = new ProgressDialog(getActivity());
                    pDialog.setMessage("Please wait...Sending...");
                    pDialog.setCancelable(true);
                    pDialog.setProgress(0);
                    pDialog.setMax(100);
                    pDialog.show();
                }

                protected String doInBackground(Void... urls) {
                    try {
                        URL url = new URL("http://www.sujhav.in/API/soil_management");

                        URLConnection conn = url.openConnection();
                        conn.setDoOutput(true);
                        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                        wr.write(data);
                        wr.flush();

                        try {
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            StringBuilder stringBuilder = new StringBuilder();
                            String line;
                            System.out.println(" bufferedreader response :" + bufferedReader);

                            while ((line = bufferedReader.readLine()) != null) {
                                stringBuilder.append(line);


                            }
                            bufferedReader.close();
                            Log.e("res", stringBuilder.toString());
                            return stringBuilder.toString();
                        } finally {

                        }
                    } catch (Exception e) {
                        Log.e("ERROR", e.getMessage(), e);
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);

                    pDialog.dismiss();
                    Toast.makeText(getActivity(),s, Toast.LENGTH_SHORT).show();

                }
            }


            private boolean checkConnection() {
                boolean isConnected = ConnectionRecevier.isConnected();

                return isConnected;
            }
            @Override
            public void onResume() {
                super.onResume();
                Sujhav.getInstance().setConnectivityListener(this);
            }

        }