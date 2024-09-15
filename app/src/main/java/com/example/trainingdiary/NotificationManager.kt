import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.trainingdiary.MainActivity
import com.example.trainingdiary.PlayerReceiver
import com.example.trainingdiary.R
import com.example.trainingdiary.ui.home.HistoryHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    const val EXTRA_APPROACH_ID = "EXTRA_APPROACH_ID"

    fun createNotification(context: Context, exercise: String, approachNumber: Int, weight: Float, repeat: Int, approachId: Int) {
        Log.d("TAG", "createNotification: $exercise")
        Log.d("TAG", "createNotification: $approachNumber")
        Log.d("TAG", "createNotification: $weight")
        Log.d("TAG", "createNotification: $repeat")
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

            setOnClickPendingIntent(R.id.player_weight_plus, buildPendingIntent(context, ACTION_INCREMENT_WEIGHT, 10, exercise, approachNumber, weight, repeat, approachId))
            setOnClickPendingIntent(R.id.player_weight_minus, buildPendingIntent(context, ACTION_DECREMENT_WEIGHT, 20, exercise, approachNumber, weight, repeat, approachId))
            setOnClickPendingIntent(R.id.player_repeat_plus, buildPendingIntent(context, ACTION_INCREMENT_REPEAT, 30, exercise, approachNumber, weight, repeat, approachId))
            setOnClickPendingIntent(R.id.player_repeat_minus, buildPendingIntent(context, ACTION_DECREMENT_REPEAT, 40, exercise, approachNumber, weight, repeat, approachId))

            setOnClickPendingIntent(R.id.player_save_approach, buildPendingIntent(context, ACTION_SAVE, 50, exercise, approachNumber, weight, repeat, approachId))
        }

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(getAppName(context))
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
        repeat: Int,
        approachId: Int
    ): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            buildIntent(context, actionTag, exercise, approachNumber, weight, repeat, approachId),
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun buildIntent(context: Context,
                            actionTag: String,
                            exercise: String,
                            approachNumber: Int,
                            weight: Float,
                            repeat: Int,
                            approachId: Int): Intent {
        return Intent(context, PlayerReceiver::class.java).apply {
            action = actionTag
            putExtra(EXTRA_WEIGHT, weight)
            putExtra(EXTRA_REPEAT, repeat)
            putExtra(EXTRA_APPROACH, approachNumber)
            putExtra(EXTRA_EXERCISE, exercise)
            putExtra(EXTRA_APPROACH_ID, approachId)
        }
    }

    fun hideNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }

    fun getAppName(context: Context): String {
        val packageManager = context.packageManager

        val applicationInfo: ApplicationInfo = packageManager.getApplicationInfo(context.packageName, 0)

        return packageManager.getApplicationLabel(applicationInfo).toString()
    }

    fun createPlayer(context: Context, fromPlayer: Boolean = false) {
        CoroutineScope(Dispatchers.Main).launch {
            val result = HistoryHelper(context).getFirstNotConfirmedApproach()

            val approach = result.firstNotConfirmedApproach
            val exerciseTitle = result.approachExerciseTitle
            val approachNumber = result.approachIndex
            val hasExercisesWithoutApproaches = result.hasExercisesWithoutApproaches

            val handler: Handler = Handler(Looper.getMainLooper())

            if (hasExercisesWithoutApproaches && !fromPlayer) {
                handler.postDelayed(Runnable {
                    Toast.makeText(
                        context,
                        "Для некоторых упражнений нет подходов!",
                        Toast.LENGTH_SHORT
                    ).show()
                }, 100)
            }

            if (approach == null || exerciseTitle == null || approachNumber == null) {
                hideNotification(context)

                val text = if (fromPlayer) {
                    "Тренировка закончена"
                } else {
                    "Сначала нужно составить план тренировки!"
                }

                handler.postDelayed(Runnable {
                    Toast.makeText(
                        context,
                        text,
                        Toast.LENGTH_SHORT
                    ).show()
                }, 1000)
            } else {
                NotificationHelper.createNotification(
                    context,
                    exerciseTitle,
                    approachNumber,
                    approach.weight,
                    approach.repeatCount,
                    approach.id
                )
                handler.postDelayed(Runnable {
                    Toast.makeText(
                        context,
                        "Откройте плеер в шторке уведомленй!",
                        Toast.LENGTH_SHORT
                    ).show()
                }, 1000)
            }
        }
    }
}