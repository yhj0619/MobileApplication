package ddwu.mobile.lbs.locationtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.Manifest;
import android.content.pm.PackageManager;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    final String TAG = "MainActivity";

    final int REQ_PERMISSION_CODE = 100;

    FusedLocationProviderClient flpClient;
    Location mLastLocation;
    TextView tvText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvText = findViewById(R.id.tvText);

//         FusedLocationProviderClient 객체 생성
        flpClient = LocationServices.getFusedLocationProviderClient(this);
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
                executeGeocoding(mLastLocation);    //우리가 만든 메소드
                break;
            case R.id.btn_start:
                // 위치정보 수신 시작
               flpClient.requestLocationUpdates(getLocationRequest(), mLocCallback, Looper.getMainLooper());
                break;
            case R.id.btn_stop:
                // 위치정보 수신 종료
                flpClient.removeLocationUpdates(mLocCallback);
                break;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        // 위치정보 수신 종료
        flpClient.removeLocationUpdates(mLocCallback);

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


    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }


    LocationCallback mLocCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            // 위치정보 수신 시 동작 실행
            for (Location loc : locationResult.getLocations()){
                double lat = loc.getLatitude();
                double lng = loc.getLongitude();
                setTvText(String.format("(%.6f, %.6f)", lat, lng));
                mLastLocation = loc;
                executeGeocoding(mLastLocation);
            }
        }
    };


    private void getLastLocation() {
        flpClient.getLastLocation().addOnSuccessListener(
                new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // 최종 위치정보 수신 시 수행할 동작 지정
                        if(location != null){
                            double latitue = location.getLatitude();
                            double longitude = location.getLongitude();
                            setTvText(String.format("최종위치: (%.6, %.6)", latitue, longitude));
                            mLastLocation = location;
                        } else{
                            Toast.makeText(MainActivity.this, "No locaton", Toast.LENGTH_SHORT).show();
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
            if (location != null)  {
                new GeoTask().execute(location); //있으면 실행
            }
        } else {
            Toast.makeText(this, "No Geocoder", Toast.LENGTH_SHORT).show();
        }
    }


    class GeoTask extends AsyncTask<Location, Void, List<Address>> {
//        Geocoder 객체 생성
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

        @Override
        protected List<Address> doInBackground(Location... locations) {
            List<Address> addresses = null;
            try {
//             geocoding 수행
                addresses = geocoder.getFromLocation(locations[0].getLatitude(), locations[0].getLongitude(), 5);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            Address address = addresses.get(0);
            Toast.makeText(MainActivity.this, address.getAddressLine(0), Toast.LENGTH_SHORT ).show();
            setTvText( address.getAddressLine(0) ); //상세 주소
                            //getAddressLine(1); 일반 주소
        }
    }


    private void setTvText(String text) {
        String before = tvText.getText() + System.getProperty("line.separator");
        tvText.setText(before + text);
    }

}