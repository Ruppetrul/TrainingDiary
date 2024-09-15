import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TrainingStopReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        NotificationHelper.cancelNotification(context)
    }
}