package mobile.example.movingballtest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

	TextView tvText;
	BallView ballView;

	SensorManager sensorManager;

	float[] mGravity;
	float[] mMagenetic;

	Sensor acclerometer;
	Sensor magnetometer;
	Sensor light;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tvText = findViewById(R.id.tvText);
		ballView = new BallView(this);
		setContentView(ballView);

		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	}


	@Override
	protected void onPause() {
		super.onPause();
		sensorManager.unregisterListener(sensorEventListener);
		sensorManager.unregisterListener(mLightSensorListener);
	}


	@Override
	protected void onResume() {
		super.onResume();
		acclerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

		int sensorDelay = SensorManager.SENSOR_DELAY_NORMAL;

		sensorManager.registerListener(sensorEventListener, acclerometer, sensorDelay);
		sensorManager.registerListener(sensorEventListener, magnetometer, sensorDelay);
		sensorManager.registerListener(mLightSensorListener, light, sensorDelay);

	}

	//좌표넣어주고 그림 바꿔주는 녀석만 추가하면 돼. 대여섯 줄만 추가헤쥑 Lisntener에!!!!!
	SensorEventListener sensorEventListener = new SensorEventListener() {
		@Override
		public void onSensorChanged(SensorEvent event) {
			if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
				mGravity = event.values.clone();
			}
			if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
				mMagenetic = event.values.clone();
			}
			if(mGravity != null && mMagenetic != null){
				float rMatrix[] = new float[9];
				boolean success = SensorManager.getRotationMatrix(rMatrix, null, mGravity,mMagenetic);
				if(success){
					float values[] = new float[3];
					SensorManager.getOrientation(rMatrix, values);
					for(int i=0;i<values.length; i++){
						Double degree = Math.toDegrees(values[i]);
						values[i] = degree.floatValue();
					}
					String result = String.format("azimuth: %f\npitsh: %f\nroll: %f",
							values[0], values[1],values[2]);
					tvText.setText(result);
					if(values[1] > 0){
						ballView.y  -= 10;
					} else{
						ballView.y += 10;
					}
					if(values[2] > 0){
						ballView.x += 10;
					} else{
						ballView.x -= 10;
					}

					ballView.invalidate();
				}
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}
	};

	SensorEventListener mLightSensorListener = new SensorEventListener() {
		@Override
		public void onSensorChanged(SensorEvent event) {
			if(event.values[0] < 30){
				if(ballView.paint.getColor() == Color.RED){
					ballView.paint.setColor(Color.BLUE);
				} else {
					ballView.paint.setColor(Color.RED);
				}
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}
	};
	class BallView extends View{

		Paint paint;

		int width;
		int height;

		int x;
		int y;
		int r;

		boolean isStart;

		public BallView(Context context) {
			super(context);
			paint = new Paint();
			paint.setColor(Color.RED);
			paint.setAntiAlias(true);
			isStart = true;
			r = 100;
		}

		public void onDraw(Canvas canvas) {
			if(isStart) {
				width = canvas.getWidth();
				height = canvas.getHeight();
				x =  width / 2;
				y =  height / 2;
				isStart = false;
			}

			canvas.drawCircle(x, y, r, paint);
		}

	}
}
