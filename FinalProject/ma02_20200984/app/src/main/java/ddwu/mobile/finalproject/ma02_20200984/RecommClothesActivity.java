package ddwu.mobile.finalproject.ma02_20200984;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class RecommClothesActivity extends AppCompatActivity {

    TextView tvRangeTemp;
    TextView tvClothes;
    ImageView ivPicture;


    String strTemp;
    int temp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomm_clothes);

        Intent intent = getIntent();

        strTemp = intent.getStringExtra("strTemp");
        temp = Integer.parseInt(strTemp);
        Log.d("int temp" , "int temp : " + temp);

        tvRangeTemp = findViewById(R.id.tv_rangeTemp);
        tvClothes = findViewById(R.id.tv_clothes);
        ivPicture = findViewById(R.id.iv_picture);

        if(temp >= 28){
            tvRangeTemp.setText("28℃~");
            tvClothes.setText("민소매, 반팔, 반바지, 원피스");
            Glide.with(this).load(R.mipmap.four)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(ivPicture);
        }
        else if(temp >= 23 && temp <= 27){
            tvRangeTemp.setText("23℃~27℃");
            tvClothes.setText("반팔, 얇은 셔츠, 반바지, 면바지");
            Glide.with(this).load(R.mipmap.tewntyseven)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(ivPicture);
        }
        else if(temp >= 20 && temp <= 22){
            tvRangeTemp.setText("20℃~22℃");
            tvClothes.setText("얇은 가디건, 청바지, 면바지, 긴팔");
            Glide.with(this).load(R.mipmap.twentytwo)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(ivPicture);
        }
        else if(temp >= 17 && temp <= 19){
            tvRangeTemp.setText("17℃~19℃");
            tvClothes.setText("얇은 니트, 맨투맨, 가디건, 얇은 자켓, 청바지");
            Glide.with(this).load(R.mipmap.nineteen)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(ivPicture);
        }
        else if(temp >= 9 && temp <= 16){
            tvRangeTemp.setText("9℃~16℃");
            tvClothes.setText("자켓, 트렌치코트, 가디건, 니트, 청바지, 스타킹");
            Glide.with(this).load(R.mipmap.sixteen)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(ivPicture);
        }
        else if(temp >= 5 && temp <= 8){
            tvRangeTemp.setText("5℃~8℃");
            tvClothes.setText("코트, 가죽자켓, 히트텍, 두꺼운니트, 후드티");
            Glide.with(this).load(R.mipmap.eight)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(ivPicture);
        }
        else if(temp <= 4){
            tvRangeTemp.setText("~4℃");
            tvClothes.setText("패딩, 두꺼운코트, 목도리, 기모제품");
            Glide.with(this).load(R.mipmap.four)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(ivPicture);
        }

    }
}
