package ddwu.mobile.finalproject.ma02_20200984;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class UpdateActivity extends AppCompatActivity {

    Diary diary;

    EditText etTitle;
    EditText etContents;
    TextView etDate;
    TextView etTime;
    TextView etLocation;
    TextView etWeather;

    ImageView imageView;
    DiaryDBManager diaryDBManager;
    Calendar calendar;

    final static int INTENT_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        diary = (Diary) getIntent().getSerializableExtra("diary");

        etTitle = findViewById(R.id.et_diaryTitle);
        etContents = findViewById(R.id.et_contents);
        etDate = findViewById(R.id.et_date);
        etTime = findViewById(R.id.et_time);
        etLocation = findViewById(R.id.et_location);
        etWeather = findViewById(R.id.et_weather);

        etTitle.setText(diary.getTitle());
        etContents.setText(diary.getContents());
        etDate.setText(diary.getDate());
        etTime.setText(diary.getTime());
        etLocation.setText(diary.getLocation());
        etWeather.setText(diary.getWeather());

        calendar = Calendar.getInstance();

        diaryDBManager = new DiaryDBManager(this);
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            etDate.setText(String.format("%d.\n%d.%d", year,month,dayOfMonth));
        }
    };
    TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            etTime.setText(String.format("%d시 %d분",hourOfDay, minute));
        }
    };


    public void OnButtonClick(View v)
    {
        switch (v.getId()){
            case R.id.button:
                new DatePickerDialog(this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.button2:
                new TimePickerDialog(this,timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),false).show();
                break;

            case R.id.button3:
                Intent intent = new Intent(UpdateActivity.this, MapActivity.class);
                startActivityForResult(intent,INTENT_CODE);
                break;
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        switch(requestCode){ //요청했던 코드에 해당하는 결과인지 확인
            case INTENT_CODE: //resultCode의 값에 따라!
                if(resultCode == RESULT_OK){
                    String resultName = data.getStringExtra("name");
                    String resultAddress = data.getStringExtra("address");

                    etLocation.setText(String.format("%s\n%s", resultName, resultAddress));
                }
                break;
        }
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_update:
                diary.setTitle(etTitle.getText().toString());
                diary.setContents(etContents.getText().toString());
                diary.setDate(etDate.getText().toString());
                diary.setTime(etTime.getText().toString());
                diary.setLocation(etLocation.getText().toString());
                diary.setWeather(etWeather.getText().toString());


                if(etTitle.getText().toString().equals("")||etContents.getText().toString().equals("")||etDate.getText().toString().equals("")||etTime.getText().toString().equals("")||etLocation.getText().toString().equals("") ||etWeather.getText().toString().equals("")){
                    Toast.makeText(this,"빈칸을 모두 입력하시오.",Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED);
                } else if (diaryDBManager.modifyDiary(diary)) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("diary", diary);
                    setResult(RESULT_OK, resultIntent);
                } else {
                    setResult(RESULT_CANCELED);
                }


                break;
            case R.id.btn_cancel:
                setResult(RESULT_CANCELED);
                break;
        }
        finish();
    }
}
