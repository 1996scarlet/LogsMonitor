package hit.sun.scarlet.logsmonitor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.*


class MainActivity : AppCompatActivity() {

    private val todayPath = "${Environment.getExternalStorageDirectory().path}/LogsMonitor/OldLogs/${Date(System.currentTimeMillis()).stringFormat("yyyy-MM-dd")}.log"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermission()

        initDictionary()

        start.setOnClickListener { initDownLoad() }
        today.setOnClickListener {
            startActivity(Intent(applicationContext, DetailsActivity::class.java).putExtra("path", todayPath))
        }

        val startIntent = Intent(this, ListeningService::class.java)
        startService(startIntent)
    }

    private fun initDictionary() {
        val oldLogsDictionary = File("${Environment.getExternalStorageDirectory().path}/LogsMonitor/OldLogs")

        val oldLogsList = ArrayList<String>()

        if (!oldLogsDictionary.exists()) {
            oldLogsDictionary.mkdirs()
            initDownLoad()
        } else oldLogsDictionary.listFiles()
                .filter { it.absolutePath.endsWith(".log") }
                .forEach { oldLogsList.add(it.absolutePath.split("/").last()) }

        oldLogsList.sort()

        old_list.apply {

            adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, oldLogsList)
            onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                startActivity(Intent(context, DetailsActivity::class.java).putExtra("path", "${Environment.getExternalStorageDirectory().path}/LogsMonitor/OldLogs/${oldLogsList[position]}"))
            }
        }
    }

    private fun initDownLoad() {

        Toast.makeText(applicationContext, "已启动后台下载 请稍后重启APP", Toast.LENGTH_LONG).show()
        val baseUrl = "http://yppf.hljda.gov.cn/images/log/maltrail/"

        val urlList = ArrayList<String>()
        var begin = Date(117, 8, 19)
        val end = Date(System.currentTimeMillis())

        while (begin < end) {
            urlList.add(baseUrl + begin.stringFormat("yyyy-MM-dd") + ".log")
            begin += 1
        }

        FileDownloader.setup(applicationContext)

        for (url in urlList) {
            FileDownloader.getImpl().create(url)
                    .setPath("${Environment.getExternalStorageDirectory().path}/LogsMonitor/OldLogs/" + url.split("/").last())
                    .setCallbackProgressTimes(0) // 由于是队列任务, 这里是我们假设了现在不需要每个任务都回调`FileDownloadListener#progress`, 我们只关系每个任务是否完成, 所以这里这样设置可以很有效的减少ipc.
                    .setListener(queueTarget)
                    .setForceReDownload(false)
                    .asInQueueTask()
                    .enqueue()
        }

        FileDownloader.getImpl().start(queueTarget, false)
    }

    private fun checkPermission() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show()
            }
            //申请权限
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 10086)

        } else {
            Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show()
        }
    }

    val queueTarget: FileDownloadListener = object : FileDownloadListener() {
        override fun pending(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {}

        override fun connected(task: BaseDownloadTask?, etag: String?, isContinue: Boolean, soFarBytes: Int, totalBytes: Int) {}

        override fun progress(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {}

        override fun blockComplete(task: BaseDownloadTask?) {}

        override fun retry(task: BaseDownloadTask?, ex: Throwable?, retryingTimes: Int, soFarBytes: Int) {}

        override fun completed(task: BaseDownloadTask) {
        }

        override fun paused(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {}

        override fun error(task: BaseDownloadTask, e: Throwable) {}

        override fun warn(task: BaseDownloadTask) {}
    }
}
