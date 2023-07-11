package mobile.example.sensorlistenertest;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

	final String TAG = "SensorTest";

	private TextView tvText;
	SensorManager sensorManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tvText = findViewById(R.id.tvText);
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	}
	
	
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btnStart:
			Log.d(TAG, "Sensing Start!");
			Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
			int sensorDelay = sensorManager.SENSOR_DELAY_NORMAL;
			sensorManager.registerListener(sensorEventListener,sensor,sensorDelay);
			break;
		}
	}
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d(TAG, "Sensing Stop!");
		sensorManager.unregisterListener(sensorEventListener);
	}
	
	SensorEventListener sensorEventListener = new SensorEventListener() {
		@Override
		public void onSensorChanged(SensorEvent sensorEvent) {
			String data = String.format("Value : %f", sensorEvent.values[0]);
			tvText.setText(data);
		}


		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}
	};
}
