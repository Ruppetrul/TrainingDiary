import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.trainingdiary.MainActivity
import com.example.trainingdiary.R

object NotificationHelper {
    private const val CHANNEL_ID = "training_channel_id"
    private const val NOTIFICATION_ID = 1

    fun createNotification(context: Context) {

        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, "Training Channel", importance)
        channel.description = "Channel for training notifications"

        val notificationManager = context.getSystemService(
            NotificationManager::class.java
        )
        notificationManager.createNotificationChannel(channel)

        val stopIntent = Intent(
            context,
            TrainingStopReceiver::class.java
        )
        val stopPendingIntent =
            PendingIntent.getBroadcast(context, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Training in Progress")
            .setContentText("Tap to end training")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setOngoing(true)
            .addAction(R.drawable.logo, "Stop Training", stopPendingIntent)
            .setContentIntent(
                PendingIntent.getActivity(
                    context, 0, Intent(
                        context,
                        MainActivity::class.java
                    ), PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setAutoCancel(false)

        val notificationManager2 = NotificationManagerCompat.from(context)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager2.notify(NOTIFICATION_ID, builder.build())
    }

    fun cancelNotification(context: Context?) {
        val notificationManager = NotificationManagerCompat.from(context!!)
        notificationManager.cancel(NOTIFICATION_ID)
    }
}