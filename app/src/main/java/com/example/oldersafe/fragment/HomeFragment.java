package com.example.oldersafe.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.provider.Settings;
import android.telephony.SmsManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oldersafe.R;
import com.example.oldersafe.config.Constants;
import com.example.oldersafe.config.SharedPreferencesUtils;
import com.example.oldersafe.config.ToLocation;
import com.example.oldersafe.database.DBDao;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Map;

public class HomeFragment extends Fragment implements SensorEventListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
    }


    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        isStart();
        initMap();
        initSensor();
    }

    private void isStart() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGpsEnabled && !isNetworkEnabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    TextView state;
    TextView name;
    TextView phone;
    TextView position;
    TextView lat;
    TextView longg;
    Button help;
    Button cancal;
    double latitude;
    double longitude;
    MyListener myListener;
    private FusedLocationProviderClient fusedLocationClient;
    private void  initView() {

        state = getActivity().findViewById(R.id.state);
        name = getActivity().findViewById(R.id.name);
        phone = getActivity().findViewById(R.id.phone);
        position = getActivity().findViewById(R.id.position);
        help = getActivity().findViewById(R.id.help);
        cancal = getActivity().findViewById(R.id.cancal);
        lat = getActivity().findViewById(R.id.lat);
        longg = getActivity().findViewById(R.id.longg);
        dialog = getActivity().findViewById(R.id.dialog);
        time = getActivity().findViewById(R.id.time);
        final String state_ = (String) SharedPreferencesUtils.getParam(getContext(), Constants.State, "00");
        String name_ = (String) SharedPreferencesUtils.getParam(getContext(), Constants.User_Name, "00");
        String phone_ = (String) SharedPreferencesUtils.getParam(getContext(), Constants.Phone, "00");

        if (state_.equals("1")) {
            state.setText("Normal");
            state.setTextColor(getResources().getColor(R.color.green));
        } else {
            state.setText("UnNormal");
            state.setTextColor(getResources().getColor(R.color.red));
            help.setVisibility(View.VISIBLE);
            cancal.setVisibility(View.VISIBLE);
        }

        name.setText(name_);
        phone.setText(phone_);

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(process) {
                    myCountDownTimer.cancel();
                    myCountDownTimer = null;
                    state.setText("UnNormal");
                    state.setTextColor(getResources().getColor(R.color.red));
                    time.setVisibility(View.GONE);
                    dialog.setVisibility(View.GONE);
                    //adding the message of successful sending out help
                    sendSms();
                    Toast.makeText(getContext(),"has send message for help! please wait for Rescue Worker to come",Toast.LENGTH_SHORT).show();
                    process = false;
                }
            }
        });
        cancal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(process) {
                    myCountDownTimer.cancel();
                    myCountDownTimer = null;
                    process = false;
                    state.setText("Normal");
                    state.setTextColor(getResources().getColor(R.color.green));
                    time.setVisibility(View.GONE);
                    dialog.setVisibility(View.GONE);
                }else{
                    DBDao dao=new DBDao(getContext());
                    dao.open();
                    String name = (String) SharedPreferencesUtils.getParam(getContext(), Constants.User_Name, "00");
                    latitude = Double.parseDouble(lat.getText().toString());
                    longitude = Double.parseDouble(longg.getText().toString());
                    dao.updateAddress(name,position.getText().toString(),"1",latitude,longitude);
                    state.setText("Normal");
                    state.setTextColor(getResources().getColor(R.color.green));
                }
            }
        });
    }

    private GoogleApiClient mGoogleApiClient;
    private ToLocation toLocation;

    public void initMap() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        myListener = new MyListener(latitude,longitude,toLocation);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //Set the update frequency of latitude and longitude request
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)
                .setFastestInterval(5000)
                .setMaxWaitTime(5000);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        toLocation = new ToLocation(getContext(), latitude, longitude);
                        String area = toLocation.getCountry() + toLocation.getAdminarea() + toLocation.getCity() + toLocation.getArea() + toLocation.getThrough();
                        position.setText(area);
                        lat.setText(String.valueOf(latitude));
                        longg.setText(String.valueOf(longitude));
                    }
                }
            }
        };
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class MyListener implements LocationListener{

        double latitude;
        double longitude;
        ToLocation toLocation;
        public MyListener(double latitude,double longitude,ToLocation toLocation) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public void onLocationChanged(@NonNull Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            toLocation = new ToLocation(getContext(),latitude,longitude);
            String area = toLocation.getCountry()+toLocation.getAdminarea()+toLocation.getCity()+toLocation.getArea()+toLocation.getThrough();
            position.setText(area);
            lat.setText(String.valueOf(latitude));
            longg.setText(String.valueOf(longitude));
        }
    }

    SensorManager sensorManager;
    Sensor accelerometer;
    //threshold of accelerometer detecting fall
    float threshold = 80.0f;
    public void initSensor(){
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];


            double accelerationSquareRoot = Math.sqrt(x * x + y * y + z * z);


            if (accelerationSquareRoot > threshold) {
                dealHelp();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    MyCountDownTimer myCountDownTimer;
    boolean process = false;
    ImageView dialog;
    TextView time;
    public void dealHelp(){
        if(myCountDownTimer !=null) return;
        myCountDownTimer = new MyCountDownTimer(10000, 1000);
        myCountDownTimer.start();
        state.setTextColor(getResources().getColor(R.color.red));
        state.setText("UnNormal");
        time.setVisibility(View.VISIBLE);
        dialog.setVisibility(View.VISIBLE);
    }
    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            process = true;
            millisUntilFinished = millisUntilFinished/1000;
            time.setText(millisUntilFinished+"s");
        }

        @Override
        public void onFinish() {
            time.setVisibility(View.GONE);
            dialog.setVisibility(View.GONE);
            process = false;
            sendSms();
            Toast.makeText(getContext(),"has send message for help! please wait for Rescue Worker to come",Toast.LENGTH_SHORT).show();
        }
    }
    private void sendSms() {
        DBDao dao=new DBDao(getContext());
        dao.open();
        String name = (String) SharedPreferencesUtils.getParam(getContext(), Constants.User_Name, "00");
        latitude = Double.parseDouble(lat.getText().toString());
        longitude = Double.parseDouble(longg.getText().toString());
        dao.updateAddress(name,position.getText().toString(),"2",latitude,longitude);
        String User_Type = (String) SharedPreferencesUtils.getParam(getContext(), Constants.User_Type, "00");
        ArrayList<Map<String, Object>> contactData = dao.getContactData(name,User_Type,"2");
        dao.close();
        SmsManager smsManager = SmsManager.getDefault();
        String message = name + " fall down! "+" ask for your help, in " +
                "latitude: " + latitude + "\n" +
                "longitude: " +longitude + "\n" +
                position.getText().toString();
        if(contactData == null) return;
        for (int i=0;i<contactData.size();i++){
            Map<String,Object> map = contactData.get(i);
            String phoneNumber = (String) map.get("contact_phone");
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        }
    }

}


