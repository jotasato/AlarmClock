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
        //アクティビティを呼ぶためのインテントを作成。
        val mainIntent = Intent(context, MainActivity::class.java)
        //　ここで設定しているエクストラは、アクティビティがBroadcastReceiverから起動されたことがわかるようにするための情報。キー名は任意で、ここでは"onReceive"としている
            .putExtra("onReceive", true)
            //アクティビティから他のアクティビティを開く場合と違い、BroadcastReceiverからアクティビティを呼び出すには、インテントにIntent.FLAG_ACTIVITY_NEW_TASKフラグを
            //つけておく必要がある。このフラグはラスクがスタックに存在しても新しいタスクとしてアクティビティを起動するためのものですが、
            //今回はタスクやスタックについては深く考えず、決まりだと考える。
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        //最後にアクティビティを起動しますが、ここはActivityクラスの中ではないので、onReciveメソッドに渡されるContextを使って、startActivityメソッドを実行します。
        context.startActivity(mainIntent)

    }
}
