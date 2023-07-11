package mobile.example.sensorbasictest;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

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
		case R.id.btnSensor:

			List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

			String result = "";

			for(Sensor sensor : sensorList){
				String sensorSpec = String.format("Sensor Name: %s\nSensor Type: %s \n\n",
						sensor.getName(), sensor.getType());
				result += sensorSpec;
			}

			tvText.setText(result);
			
			break;
		}
	}
	
	
}
