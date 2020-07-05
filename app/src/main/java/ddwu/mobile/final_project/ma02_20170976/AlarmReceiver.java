package ddwu.mobile.final_project.ma02_20170976;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {
	String content;
	int id = 0;

	public void onReceive(Context context, Intent intent) {

		content = intent.getStringExtra("content");
		Toast.makeText(context, content, Toast.LENGTH_LONG).show();
		Log.d("content: ", content);
		Log.d("id: ", String.valueOf(intent.getLongExtra("id", 0)));

		Uri ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION);
		Ringtone ringtone = RingtoneManager.getRingtone(context, ringtoneUri);
		ringtone.play();
	}
}