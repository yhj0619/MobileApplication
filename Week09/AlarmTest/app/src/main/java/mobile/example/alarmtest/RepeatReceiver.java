package mobile.example.alarmtest;

import android.app.PendingIntent;
import android.content.*;
import android.widget.*;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class RepeatReceiver extends BroadcastReceiver {
	public void onReceive(Context context, Intent i) {
		Toast.makeText(context, "Hi all!", Toast.LENGTH_SHORT).show();

		// notification 생성
		Intent intent = new Intent(context, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

		NotificationCompat.Builder builder= new NotificationCompat.Builder(context, context.getString(R.string.CHANNEL_ID))
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("기상 시간")
				.setContentText("일어나! 공부할 시간이야!")
				.setPriority(NotificationCompat.PRIORITY_DEFAULT)
				.setContentIntent(pendingIntent)
				.addAction(R.drawable.ic_launcher, "Noti", pendingIntent)
				.setAutoCancel(true);

		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

		int notificationId = 100; //알람을 구분하기 위한 정수형 식별자 지정
		notificationManager.notify(notificationId,builder.build()); //이 순간 NOTI 뜸


	}
}
