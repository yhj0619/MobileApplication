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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
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

    FakeParser parser;

    FusedLocationProviderClient flpClient;
    Location mLastLocation;

    private GoogleMap mGoogleMap;       // 지도 객체
    private Marker mCenterMarer;         // 중앙 표시 Marker
    private Polyline mPolyline;

    private Marker mPoiMarker;
    private List<Marker> markerList;    //마커 저장


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvText = findViewById(R.id.tvText);

        parser = new FakeParser();      // 모의 parser 생성

        flpClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment
                = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallback);
    }


    OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            mGoogleMap = googleMap;

//            지도 초기 위치 이동
            LatLng latLng = new LatLng(37.606320, 127.041808);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

//            지도 중심 마커 추가
            MarkerOptions centerMarkerOptions = new MarkerOptions()
                    .position(latLng)
                    .title("현재 위치")
                    .snippet("이동중")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

            mCenterMarer = mGoogleMap.addMarker(centerMarkerOptions);
            mCenterMarer.showInfoWindow();

//            지도 롱클릭 이벤트 처리
            mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(@NonNull LatLng latLng) {
                    Location location = new Location("GPS"); //위치정보 수신 받는 것을  GPS(네트워크)라고 적어주기
                    location.setLatitude(latLng.latitude);
                    location.setLongitude(latLng.longitude);

                    executeGeocoding(latLng);
                }
            });
        }
    };


    LocationCallback mLocCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            for (Location loc : locationResult.getLocations()) {
                double lat = loc.getLatitude();
                double lng = loc.getLongitude();
                setTvText(String.format("(%.6f, %.6f)", lat, lng));

//                지도 위치 이동
                mLastLocation = loc;
                LatLng currentLoc = new LatLng(lat, lng);
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 17));

//                지도 마커 위치 이동
                mCenterMarer.setPosition(currentLoc);

//                지도 선을 그리기 위한 지점(위도/경도) 추가
                List<LatLng> latLngs = mPolyline.getPoints();
                latLngs.add(currentLoc);
                mPolyline.setPoints(latLngs);
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
                LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                executeGeocoding(latLng);
                break;
            case R.id.btn_start:
                flpClient.requestLocationUpdates(
                        getLocationRequest(),
                        mLocCallback,
                        Looper.getMainLooper()
                );
//            지도 poly line 추가 -> 선그리기 준비
//            강의자료 내용과 별도의 내용으로 polyOption 을 멤버변수로 선언하는 것이 아닌
//            지도에 추가 후 반환하는 PolyLine 을 멤버변수로 선언하고 관리
                PolylineOptions polylineOptions = new PolylineOptions()
                        .color(Color.RED)
                        .width(5);
                mPolyline = mGoogleMap.addPolyline(polylineOptions);

                break;
            case R.id.btn_stop:
                flpClient.removeLocationUpdates(mLocCallback);

//                지도 polyline 제거
                mPolyline.remove();
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
            Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show();
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
                            setTvText(String.format("최종위치: (%.6f, %.6f)", latitude, longitude));
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


    private void executeGeocoding(LatLng latLng) {
        if (Geocoder.isPresent()) {
            Toast.makeText(this, "Run Geocoder", Toast.LENGTH_SHORT).show();
            if (latLng != null) new GeoTask().execute(latLng);
        } else {
            Toast.makeText(this, "No Geocoder", Toast.LENGTH_SHORT).show();
        }
    }

    class GeoTask extends AsyncTask<LatLng, Void, List<Address>> {
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

        @Override
        protected List<Address> doInBackground(LatLng... latLngs) {
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(latLngs[0].latitude,
                        latLngs[0].longitude, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            if (addresses != null) {
                Address address = addresses.get(0);
                Toast.makeText(MainActivity.this, address.getAddressLine(0), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setTvText(String text) {
        String before = System.getProperty("line.separator") + tvText.getText();
        tvText.setText(text + before);
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
        protected void onPostExecute(String result) {                           //poi 5개를 execyteReverse해야함.
//            작업 수행 후 가상 parsing 수행
            List<POI> poiList = parser.parse(result);
            apiProgressDialog.dismiss();

//            poiList 의 POI 로 마커 추가 기능 수행
            for(POI poi : poiList){
                executeReverseGeocoding(poi); //위도경도 알아오기.
            }
        }
    }

    private void executeReverseGeocoding(POI poi) {
        if (Geocoder.isPresent()) {
            Toast.makeText(this, "Run ReverseGeocoder", Toast.LENGTH_SHORT).show();
            if(poi.getAddress() != null) {
                new ReverseGeoTask().execute(poi);
            }
        } else {
            Toast.makeText(this, "No Geocoder", Toast.LENGTH_SHORT).show();
        }
    }

    // POI 객체를 전달 받아 위도 경도를 확인하는 Geocoder 를 사용하는 AsyncTask 구현
    class ReverseGeoTask extends AsyncTask<POI, Void, List<Address>> {      //poi 하나에 대해!!
        POI poi;
        String strAddr;  //주소
        String strTitle;
        String strPhone;
        double latitude;
        double longitude;

        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        List<Address> addresses = null;

        @Override
        protected List<Address> doInBackground(POI... pois) {
            poi = pois[0];
            strAddr = poi.getAddress();
            strTitle = poi.getTitle();
            strPhone = poi.getPhone();
            try {
                addresses = geocoder.getFromLocationName(strAddr, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            Address address = addresses.get(0);

            latitude = address.getLatitude();
            longitude = address.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);

            //지도 poi 마커
            MarkerOptions poiMarkerOptions = new MarkerOptions();
            poiMarkerOptions.position(latLng)
                    .title(strTitle)
                    .snippet(strPhone)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

            mPoiMarker = mGoogleMap.addMarker(poiMarkerOptions);

            markerList = new ArrayList<>();
            markerList.add(mPoiMarker);
        }
    }
}