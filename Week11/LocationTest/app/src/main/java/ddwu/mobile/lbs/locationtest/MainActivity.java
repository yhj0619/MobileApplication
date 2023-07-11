package ddwu.mobile.lbs.locationtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    final String TAG = "MainActivity";

    final int REQ_PERMISSION_CODE = 100;

    TextView tvText;

    FusedLocationProviderClient flpClient;
    Location mLastLocation;

//    GoogleMap 객체 추가
    private GoogleMap mGoogleMap;
    //마커
    private Marker centerMarker;
    //선 옵션
    PolylineOptions pOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvText = findViewById(R.id.tvText);

        flpClient = LocationServices.getFusedLocationProviderClient(this);

//        MapFragment 추가 및 지도 로딩 실행
        SupportMapFragment mapFragment
                = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallback );
    }


//    OnMapReadyCallback 선언
    //준비하는 작업
   OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            mGoogleMap = googleMap;

            //지도 다루는 작업
            LatLng latLng = new LatLng(37.606320, 127.041808);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));

            MarkerOptions options = new MarkerOptions();
            options.position(latLng)
                    .title("현재위치")
                    .snippet("이동중")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

            centerMarker = mGoogleMap.addMarker(options);
            centerMarker.showInfoWindow();

            pOptions = new PolylineOptions();
            pOptions.color(Color.RED)
                    .width(5);



            mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(@NonNull LatLng latLng) {
                    String loc = String.format("위도:%f, 경도:%f", latLng.latitude, latLng.longitude);
                    Toast.makeText(MainActivity.this, loc, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };




    //위치 바뀔때 새로운 위치 얻는 부분(위치 정보 수신하는 부분)
    LocationCallback mLocCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            for (Location loc : locationResult.getLocations()) {
                double lat = loc.getLatitude();
                double lng = loc.getLongitude();
                setTvText(String.format("(%.6f, %.6f)", lat, lng));
                mLastLocation = loc;

//                현재 수신 위치로 GoogleMap 위치 설정
                LatLng latlng = new LatLng(lat, lng);
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));

                centerMarker.setPosition(latlng);

                //위치 정보 수실 할 때, 위치정보 추가하고 그리면 돼!!!!!!
                pOptions.add(latlng);
                mGoogleMap.addPolyline(pOptions);
            }
        }
    };


    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_permission:
                checkPermission();
                break;
            case R.id.btn_get_last:
                getLastLocation();
                break;
            case R.id.btn_geocoding:
                executeGeocoding(mLastLocation);
                break;
            case R.id.btn_start:

                flpClient.requestLocationUpdates(
                        getLocationRequest(),
                        mLocCallback,
                        Looper.getMainLooper()
                );

                break;
            case R.id.btn_stop:
                flpClient.removeLocationUpdates(mLocCallback);
                break;
            case R.id.btn_poi:
                String url = "fake url";
                new NetworkAsyncTask().execute(url);
                break;

        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        flpClient.removeLocationUpdates(mLocCallback);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERMISSION_CODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "위치권한 획득 완료", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "위치권한 미획득", Toast.LENGTH_SHORT).show();
                }
        }
    }


    private void checkPermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            // 권한이 있을 경우 수행할 동작
            Toast.makeText(this,"Permissions Granted", Toast.LENGTH_SHORT).show();
        } else {
            // 권한 요청
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, REQ_PERMISSION_CODE);
        }
    }


    private void getLastLocation() {

        flpClient.getLastLocation().addOnSuccessListener(
                new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            setTvText( String.format("최종위치: (%.6f, %.6f)", latitude, longitude) );
                            mLastLocation = location;
                        } else {
                            Toast.makeText(MainActivity.this, "No location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        flpClient.getLastLocation().addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Unknown");
                    }
                }
        );

    }


    private void executeGeocoding(Location location) {
        if (Geocoder.isPresent()) {
            Toast.makeText(this, "Run Geocoder", Toast.LENGTH_SHORT).show();
            if (location != null)  new GeoTask().execute(location);
        } else {
            Toast.makeText(this, "No Geocoder", Toast.LENGTH_SHORT).show();
        }
    }


    class GeoTask extends AsyncTask<Location, Void, List<Address>> {
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        @Override
        protected List<Address> doInBackground(Location... locations) {
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(locations[0].getLatitude(),
                        locations[0].getLongitude(), 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            Address address = addresses.get(0);
            Toast.makeText(MainActivity.this, address.getAddressLine(0), Toast.LENGTH_SHORT ).show();
        }
    }



//    실제 앱을 구현할 때는 네트워크 AsyncTask로 구현할 것
    class NetworkAsyncTask extends AsyncTask<String, Integer, String> {

        public final static String TAG = "NetworkAsyncTask";
        private ProgressDialog apiProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            apiProgressDialog = ProgressDialog.show(MainActivity.this, "Wait", "Downloading...");
        }

        @Override
        protected String doInBackground(String... strings) {

//            NetworkAsyncTask 는 네트워크 작업을 실제 실행하지는 않으며 잠시 시간 대기만 수행
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Open API search is completed";
        }

        @Override
        protected void onPostExecute(String result) {


//            작업 수행 후 가상 parsing 수행
            ArrayList<POI> poiList = parser.parse(result);
            apiProgressDialog.dismiss();

//            poiList 의 POI 로 마커 추가

            //poi list에서 객체 꺼내서 객체 확인하고 역지오코딩하고 마커로 찍는 작업 하면됨



       }
    }





    private void setTvText(String text) {
        String before = tvText.getText() + System.getProperty("line.separator");
        tvText.setText(before + text);
    }

}