import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.trainingdiary.MainActivity
import com.example.trainingdiary.PlayerReceiver
import com.example.trainingdiary.R

object NotificationHelper {

    const val CHANNEL_ID = "training_channel_id"
    const val NOTIFICATION_ID = 1

    const val ACTION_INCREMENT_WEIGHT = "ACTION_INCREMENT_WEIGHT"
    const val ACTION_DECREMENT_WEIGHT = "ACTION_DECREMENT_WEIGHT"
    const val ACTION_INCREMENT_REPEAT = "ACTION_INCREMENT_REPEAT"
    const val ACTION_DECREMENT_REPEAT = "ACTION_DECREMENT_REPEAT"
    const val ACTION_SAVE = "ACTION_SAVE"

    const val EXTRA_WEIGHT = "EXTRA_WEIGHT"
    const val EXTRA_REPEAT = "EXTRA_REPEAT"
    const val EXTRA_EXERCISE = "EXTRA_EXERCISE"
    const val EXTRA_APPROACH = "EXTRA_APPROACH"

    fun createNotification(context: Context, exercise: String, approachNumber: Int, weight: Float, repeat: Int) {
        val channel = NotificationChannel(CHANNEL_ID, "Training Channel", NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = "Channel for training notifications"

        val notificationManager = context.getSystemService(
            NotificationManager::class.java
        )
        notificationManager.createNotificationChannel(channel)

        val remoteViews = RemoteViews(context.packageName, R.layout.notification_player).apply {
            setTextViewText(R.id.player_weight, weight.toString())
            setTextViewText(R.id.player_repeat, repeat.toString())
            setTextViewText(R.id.player_exercise_name, "$exercise (Подход: $approachNumber)")

            setOnClickPendingIntent(R.id.player_weight_plus, buildPendingIntent(context, ACTION_INCREMENT_WEIGHT, 10, exercise, approachNumber, weight, repeat))
            setOnClickPendingIntent(R.id.player_weight_minus, buildPendingIntent(context, ACTION_DECREMENT_WEIGHT, 20, exercise, approachNumber, weight, repeat))
            setOnClickPendingIntent(R.id.player_repeat_plus, buildPendingIntent(context, ACTION_INCREMENT_REPEAT, 30, exercise, approachNumber, weight, repeat))
            setOnClickPendingIntent(R.id.player_repeat_minus, buildPendingIntent(context, ACTION_DECREMENT_REPEAT, 40, exercise, approachNumber, weight, repeat))

            setOnClickPendingIntent(R.id.player_save_approach, buildPendingIntent(context, ACTION_SAVE, 50, exercise, approachNumber, weight, repeat))
        }

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Training in Progress")
            .setCustomBigContentView(
                remoteViews
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setOngoing(true)
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

    private fun buildPendingIntent(
        context: Context,
        actionTag: String,
        requestCode: Int,
        exercise: String,
        approachNumber: Int,
        weight: Float,
        repeat: Int
    ): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            buildIntent(context, actionTag, exercise, approachNumber, weight, repeat),
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun buildIntent(context: Context,
                            actionTag: String,
                            exercise: String,
                            approachNumber: Int,
                            weight: Float,
                            repeat: Int): Intent {
        return Intent(context, PlayerReceiver::class.java).apply {
            action = actionTag
            putExtra(EXTRA_WEIGHT, weight)
            putExtra(EXTRA_REPEAT, repeat)
            putExtra(EXTRA_APPROACH, approachNumber)
            putExtra(EXTRA_EXERCISE, exercise)
        }
    }
}