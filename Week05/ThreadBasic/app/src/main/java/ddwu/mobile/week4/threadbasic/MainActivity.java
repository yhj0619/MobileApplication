package ddwu.mobile.week4.threadbasic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    final static String TAG = "MainActivity";

    EditText etText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etText = findViewById(R.id.etText);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnStart:
                TestThread t = new TestThread(handler);
                t.start();
                etText.setText("Thread start!");//이 두 줄은 바로 수행됨 안기다리고!
                Toast.makeText(this, "Running!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) { //메시지 수신하는 기능, sendMessage의 매개변수가 들어옴
            int  i =msg.arg1;
            etText.setText("i: " + i);
            //이 핸들러는 MainActivity안에 만들어진 멤버이므로 thread와 상관이 없음. etText접근 가능
        }
    };


    class TestThread extends Thread { //계속 버튼 누르면 여러개의 thread가 실행됨. 번호 섞여서 Log에 찍힘
        Handler handler;

        public TestThread(Handler handler){
            this.handler=handler;
        }
        @Override
        public void run() { //해야할 부분 재정의. i값 넣어서 Log 찍어봄
            for (int i=0; i < 100; i++) {
//                Log.d(TAG, "i: " + i );
                Message msg = Message.obtain();
                msg.arg1 = i;
                handler.sendMessage(msg);//sendMessage하는 순간 Handler의 hanldeMessage 호출!!!
//              handler한테 sendMessage를 보내는 기능.

//                etText.setText("i: " + i ); //별도의 THread를 만든 부분에서는 이렇게 화면의 요소를 건들일 수 없음!! 오휴남
                //etText는 화면에 있는 입력창을 의미. UI와 관련된거는 UI Thread(=Main Thread)에서 실행되기에. 화면 요소에 대한 소유권은 Main Thread가 갖고있음.
                //이걸 새로만든 Thread에서 한다면, 다시 화면이 멈춰진것처럼 보여질 수 있음!!!
                //이를 방지하기 위해 UI Thread의 요소들은 별도 thread에서 접근하고 수정하는 것을 금지시킴.
                //Log는 디버깅 작업이기에 가능.
                //정리 - 별도의 Thread에서는 화면에 속해있는 UI 요소를 직접 접근할 수 없음!!!!
                try {
                    Thread.sleep(50); //Thread 쉬고,
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}

