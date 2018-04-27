package hit.sun.scarlet.logsmonitor

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_details.*
import java.io.File
import java.util.*

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val path = intent.getStringExtra("path")

        val filePath = File(path)

        if (filePath.exists()) {
            showLogFile(filePath)
        } else {
            Toast.makeText(applicationContext, "今天的日志尚未下载 请点击重新下载按钮", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun showLogFile(filePath: File) {

        val logLines = filePath.readText().split("\n").dropLast(1)

        val logItems = ArrayList<String>()

        logLines.forEach {
            val cells = it.split(" ")

            val logString = "攻击时间：${cells[0]} ${cells[1]}\n攻击IP-端口：${cells[3]}-${cells[4]}\n攻击类型：${cells[7]} ${cells[8]} ${cells[10]}\n"

            logItems.add(logString)
        }

        log_list.apply {
            adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, logItems)
        }
    }
}
