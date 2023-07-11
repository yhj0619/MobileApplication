package ddwu.mobile.finalproject.ma02_20200984;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SendEmailActivity extends AppCompatActivity {
    EditText etEmail;
    EditText etTitle;
    EditText etContents;
    String str;
    String title;
    String emailAddress;
    String content;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);

        etTitle = findViewById(R.id.et_diary_title);
        etEmail = findViewById(R.id.et_email);
        etContents = findViewById(R.id.et_contents);

        Intent intent = getIntent();

        etTitle.setText(intent.getStringExtra("name") + "의 현재 날씨");

        str = "현재온도 : " + intent.getStringExtra("temp")
                + "\n상세날씨 : " + intent.getStringExtra("description")
                + "\n현재습도 : " + intent.getStringExtra("humidity")
                + "\n바람 : " + intent.getStringExtra("wind")
                + "\n구름 : " + intent.getStringExtra("clouds");
        etContents.setText(str);

        emailAddress = etEmail.getText().toString();
        title = etTitle.getText().toString();
        content = etContents.getText().toString();

    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnEmail:
                sendEmail(emailAddress);
                break;
        }
    }

    public void sendEmail(String email){
        try{
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            emailIntent.putExtra(Intent.EXTRA_CC, new String[]{email});
            emailIntent.putExtra(Intent.EXTRA_BCC, new String[]{email});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, title);
            emailIntent.putExtra(Intent.EXTRA_TEXT, content);
            emailIntent.setType("message/rfc822");
            startActivity(Intent.createChooser(emailIntent, "Email Choose"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
