package ddwu.mobile.finalproject.ma02_20200984;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RealMainActivity extends AppCompatActivity {
    final static String TAG = "RealMainActivity";
    final int REQ_CODE = 100; //추가
    final int UPDATE_CODE = 200;  //수정
    final int REQ_PERMISSION_CODE = 100;

    String strUrl = "";  //통신할 URL
    RealMainActivity.NetworkTask networkTask = null;

    String name, country,icon, main, description;
    String temp, wind, clouds, humidity;

    String strTemp;

    Weather weather;
    String mWeather;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_main);

        strUrl = getString(R.string.weather_url)+"data/2.5/weather";  //Strings.xml 의 weather_url 로 통신할 URL 사용

        requestNetwork();
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_main:
                Intent intent1 = new Intent(this,MainActivity.class);

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
            case R.id.btn_write:
                Intent intent2 = new Intent(this,AddActivity.class);
                String str = "temperature: " + temp
                        + " (" + description
                        + ")\n습도 : " + humidity
                        + ", 바람 : " + wind
                        + "\n구름 : " + clouds;
                intent2.putExtra("weather",str);
                startActivityForResult(intent2, REQ_CODE);
                break;
            case R.id.btn_recomm:
                Intent intent3 = new Intent(this,RecommClothesActivity.class);
                intent3.putExtra("strTemp", strTemp);
                startActivity(intent3);
                break;
            case R.id.btn_end:
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
    /* NetworkTask 를 요청하기 위한 메소드 */
    private void requestNetwork() {
        ContentValues values = new ContentValues();
        values.put("lat", 37.583);
        values.put("lon", 127.000);
        values.put("appid", getString(R.string.weather_app_id));

        networkTask = new RealMainActivity.NetworkTask(RealMainActivity.this, strUrl, values);
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

                Log.d("weather", mWeather);
                name = weather.getName();
                country = weather.getCountry();
                main = weather.getMain();
                description = weather.getDescription();
                temp = doubleToStrFormat(2, weather.getTemp()) + " ℃";
                wind = doubleToStrFormat(2, weather.getWind()) + " m/s";
                clouds = doubleToStrFormat(2, weather.getClouds()) + " %";
                humidity = doubleToStrFormat(2, weather.getHumidity()) + " %";

                strTemp = doubleToStrFormat(0, weather.getTemp());

                Log.d("strTemp", strTemp);

            } else {
                showFailPop();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

    }  //NetworkTask End

    /* 통신 실패시 AlertDialog 표시하는 메소드 */
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


    /* 소수점 n번째 자리까지 반올림하기 */
    private String doubleToStrFormat(int n, double value) {
        return String.format("%."+n+"f", value);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
