package ddwu.mobile.finalproject.ma02_20200984;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity{
    final static String TAG = "MainActivity";
    final int REQ_CODE = 100; //추가
    final int UPDATE_CODE = 200;  //수정
    final int REQ_PERMISSION_CODE = 100;

    private ArrayList<Diary> diaryList = null;
    private CustomAdapter myAdapter;
    private ListView listView;
    DiaryDBManager diaryDBManager;

    FusedLocationProviderClient flpClient;
    Location mLastLocation;

    double mLatitude;
    double mLongitude;

    String mWeather;

    TextView tv_name, tv_country;
    ImageView iv_weather;
    TextView tv_temp, tv_main, tv_description;

    String strUrl = "";  //통신할 URL
    MainActivity.NetworkTask networkTask = null;

    String name, country,icon, main, description;
    String temp, wind, clouds, humidity;

    Weather weather;
    String strTemp;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);

        return true;
    }

    public void onMenuClick(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu0:
                Intent intent3 = new Intent(this,RecommClothesActivity.class);
                intent3.putExtra("strTemp", strTemp);
                startActivity(intent3);
                break;
            case R.id.menu1:
                Intent intent = new Intent(this, AddActivity.class);

                String str = "temperature: " + temp
                        + " (" + description
                        + ")\n습도 : " + humidity
                        + ", 바람 : " + wind
                        + "\n구름 : " + clouds;
                Log.d("weather",str);
                intent.putExtra("weather",str);
                startActivityForResult(intent, REQ_CODE);
                break;
            case R.id.menu2:
                Intent intent2 = new Intent(this,Introduce.class);
                startActivity(intent2);
                break;
            case R.id.menu3:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("앱 종료")
                        .setMessage("앱을 종료하시겠습니까?")
                        .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("취소",null)
                        .show();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView =  findViewById(R.id.listView);
        diaryList = new ArrayList();
        myAdapter = new CustomAdapter(this, R.layout.custom_adapter_view, diaryList);
        listView.setAdapter(myAdapter);
        diaryDBManager = new DiaryDBManager(this);

        strUrl = getString(R.string.weather_url)+"data/2.5/weather";  //Strings.xml 의 weather_url 로 통신할 URL 사용

        initView();
        requestNetwork();
        checkPermission();

        flpClient = LocationServices.getFusedLocationProviderClient(this);

        flpClient.requestLocationUpdates(
                getLocationRequest(),
                mLocCallback,
                Looper.getMainLooper()
        );

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Diary diary = diaryList.get(position);
                Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
                intent.putExtra("diary", diary);
                startActivityForResult(intent, UPDATE_CODE);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("일기 삭제")
                        .setMessage(diaryList.get(pos).getTitle().toString() + "를 삭제하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (diaryDBManager.removeDiary(diaryList.get(pos).get_id())) {
                                    Toast.makeText(MainActivity.this, "삭제 완료", Toast.LENGTH_SHORT).show();
                                    diaryList.clear();
                                    diaryList.addAll(diaryDBManager.getAllDiary());
                                    myAdapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(MainActivity.this, "삭제 실패", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("취소", null)
                        .setCancelable(false)
                        .show();
                return true;
            }
        });
        iv_weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(MainActivity.this, DetailWeatherActivity.class);

                intent1.putExtra("name", name);
                intent1.putExtra("country", country);
                intent1.putExtra("icon", icon);
                intent1.putExtra("main", main);
                intent1.putExtra("description", description);
                intent1.putExtra("temp", temp);
                intent1.putExtra("wind", wind);
                intent1.putExtra("clouds", clouds);
                intent1.putExtra("humidity", humidity);

                startActivity(intent1);
            }
        });

    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnSearch:
                Intent intent1 = new Intent(MainActivity.this, DetailWeatherActivity.class);

                intent1.putExtra("name", name);
                intent1.putExtra("country", country);
                intent1.putExtra("icon", icon);
                intent1.putExtra("main", main);
                intent1.putExtra("description", description);
                intent1.putExtra("temp", temp);
                intent1.putExtra("wind", wind);
                intent1.putExtra("clouds", clouds);
                intent1.putExtra("humidity", humidity);

                startActivity(intent1);
                break;
            case R.id.btnAddDiary:
                Intent intent = new Intent(this, AddActivity.class);

                String str = "temperature: " + temp
                        + " (" + description
                        + ")\n습도 : " + humidity
                        + ", 바람 : " + wind
                        + "\n구름 : " + clouds;
                Log.d("weather",str);
                intent.putExtra("weather",str);
                intent.putExtra("mWeather",mWeather);

                startActivityForResult(intent, REQ_CODE);
                break;
            case R.id.btnSendEmail:

                Intent intent3 = new Intent(this, SendEmailActivity.class);
                intent3.putExtra("name", name);
                intent3.putExtra("main", main);
                intent3.putExtra("description", description);
                intent3.putExtra("temp", temp);
                intent3.putExtra("wind", wind);
                intent3.putExtra("clouds", clouds);
                intent3.putExtra("humidity", humidity);
                startActivityForResult(intent3, REQ_CODE);
                break;
            case R.id.btnSns:
                Intent intent2 = new Intent(this, SendMessageActivity.class);
                intent2.putExtra("name", name);
                intent2.putExtra("main", main);
                intent2.putExtra("description", description);
                intent2.putExtra("temp", temp);
                intent2.putExtra("wind", wind);
                intent2.putExtra("clouds", clouds);
                intent2.putExtra("humidity", humidity);
                startActivityForResult(intent2, REQ_CODE);
                break;

        }
    }

    /* view 를 설정하는 메소드 */
    private void initView() {
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_country = (TextView) findViewById(R.id.tv_country);
        iv_weather = (ImageView) findViewById(R.id.iv_weather);
        tv_temp = (TextView) findViewById(R.id.tv_temp);
        tv_main = (TextView) findViewById(R.id.tv_main);
        tv_description = (TextView) findViewById(R.id.tv_description);

    }


    /* NetworkTask 를 요청하기 위한 메소드*/
    private void requestNetwork() {
        ContentValues values = new ContentValues();
        values.put("lat", 37.583);
        values.put("lon", 127.000);
        values.put("appid", getString(R.string.weather_app_id));

        networkTask = new NetworkTask(MainActivity.this, strUrl, values);
        networkTask.execute();
    }

    /* 비동기 처리를 위해 AsyncTask 상속한 NetworkTask 클래스 */
    public class NetworkTask extends AsyncTask<Void, Void, String> {
        Context context;
        String url = "";
        ContentValues values;

        public NetworkTask(Context context, String url, ContentValues values) {
            this.context = context;
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";

            RequestHttpUrlConnection requestHttpUrlConnection = new RequestHttpUrlConnection();
            result = requestHttpUrlConnection.request(url, values, "GET");  //HttpURLConnection 통신 요청

            Log.d(TAG, "NetworkTask >> doInBackground() - result : " + result);
            return result;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "NetworkTask >> onPostExecute() - result : " + result);

            if (result != null && !result.equals("")) {
                JsonParser jp = new JsonParser();
                JsonObject jsonObject = (JsonObject) jp.parse(result);
                JsonObject jsonObjectSys = (JsonObject) jp.parse(jsonObject.get("sys").getAsJsonObject().toString());
                JsonObject jsonObjectWeather = (JsonObject) jp.parse(jsonObject.get("weather").getAsJsonArray().get(0).toString());
                JsonObject jsonObjectMain = (JsonObject) jp.parse(jsonObject.get("main").getAsJsonObject().toString());
                JsonObject jsonObjectWind = (JsonObject) jp.parse(jsonObject.get("wind").getAsJsonObject().toString());
                JsonObject jsonObjectClouds = (JsonObject) jp.parse(jsonObject.get("clouds").getAsJsonObject().toString());

                weather = new Weather();
                weather.setName(jsonObject.get("name").toString().replaceAll("\"",""));
                weather.setCountry(jsonObjectSys.get("country").toString().replaceAll("\"",""));
                weather.setIcon(getString(R.string.weather_url)+"img/w/" + jsonObjectWeather.get("icon").toString().replaceAll("\"","") + ".png");
                weather.setTemp(jsonObjectMain.get("temp").getAsDouble() - 273.15);
                weather.setMain(jsonObjectWeather.get("main").toString().replaceAll("\"",""));
                weather.setDescription(jsonObjectWeather.get("description").toString().replaceAll("\"",""));
                weather.setWind(jsonObjectWind.get("speed").getAsDouble());
                weather.setClouds(jsonObjectClouds.get("all").getAsDouble());
                weather.setHumidity(jsonObjectMain.get("humidity").getAsDouble());

                icon = getString(R.string.weather_url)+"img/w/" + jsonObjectWeather.get("icon").toString().replaceAll("\"","") + ".png";
                mWeather = "temperature: " + String.format("%.2f", jsonObjectMain.get("temp").getAsDouble() - 273.15) + "℃ (" +
                        jsonObjectWeather.get("description").toString().replaceAll("\"","") + ")";

                Log.d("mWeather", mWeather);
                setWeatherData(weather);  //UI 업데이트

            } else {
                showFailPop();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

    }  //NetworkTask End


    /* 통신하여 받아온 날씨 데이터를 통해 UI 업데이트 메소드*/
    private void setWeatherData(Weather model) {
        Log.d(TAG, "setWeatherData");
        tv_name.setText(model.getName());
        tv_country.setText(model.getCountry());
        Glide.with(MainActivity.this).load(model.getIcon())  //Glide 라이브러리를 이용하여 ImageView 에 url 로 이미지 지정
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(iv_weather);
        tv_temp.setText(doubleToStrFormat(2, model.getTemp()) + " ℃");  //소수점 2번째 자리까지 반올림하기
        tv_main.setText(model.getMain());
        tv_description.setText(model.getDescription());

        name = model.getName();
        country = model.getCountry();
        main = model.getMain();
        description = model.getDescription();
        temp = doubleToStrFormat(2, model.getTemp()) + " ℃";
        wind = doubleToStrFormat(2, model.getWind()) + " m/s";
        clouds = doubleToStrFormat(2, model.getClouds()) + " %";
        humidity = doubleToStrFormat(2, model.getHumidity()) + " %";

        strTemp = doubleToStrFormat(0, weather.getTemp());
    }


    /* 통신 실패시 AlertDialog 표시하는 메소드*/
    private void showFailPop() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title").setMessage("통신실패");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getApplicationContext(), "OK Click", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getApplicationContext(), "Cancel Click", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


     /*소수점 n번째 자리까지 반올림하기*/
    private String doubleToStrFormat(int n, double value) {
        return String.format("%."+n+"f", value);
    }

    @Override
    protected void onResume() {
        super.onResume();
        diaryList.clear();
        diaryList.addAll(diaryDBManager.getAllDiary());
        myAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    LocationCallback mLocCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            for (Location loc : locationResult.getLocations()) {
                double lat = loc.getLatitude();
                double lng = loc.getLongitude();

                mLatitude = lat;
                mLongitude = lng;
                mLastLocation = loc;
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
                            mLatitude = latitude;
                            mLongitude = longitude;
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
}
