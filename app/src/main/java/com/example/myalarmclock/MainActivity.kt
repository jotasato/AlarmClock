package com.example.myalarmclock

import android.app.AlarmManager
import android.app.KeyguardManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.WindowManager.LayoutParams.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), TimeAlertDialog.Listener
    //DatePickerFragmentとTimePickerFragmentで定義したインターフェイスを実装
    , DatePickerFragment.OnDateSelectedListener
    , TimePickerFragment.OnTimeSelectedListener {

    //日付選択ダイアログの選択が終了したら呼ばれるところ。
    override fun onSelected(year: Int, month: Int, date: Int) {
        //カレンダーオブジェクトを作成。
        val c = Calendar.getInstance()
        //ダイアログで選択された年、月、日を設定
        c.set(year, month, date)
        //文字列に変換してテキストビューに表示しています。
        //DateFormatクラスメソッドであるformatメソッドを使って、Calendarオブジェクトを
        //フォーマット済み文字列に変換している。
        dateText.text = DateFormat.format("yyyy/MM/dd", c)
    }

    //時刻選択ダイアログの選択が終了したら呼ばれるところ。
    //メソッドの中では選択された時と分をtimeTextに設定する
    override fun onSelected(hourOfDay: Int, minute: Int) {
        timeText.text = "%1$02d:%2$02d".format(hourOfDay, minute)
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
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 -> {
                    //アクティビティが終了した時に画面を表示するかどうか。trueの場合Acticityをロック画面上に表示
                    setShowWhenLocked(true)
                    //アクティビティが再開した時に画面をONにするかどうか。trueの場合画面をONにする
                    setTurnScreenOn(true)
                    //キーボードの解除にKeyguardManagerクラスのインスタンスを取得する必要がある
                    val keyguardManager =
                        getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                    //デバイスのキーガード（ロック画面を解除している）
                    keyguardManager.requestDismissKeyguard(this, null)
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                    window.addFlags(
                        FLAG_TURN_SCREEN_ON or FLAG_SHOW_WHEN_LOCKED
                    )
                    val keyguardManager =
                        getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                    keyguardManager.requestDismissKeyguard(this, null)
                }
                else ->
                    //addFlagsメソッドデフラグに従って、Windowのフラグを設定する
                    window.addFlags(
                        FLAG_TURN_SCREEN_ON or FLAG_SHOW_WHEN_LOCKED or FLAG_DISMISS_KEYGUARD
                    )
            }
            //TimeAlertDialogのインスタンスを生成し、showメソッドで表示している。
            val dialog = TimeAlertDialog()
            dialog.show(supportFragmentManager, "alert_dialog")
        }
        setContentView(R.layout.activity_main)


        //アラームをセットするボタンが押されたら、ダイアログで選択した日付、時刻でアラームを設定する。
        setAlarm.setOnClickListener {
            //各テキストビューdateText(日付)、timeText(時刻)を取り出しDate型に変換して変数dateに代入している。
            //toDate()という関数は下の方で定義してあるString型の拡張関数
            val date = "${dateText.text} ${timeText.text}".toDate()
            when {
                //変数dateがnullでない場合は、アラームをセットされましたのメッセージを表示し、
                //nullの場合はエラーメッセージを表示するようにしている。
                date != null -> {
                    //Calendarクラスのインスタンスは、getInstanceメソッドで取得する必要があります。
                    val calendar = Calendar.getInstance()
                    calendar.time = date
                    //Calendarクラスのインスタンスである変数calendarをsetAlarmManagerに渡している。
                    setAlarmManager(calendar)
                    Toast.makeText(
                        this, "アラームをセットしました",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    Toast.makeText(
                        this, "日付の形式が正しくありません",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        cancelAlarm.setOnClickListener {
            cancelAlarmManager()
        }

        //dateTextをタップした時、日付選択ダイアログを表示。選択が終了したらonSelectedメソッドが呼ばれる
        dateText.setOnClickListener {
            val dialog = DatePickerFragment()
            dialog.show(supportFragmentManager, "data_dialog")
        }

        //timeTextをタップした時、時刻選択ダイアログを表示。選択が終了したらonSelectedメソッドが呼ばれる
        timeText.setOnClickListener {
            val dialog = TimePickerFragment()
            dialog.show(supportFragmentManager, "time_dialog")
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
                    calendar.timeInMillis, null
                )
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
                am.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pending)
            }
            else -> {
                ////それ以前の場合の処理。setメソッドを使っている。使い方はsetExactと同じ
                am.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis, pending
                )
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

    private fun String.toDate(pattern: String = "yyyy/MM/dd HH:mm"): Date? {
        return try {
            //Dateに変換したい文字列の形式を指定してSimpleDateFormatクラスのインスタンスを生成し、
            //parseメソッドに文字列フォーマットを渡して、Date型に変換している。
            SimpleDateFormat(pattern).parse(this)
            //文字列が意図した形式ではない場合などにはIllegalArgumentException例外、ParseException例外が発生しますので、
            //try-catch処理を入れる必要があります。
            //拡張関数は自分自身を処理するので、その内部では拡張するオブジェクトをthisとして参照できることに注意する。
        } catch (e: IllegalArgumentException) {
            return null
        } catch (e: ParseException) {
            return null
        }
    }

}
