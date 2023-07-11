package mobile.example.network.requesthttp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    final static String TAG = "MainActivity";

    EditText etDate;
    TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etDate = findViewById(R.id.etUrl);
        tvResult = findViewById(R.id.tvResult);

        StrictMode.ThreadPolicy pol
                = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(pol);
    }


    public void onClick(View v) {

        String date =etDate.getText().toString();
        String url = getResources().getString(R.string.url);

        if (!isOnline()) {
            Toast.makeText(this, "Network is not available!", Toast.LENGTH_SHORT).show();
            return;
        }


        switch (v.getId()) {
            case R.id.btn_request:
                String key = getResources().getString(R.string.key);

                String address = key + "amp;targetDt=" + date;
                url.concat(address);
                break;
        }
    }


    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public String downloadContents(String address){
        HttpsURLConnection conn = null;
        InputStream stream = null;
        String result = null;
        int responseCode = 200;

        try {
            URL url = new URL(address);

            conn = (HttpsURLConnection)url.openConnection();

            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setDoInput(true);
            
			conn.setRequestMethod("GET");




            responseCode = conn.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            stream = conn.getInputStream();
            result = readStream(stream);	// 이미지일 경우 readStreamToBitmap 사용
        } catch (MalformedURLException e) {
            Toast.makeText(this, "주소 오류", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NetworkOnMainThreadException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try { stream.close(); }
                catch (IOException e) { e.printStackTrace(); }
            }
            if (conn != null) conn.disconnect();
        }

        return result;
    }

    public String readStream(InputStream stream){
        StringBuilder result = new StringBuilder();

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(stream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String readLine = bufferedReader.readLine();

            while (readLine != null) {
                result.append(readLine + "\n");
                readLine = bufferedReader.readLine();
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }




}
