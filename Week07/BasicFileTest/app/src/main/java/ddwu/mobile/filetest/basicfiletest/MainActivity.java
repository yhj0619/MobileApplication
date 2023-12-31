package ddwu.mobile.filetest.basicfiletest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;

public class MainActivity extends AppCompatActivity {

    final static String TAG = "MainActivity";

    final static String IN_FILE_NAME = "infile.txt";
    final static String EXT_FILE_NAME = "extfile.txt";
    final static String CACHE_FILE_NAME = "cachefile.txt";
    final static String EXT_IMG_FILE_NAME = "test.jpg";

//    final static String IMG_URL = "https://cs.dongduk.ac.kr/img_up/shop_pds/Admin_System/farm2_skin_data/logo01484890324.png";
    final static String IMG_URL = "http://soft.dongduk.ac.kr:8080/downloads/images/cat.jpg";


    EditText etText;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etText = findViewById(R.id.etText);
        imageView = findViewById(R.id.imageView);
    }


    public void onInClick(View v) {
        switch(v.getId()) {
            case R.id.btnInWrite: //editText에 글씨 쓰면 바로 읽어짐.
                String data = etText.getText().toString();
                File saveFile = new File(getFilesDir(), IN_FILE_NAME); //앱 실행하고 내부 쓰기 눌러야지 파일 디렉토리 만들어짐.
                try {
//                    FileOutputStream fos = new FileOutputStream((saveFile));
                    FileOutputStream fos = openFileOutput(IN_FILE_NAME, Context.MODE_APPEND);
                    fos.write(data.getBytes());
                    fos.flush();
                    fos.close();
                } catch (IOException e) { e.printStackTrace(); }
                break;
            case R.id.btnInRead:
                String path = getFilesDir() + "/" + IN_FILE_NAME;
                File readFile = new File(path);
                try {
                    FileReader fileReader = new FileReader(readFile);
                    BufferedReader br = new BufferedReader(fileReader);
                    String line = "";
                    String result = "";
                    while ((line = br.readLine()) != null) {
                        result += line;
                    }
                    etText.setText(result);
                    br.close();
                } catch (IOException e) { e.printStackTrace(); }
                break;
            case R.id.btnInDelete:
                File inFiles = getFilesDir();
                File[] files = inFiles.listFiles();
                for  (File target : files) {
                    target.delete();
                }
                break;
        }
    }


    public void onExtClick(View v) {
        switch (v.getId()) {
            case R.id.btnExtWrite:
                if (isExternalStorageWritable()) { //사용할 수 있는지 check
                    File file = new File (getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                            "myalbum");
                    if (!file.mkdirs()) { //디렉토리로 꼭 안만들어도 됨. 이 방식은 내부저장소도 동일함!! 넣어줘도 되고 아니어도 되고
                        Log.d(TAG, "directory not created");
                    }

                    String data = etText.getText().toString();
                    File saveFile = new File(file.getPath(), EXT_FILE_NAME);
                    try {
                        FileOutputStream fos = new FileOutputStream((saveFile));
                        fos.write(data.getBytes());
                        fos.flush();
                        fos.close();
                    } catch (IOException e) { e.printStackTrace(); }
                }
                break;
            case R.id.btnExtRead:
                if (isExternalStorageWritable()) {
                    String path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath()
                            + "/myalbum/" + EXT_FILE_NAME;
                    File readFile = new File(path);
                    try {
                        FileReader fileReader = new FileReader(readFile);
                        BufferedReader br = new BufferedReader(fileReader);
                        String line = "";
                        String result = "";
                        while ((line = br.readLine()) != null) {
                            result += line;
                        }
                        etText.setText(result);
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btnExtDelete:

                break;
        }
    }

    public void onCacheClick(View v) {
        switch(v.getId()) {
            case R.id.btnCacheWrite:
//                getCacheDir();  내부저장소의 cache dir
//                getExternalCacheDir();  외부저장소의 cache dir
                break;
            case R.id.btnCacheRead:

                break;
            case R.id.btnCacheDelete:

                break;
        }
    }


    public void onImgClick(View v) {
        switch (v.getId()) {
            case R.id.btnImgShow:
                Glide.with(this)
                        .load(IMG_URL)
                        .into(imageView);

                break;
            case R.id.btnImgRead:

                if (isExternalStorageWritable()) {
                    String path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath()
                            + "/myalbum/" + EXT_IMG_FILE_NAME;
                    File readFile = new File(path);
                    Glide.with(this)
                            .load(readFile)
                            .into(imageView);
                    //glide 써서 read하면 이렇게 빠름ㅁㄴ
                }


                break;
            case R.id.btnImgSave:

                Glide.with(MainActivity.this)
                        .asBitmap()
                        .load(IMG_URL)
                        .into(new CustomTarget<Bitmap>() { //Glide로 하면 이렇게 긴 코드대신 간단하게 !!
                            //파일에다가 imageView를 넣어주고싶기 때문에 이렇게 긴 코드
                            @Override //이미지 성공적으로 받아옴
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                if (isExternalStorageWritable()) {
                                    File file = new File (getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                            "myalbum");
                                    if (!file.mkdirs()) {
                                        Log.d(TAG, "directory not created");
                                    }
                                    File saveFile = new File(file.getPath(), EXT_IMG_FILE_NAME);
                                    try {
                                        FileOutputStream fos = new FileOutputStream((saveFile));
                                        resource.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                        fos.flush();
                                        fos.close();
                                        Toast.makeText(MainActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                                    } catch (IOException e) { e.printStackTrace(); }
                                }
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
                break;
        }
    }


    private boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

}