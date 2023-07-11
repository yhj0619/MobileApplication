package ddwu.mobile.finalproject.ma02_20200984;
import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ApiException;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceTypes;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ddwu.mobile.place.placebasic.OnPlaceBasicResult;
import ddwu.mobile.place.placebasic.PlaceBasicManager;
import ddwu.mobile.place.placebasic.pojo.PlaceBasic;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    final static String TAG = "MainActivity";
    final static int PERMISSION_REQ_CODE = 100;

    FusedLocationProviderClient flpClient;
    Location mLastLocation;

    //    Map & Place
    private GoogleMap mGoogleMap;
    private PlaceBasicManager placeBasicManager;
    private PlacesClient placesClient;

    private Marker mCenterMarker;

    double mLatitude;
    double mLongitude;
    String mAddress;
    String mName;
    String mWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        placeBasicManager = new PlaceBasicManager(getString(R.string.api_key));

        placeBasicManager.setOnPlaceBasicResult(onPlaceBasicResult);

        if (checkPermission()) mapLoad();

        // Places 초기화 및 클라이언트 생성
        Places.initialize(getApplicationContext(), getString(R.string.api_key));
        placesClient = Places.createClient(this);

        flpClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment
                = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallback);

        flpClient.requestLocationUpdates(
                getLocationRequest(),
                mLocCallback,
                Looper.getMainLooper()
        );


    }

    /* 소수점 n번째 자리까지 반올림하기 */
    private String doubleToStrFormat(int n, double value) {
        return String.format("%."+n+"f", value);
    }
    OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            mGoogleMap = googleMap;

//            지도 초기 위치 이동
            LatLng latLng = new LatLng(mLatitude, mLongitude);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

//            지도 중심 마커 추가
            MarkerOptions centerMarkerOptions = new MarkerOptions()
                    .position(latLng)
                    .title("현재 위치")
                    .snippet("이동중")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

            mCenterMarker = mGoogleMap.addMarker(centerMarkerOptions);
            mCenterMarker.showInfoWindow();

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

//                지도 위치 이동
                mLastLocation = loc;
                LatLng currentLoc = new LatLng(lat, lng);
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 17));

//                지도 마커 위치 이동
                mCenterMarker.setPosition(currentLoc);
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

    OnPlaceBasicResult onPlaceBasicResult = new OnPlaceBasicResult() {
        @Override
        public void onPlaceBasicResult(List<PlaceBasic> list) {
            for (PlaceBasic place : list) {
                MarkerOptions options = new MarkerOptions()
                        .title(place.getName())
                        .position(new LatLng(place.getLatitude(), place.getLongitude()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                Marker marker = mGoogleMap.addMarker(options);
                /*현재 장소의 place_id 를 각각의 마커에 보관*/
                marker.setTag(place.getPlaceId());
            }
        }
    };

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cafe:
                searchStart(mLatitude,mLongitude, 500, PlaceTypes.CAFE);
                break;
            case R.id.restaurant:
                searchStart(mLatitude,mLongitude, 500, PlaceTypes.RESTAURANT);
                break;
            case R.id.bakery:
                searchStart(mLatitude,mLongitude, 500, PlaceTypes.BAKERY);
                break;
            case R.id.bank:
                searchStart(mLatitude,mLongitude, 500, PlaceTypes.BANK);
                break;
            case R.id.bar:
                searchStart(mLatitude,mLongitude, 500, PlaceTypes.BAR);
                break;
            case R.id.gym:
                searchStart(mLatitude,mLongitude, 500, PlaceTypes.GYM);
                break;
        }
    }


    /*입력된 유형의 주변 정보를 검색
     * PlaceBasicManager 를 사용해 type 의 정보로 PlaceBasic 을 사용하여 현재위치 주변의 관심장소 확인 */
    private void searchStart(double lat, double lng, int radius, String type) {
        placeBasicManager.searchPlaceBasic(lat, lng, radius, type);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        mGoogleMap.setMyLocationEnabled(true);

        getLastLocation();

        /*마커의 InfoWindow 클릭 시 marker에 Tag 로 보관한 placeID 로
         * Google PlacesAPI 를 이용하여 장소의 상세정보*/
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
//                1. 마커에서 Marker.getTag() 를 사용하여 placeID 확인
//                2. getPlaceDetail() 을 호출하여 Place 검색
//                3. callDetailActivity() 에 Place 정보를 전달하고 DetailActivity 호출 (callDetailActivity() 사용)
                String placeId = marker.getTag().toString();    // 마커의 setTag() 로 저장한 Place ID 확인
                getPlaceDetail(placeId);

            }
        });
    }


    /*Place ID 의 장소에 대한 세부정보 획득하여 반환
     * 마커의 InfoWindow 클릭 시 호출*/
    private Place getPlaceDetail(String placeId) {
        List<Place.Field> placeFields       // 상세정보로 요청할 정보의 유형 지정
                = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.PHONE_NUMBER, Place.Field.ADDRESS);

        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();    // 요청 생성

        // 요청 처리 및 요청 성공/실패 리스너 지정
        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override                    // 요청 성공 시 처리 리스너 연결
            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {  // 요청 성공 시
                final Place place = fetchPlaceResponse.getPlace();
                Log.i(TAG, "Place found: " + place.getName());  // 장소 명 확인 등
                Log.i(TAG, "Phone: " + place.getPhoneNumber());
                Log.i(TAG, "Address: " + place.getAddress());
                Log.i(TAG, "ID: " + place.getId());

                mName = place.getName();
                mAddress = place.getAddress();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("name",mName);
                resultIntent.putExtra("address",mAddress);
                setResult(RESULT_OK, resultIntent);
                finish();

                //callDetailActivity(place);          // 해당 정보로 DetailActivity 호출
            }
        }).addOnFailureListener(new OnFailureListener() {   // 요청 실패 시 처리 리스너 연결
            @Override
            public void onFailure(@NonNull Exception exception) {   // 요청 실패 시
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();  // 필요 시 확인
                    Log.e(TAG, "Place not found: " + exception.getMessage());
                }
            }
        });
        return null;
    }


    /*구글맵을 멤버변수로 로딩*/
    private void mapLoad() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);      // 매배변수 this: MainActivity 가 OnMapReadyCallback 을 구현하므로
    }

    @Override
    protected void onPause() {
        super.onPause();
        flpClient.removeLocationUpdates(mLocCallback);
    }

    /* 필요 permission 요청 */
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQ_CODE);
                return false;
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED ) {
                // 퍼미션을 획득하였을 경우 맵 로딩 실행
                mapLoad();
            } else {
                // 퍼미션 미획득 시 액티비티 종료
                Toast.makeText(this, "앱 실행을 위해 권한 허용이 필요함", Toast.LENGTH_SHORT).show();
                finish();
            }
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
                            mLatitude = latitude;
                            mLongitude = longitude;
                            mLastLocation = location;
                        } else {
                            Toast.makeText(MapActivity.this, "No location", Toast.LENGTH_SHORT).show();
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
        Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());

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
                Toast.makeText(MapActivity.this, address.getAddressLine(0), Toast.LENGTH_SHORT).show();
            }
        }
    }
}