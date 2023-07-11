package mobile.example.network.downloadhttp.basic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends Activity {

	EditText etUrl;
	TextView tvResult;
	ImageView imageView;


	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        etUrl = findViewById(R.id.etUrl);
        tvResult = findViewById(R.id.tvResult);
        imageView = findViewById(R.id.imageView);

/*        네트워크 제약 사항 적용을 해제하기 위해 사용
		실습 시 테스트용으로만 사용하며 추후 스레드 또는 AsyncTask 사용 방법으로 대체할 것		*/

		//네트워크 제약사항을 꼼수로 가능하게 함. 원래는 안되는 부분
		StrictMode.ThreadPolicy pol
				= new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
		StrictMode.setThreadPolicy(pol);

	}

	public void onClick(View v) {

		String address = etUrl.getText().toString();
//		String address = getResources().getString(R.string.image_url);

//		네트워크 사용 가능 여부 확인
		if (!isOnline()) {
			Toast.makeText(this, "Network is not available!", Toast.LENGTH_SHORT).show();
			return;
		}

		switch (v.getId()) {
			case R.id.btnDownload:
				if (!address.equals("")) {
					String result = downloadContents(address);
					tvResult.setText(result);
				}
				break;
			case R.id.btnImgDownload:
//				이미지를 다운로드 한 후 readStreamToBitmap() 호출
				String imageAddress = getResources().getString(R.string.image_url);
				Bitmap bitmap = downloadImages(imageAddress);
				imageView.setImageBitmap(bitmap);
				break;
		}

	}


//	네트워크 사용 가능 여부 확인
	private boolean isOnline() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}


//	문자열 형태의 웹 주소를 입력으로 서버 응답을 문자열로 만들어 반환
	private String downloadContents(String address){		// 이미지일 경우 Bitmap 반환
		HttpsURLConnection conn = null;
		InputStream stream = null;
		String result = null;
		int responseCode = 200;

		try {
			URL url = new URL(address);
			conn = (HttpsURLConnection)url.openConnection(); //url connection만드는것

			conn.setReadTimeout(5000);  //읽는데 5초
			conn.setConnectTimeout(5000); //연결하는 데 5초

//			conn.setRequestMethod("GET");
//			conn.setDoInput(true);

			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
 ///여기에 post 방식13page 내용 다 적어주면 돼

			conn.connect(); //연결 이루어지고

			responseCode = conn.getResponseCode(); //데이터를 얻어오는것. 잘 얻어오면 200
			if (responseCode != HttpsURLConnection.HTTP_OK) { //정상 처리 아니면 요고 수행.
				throw new IOException("HTTP error code: " + responseCode);
			}

			stream = conn.getInputStream();
			result = readStream(stream);	//readStream : stream을 문자열로 바꾸는 것

											// 이미지일 경우 readStreamToBitmap 사용
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

	private Bitmap downloadImages(String address){		// 이미지일 경우 Bitmap 반환
		HttpsURLConnection conn = null;
		InputStream stream = null;
		Bitmap result = null;
		int responseCode = 200;

		try {
			URL url = new URL(address);
			conn = (HttpsURLConnection)url.openConnection();

			conn.setReadTimeout(5000);//읽는데 5초만 기다림
			conn.setConnectTimeout(5000);//연결하는것도 5초만 기다림
			conn.setRequestMethod("GET"); //read만 하면 되니까 get방식
			conn.setDoInput(true); //서버로부터 입력을 받아오는거니까.

			conn.connect();//연결 이루어짐

			responseCode = conn.getResponseCode(); //여기서 데이터를 얻어오고, 연결 잘 했으면 200값 return
			if (responseCode != HttpsURLConnection.HTTP_OK) {
				throw new IOException("HTTP error code: " + responseCode);
			}

			stream = conn.getInputStream();
			result = readStreamToBitmap(stream);	// 이미지일 경우 readStreamToBitmap 사용
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


	private Bitmap readStreamToBitmap(InputStream stream) { //이미지 너무 크면 view에 들어올 수 없음.
		//받아오는거까지는 문제 없는데. 이미지가 일정사이즈를 넘어가면 여기서 사이즈를 변환하는 작업을 해줘야함.
		//이미지사이즈 너무 크면 앱이 죽을 것임.
		return BitmapFactory.decodeStream(stream);
	}


}
