package ddwu.mobile.finalproject.ma02_20200984;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SendMessageActivity extends AppCompatActivity{

    EditText etPhone;
    EditText etTitle;
    EditText etContents;
    String str;

    String phoneNumber;
    String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        etTitle = findViewById(R.id.et_diary_title);
        etPhone = findViewById(R.id.et_email);
        etContents = findViewById(R.id.et_contents);

        Intent intent = getIntent();

        etTitle.setText(intent.getStringExtra("name") + "의 현재 날씨");

        str = "현재온도 : " + intent.getStringExtra("temp")
                + "\n상세날씨 : " + intent.getStringExtra("description")
                + "\n현재습도 : " + intent.getStringExtra("humidity")
                + "\n바람 : " + intent.getStringExtra("wind")
                + "\n구름 : " + intent.getStringExtra("clouds");
        etContents.setText(str);

        phoneNumber = etPhone.getText().toString();
        content = "제목: " + etTitle.getText().toString() + "\nContents: " + etContents.getText().toString();

    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnSms:
                sendSmsIntent(phoneNumber);
                break;
        }
    }

    public void sendSmsIntent(String number){
        try{
            Uri smsUri = Uri.parse("sms:"+number);
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO, smsUri);
            sendIntent.putExtra("sms_body", content);
            startActivity(sendIntent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
