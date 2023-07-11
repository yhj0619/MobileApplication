package ddwu.mobile.finalproject.ma02_20200984;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class DetailWeatherActivity extends AppCompatActivity{

    TextView tv_name, tv_country;
    ImageView iv_weather, iv_wind, iv_cloud, iv_hum;
    TextView tv_temp, tv_main, tv_description;
    TextView tv_wind, tv_cloud, tv_humidity;

    String name, country,icon, main, description;
    String temp, wind, clouds, humidity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_weather);

        initView();

        Intent intent = getIntent();

        name = intent.getStringExtra("name");
        country = intent.getStringExtra("country");
        temp = intent.getStringExtra("temp");
        main = intent.getStringExtra("main");
        description  = intent.getStringExtra("description");
        wind = intent.getStringExtra("wind");
        clouds = intent.getStringExtra("clouds");
        humidity = intent.getStringExtra("humidity");
        icon = intent.getStringExtra("icon");
        Log.d("name", name);
        Log.d("country", country);
        Log.d("main", main);
        Log.d("description", description);
        Log.d("temp", temp);
        Log.d("wind", wind);
        Log.d("clouds", clouds);
        Log.d("humidity", humidity);
        Log.d("icon",icon);
        tv_name.setText(name);
        tv_country.setText(country);
        Glide.with(DetailWeatherActivity.this).load(icon)  //Glide 라이브러리를 이용하여 ImageView 에 url 로 이미지 지정
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(iv_weather);
        tv_temp.setText(temp);
        tv_main.setText(main);
        tv_description.setText(description);
        tv_wind.setText(wind);
        tv_cloud.setText(clouds);
        tv_humidity.setText(humidity);
    }


    /* view 를 설정하는 메소드 */
    private void initView() {
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_country = (TextView) findViewById(R.id.tv_country);
        iv_weather = (ImageView) findViewById(R.id.iv_weather);
        tv_temp = (TextView) findViewById(R.id.tv_temp);
        tv_main = (TextView) findViewById(R.id.tv_main);
        tv_description = (TextView) findViewById(R.id.tv_description);
        tv_wind = (TextView) findViewById(R.id.tv_wind);
        tv_cloud = (TextView) findViewById(R.id.tv_cloud);
        tv_humidity = (TextView) findViewById(R.id.tv_humidity);
        iv_cloud = (ImageView) findViewById(R.id.imgCloud);
        iv_wind = (ImageView) findViewById(R.id.imgWind);
        iv_hum = (ImageView) findViewById(R.id.imgHum);
    }
}
