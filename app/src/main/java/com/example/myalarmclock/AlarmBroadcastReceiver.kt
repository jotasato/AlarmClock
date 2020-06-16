package com.example.myalarmclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

//BroadcastReceiverクラス自体は非常にシンプルで、ブロードキャストインテントを受け取った時にonReceiveメソッドが実行され、
//引数のintentから受け取ったインテントの情報を取得することができる。
//BroadcastReceiver()を継承したクラスを作成している
class AlarmBroadcastReceiver : BroadcastReceiver() {
    //onReceiveメソッドはブロードキャストインテントを受け取った時に呼ばれる。
    override fun onReceive(context: Context, intent: Intent) {
        //onReceive内には通知を受け取った時の処理を記述。
        //Toastクラスを使ってウィンドウの前面に一定期間メッセージを表示させている。
        //Toastクラスの通常の使い方は、makeTextメソッドで表示内容を定義し、showメソッドで表示します。
        Toast.makeText(context, "アラームを受信しました", Toast.LENGTH_SHORT)
            .show()
    }
}
