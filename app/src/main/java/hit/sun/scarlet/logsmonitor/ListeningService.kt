package hit.sun.scarlet.logsmonitor

import android.app.IntentService
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.Handler
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import java.io.File
import java.util.*
import kotlin.math.log


class ListeningService : IntentService("ListeningService") {

    private val todayPath = "${Environment.getExternalStorageDirectory().path}/LogsMonitor/OldLogs/${Date(System.currentTimeMillis()).stringFormat("yyyy-MM-dd")}.log"

    private var size: Long = 0

    override fun onHandleIntent(p0: Intent?) {

        val file = File(todayPath)

        if (file.exists()) size = file.length()

        while (true) {
            fatchNowLog()
            Thread.sleep(1 * 60 * 1000)
        }
    }

    private fun fatchNowLog() {
        Log.d("FileDownloader", "$todayPath $size")
        FileDownloader.setup(applicationContext)
        FileDownloader.getImpl().create("http://yppf.hljda.gov.cn/images/log/maltrail/" + Date(System.currentTimeMillis()).stringFormat("yyyy-MM-dd") + ".log")
                .setPath(todayPath)
                .setForceReDownload(true)
                .setListener(object : FileDownloadListener() {
                    override fun pending(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {}

                    override fun connected(task: BaseDownloadTask?, etag: String?, isContinue: Boolean, soFarBytes: Int, totalBytes: Int) {}

                    override fun progress(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {}

                    override fun blockComplete(task: BaseDownloadTask?) {}

                    override fun retry(task: BaseDownloadTask?, ex: Throwable?, retryingTimes: Int, soFarBytes: Int) {}

                    override fun completed(task: BaseDownloadTask) {


                        val file = File(todayPath)

                        Log.d("FileDownloader", "$todayPath $size ${file.length()}")


                        if (file.exists() && file.length() > size) {
                            size = file.length()
                            getNewAttackInfo()
                        }
                    }

                    override fun paused(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {}

                    override fun error(task: BaseDownloadTask, e: Throwable) {}

                    override fun warn(task: BaseDownloadTask) {}
                }).start()
    }


    private fun getNewAttackInfo() {

        val pIntent = PendingIntent.getActivity(this, 1, Intent(applicationContext, DetailsActivity::class.java).putExtra("path", todayPath), 0)

        val style = NotificationCompat.BigTextStyle()
        style.bigText("捕捉到了新的攻击日志 点击查看详情")
        style.setBigContentTitle("服务器正遭受攻击！")

        val mBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.abc_ic_go_search_api_material)
                .setContentTitle("My notification")
                .setAutoCancel(true)
                .setStyle(style)
                .setContentText("服务器正遭受攻击")
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(pIntent)
                .setFullScreenIntent(pIntent, true)

        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mNotificationManager.notify(10086, mBuilder.build())
    }
}
