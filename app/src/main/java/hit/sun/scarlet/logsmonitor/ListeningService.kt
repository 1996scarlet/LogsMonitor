package hit.sun.scarlet.logsmonitor

import android.app.IntentService
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.util.Log

class ListeningService : IntentService("ListeningService") {

    override fun onHandleIntent(p0: Intent?) {
        Log.d("OJBK", "Online!")
        while (true) {
            getNewAttackInfo()
            Thread.sleep(60 * 1000)
        }
    }

    private fun getNewAttackInfo() {

        val pIntent = PendingIntent.getActivity(this, 1, Intent(this, MainActivity::class.java), 0)

        val style = NotificationCompat.BigTextStyle()
        style.bigText("D/OpenGLRenderer: endAllStagingAnimators on 0x7f627d8000 (ListView) with handle 0x7f6d9bdd80\n" +
                "04-27 15:12:27.835 7069-7069/hit.sun.scarlet.logsmonitor D/lenovotintstatusbar: Check tint and icon theme, false, false")
        style.setBigContentTitle("服务器正遭受攻击！")

        val mBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.abc_ic_go_search_api_material)
                .setContentTitle("My notification")
                .setAutoCancel(true)
                .setStyle(style)
                .setContentText("完整类名")
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(pIntent)
                .setFullScreenIntent(pIntent, true)

        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mNotificationManager.notify(10086, mBuilder.build())
    }
}
