package com.example.myalarmclock

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity() : AppCompatActivity(), TimeAlertDialog.Listener, Parcelable {
    constructor(parcel: Parcel) : this() {
    }

    //getUpメソッドは、ダイアログで「起きる」ボタンが押された時に呼ばれる。
    override fun getUp() {
        //finishメソッドでアクティビティを閉じる。
        finish()
    }

    //snoozeメソッドは、ダイアログで「あと5分」ボタンが押された時に呼ばれる。ここではToastを使って後5分がクリックされましたというメッセージを表示している。
    override fun snooze() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.MINUTE, 5)
        setAlarmManager(calendar)
        finish()

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //インテントのエクストラとして"onReceive"にtrueが指定されているかチェックしています。
        //アクティビティの起動に使用されたインテントの取得はgetIntentメソッドで行いますが、Kotlinではintentプロパティが利用できます。
        if (intent?.getBooleanExtra("onReceive", false) == true) {
            //TimeAlertDialogのインスタンスを生成し、showメソッドで表示している。
            val dialog = TimeAlertDialog()
            dialog.show(supportFragmentManager, "alert_dialog")
        }

        setContentView(R.layout.activity_main)


        //setAlarmボタンをタップした時にCalendarクラスで現在の時間より5秒後の時間を作成している。
        setAlarm.setOnClickListener {
            //Calendarクラスのインスタンスは、getInstanceメソッドで取得する必要があります。
            val calendar = Calendar.getInstance()
            //System.currentTimeMillis()で現在の時刻を取得。//.timeInMillisで取得した現在の時刻をミリ秒で設定
            calendar.timeInMillis = System.currentTimeMillis()
            //addメソッドはCalendarに設定している時刻を編集する。引数には編集したい場所と値を渡します。
            //今回の場合だと単位に秒を表すCalendar.SECONDを編集したいので指定して、値に5を渡すことで時刻を5秒進めている。
            //変数calendarには現在の時刻を秒数化したものが入っているので、現在時刻の5病後という設定になる。
            calendar.add(Calendar.SECOND, 5)
            //Calendarクラスのインスタンスである変数calendarをsetAlarmManagerに渡している。
            setAlarmManager(calendar)
        }

        cancelAlarm.setOnClickListener{
            cancelAlarmManager()
        }

    }

    private fun setAlarmManager(calendar: Calendar) {
        //AlarmManagerクラスのインスタンスを取得している。この処理はgetSystemServiceメソッドに引数「Context.ALARM_SERVICE」を渡して行う。
        //戻り値はAny型なので、AlarmManagerに変換する。
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        //アラーム時刻になった時にシステムから発行されるインテントを作成している。ここで呼び出すのはアクティビティではく、先ほど作成したAlarmBroadcastReceiverです。
        val intent = Intent(this, AlarmBroadcastReceiver::class.java)
        //AlarmManagerに登録するために、作成したインテントを指定してペンディングインテントを作成する。作成にはPendingIntentクラスのgetBroadcastメソッドを使う。
        //リクエスコードやフラグは今回仕様ないのであれば,0を渡しておく。
        val pending = PendingIntent.getBroadcast(this, 0, intent, 0)
        when {
            //Lollipop以上では、AlarmManage.AlarmClockInfoクラスのインスタンスを用意して、
            //設定するアラームの時刻とアラーム設定のためのインテントを指定する。今回はアラーム設定のためのインテントを使用しないので、第2引数にはnullを渡す。
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                val info = AlarmManager.AlarmClockInfo(
                    calendar.timeInMillis, null)
                //AlarmManager.AlarmClockInfoのインスタンスである変数infoをsetAlarmClockメソッドに渡す。
                //setAlarmClockメソッドでアラームを設定している。第1引数がアラーム情報で、第2引数がアラーム時刻になった時に実行するインテント。
                am.setAlarmClock(info, pending)
            }
            //API19から、それ以前に使用されていたsetメソッドによるアラームは正確ではなく、遅延して通知されるようになった。
            //そのためset代わりに、setExactを使用する。
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                //アラームのタイプには、RTC_WAKEUPを渡し、デバイスオフの場合にはデバイスを起動するようにしている。
                //setExactメソッドはアラームが正確に配信されるようにスケジュールします。第1引数がアラームのタイプ,
                // 第2引数が設定するアラームの時刻,第3引数がアラーム実行になった時に実行するインテント。
                am.setExact(AlarmManager.RTC_WAKEUP,calendar.timeInMillis, pending)
            }
            else -> {
                ////それ以前の場合の処理。setメソッドを使っている。使い方はsetExactと同じ
                am.set(AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis, pending)
            }
        }
    }

    //ペンディングインテントを取得するまでの処理は、アラーム登録の時と同じ。アラームキャンセルするにはAlarmManagerのcancelメソッドを使う。
    private fun cancelAlarmManager() {
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmBroadcastReceiver::class.java)
        val pending = PendingIntent.getBroadcast(this, 0, intent, 0)
        //引数にはキャンセルしたいインテントと同じものを渡す。//cancelメソッドはintentに一致するアラームを全て削除する。
        am.cancel(pending)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MainActivity> {
        override fun createFromParcel(parcel: Parcel): MainActivity {
            return MainActivity(parcel)
        }

        override fun newArray(size: Int): Array<MainActivity?> {
            return arrayOfNulls(size)
        }
    }

}
