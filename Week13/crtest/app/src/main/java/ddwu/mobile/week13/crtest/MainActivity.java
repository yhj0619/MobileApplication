package ddwu.mobile.week13.crtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    final static String TAG = "MainActivity";

    final int PERMISSION_REQ_CODE = 100;    // Permission 요청 코드

    EditText editText;
    ListView listView;
    ImageView imageView;

    SimpleCursorAdapter adapter;
    ContentResolver contentResolver;
    Cursor mCursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        listView = findViewById(R.id.listView);
        imageView = findViewById(R.id.imageView);

        contentResolver = getContentResolver();

        initList();
    }


    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button:
                if (checkPermission()) {
                    //png, jpeg인지 메소드로 확인.
                    mCursor = queryImageByType(editText.getText().toString());
                    Log.d(TAG, "count: " + mCursor.getCount());
                    if (mCursor.getCount() == 0) {
                        Toast.makeText(getApplicationContext(), "No images", Toast.LENGTH_SHORT).show();
                    } else {
                        //data있으면 SimpleCursor adapter로 바로 커서 넣어줬음!//바로 데이터 보여줌
                        adapter.changeCursor(mCursor);
                    }
                }
                break;
        }
    }


    private Cursor queryImageByType(String imageType) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String selection = MediaStore.Images.ImageColumns.MIME_TYPE + "=?";
        String[] selectArgs = new String[] { "image/" + imageType }; //입력받은 값이 같은지 확인

        if (TextUtils.isEmpty(imageType)) {
            selection = null;
            selectArgs = null;
        }

        return contentResolver.query(uri, null, selection , selectArgs, null);
        //커서를 반환함.
    }


    private void initList() {
        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2,
                null,       // 연결 Cursor
                new String[] { MediaStore.Images.ImageColumns.MIME_TYPE, MediaStore.Images.ImageColumns.DATA },
                new int[] { android.R.id.text1, android.R.id.text2 },
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        listView.setAdapter(adapter);

        //listView 항목을 터치하면 해당 URI를 생성함.
        //ListView에서 클릭하면 position이랑 id가나옴. 얘는 simplecursor adapter니까
        //cursor의 id 값이 자동으로 들어옴!!
        //_id값이 id 값으로 들어옴! id 따로 찾아줄 필요 없음.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                Log.d(TAG, "URI: " + uri);
                imageView.setImageURI(uri);//uri 넣어주면 이미지 보임!
            }
        });
    }


    /* 필요 permission 요청 */
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQ_CODE);
                return false;
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQ_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 퍼미션을 획득하였을 경우 계속 수행
                queryImageByType(editText.getText().toString());
            } else {
                // 퍼미션 미획득 시 액티비티 종료
                Toast.makeText(this, "앱 실행을 위해 권한 허용이 필요함", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}