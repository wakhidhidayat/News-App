package com.wahidhidayat.newsapp.notifications;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.wahidhidayat.newsapp.R;
import com.wahidhidayat.newsapp.activities.MainActivity;

import java.util.Calendar;

public class DailyReminderReceiver extends BroadcastReceiver {
    private final int DAILY_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        DailyReminder(context);
    }

    private void DailyReminder(Context context) {
        int req = 19;
        String id = "chanelFirst";
        String name = "DailyReminder";
        String title = context.getString(R.string.judul_daily);
        CharSequence message = context.getString(R.string.desk_daily);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent chPendingIntent = TaskStackBuilder.create(context)
                .addNextIntent(intent)
                .getPendingIntent(req, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, id)
                .setContentIntent(chPendingIntent)
                .setSmallIcon(R.drawable.ic_baseline_notifications_none_24)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_baseline_notifications_none_24))
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT);
            builder.setChannelId(id);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        if (notificationManager != null) {
            notificationManager.notify(DAILY_ID, builder.build());
        }
    }

    public void dailyReminderOn(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, DailyReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, DAILY_ID, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (alarmManager != null) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }
}
