package com.example.myalarmclock

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class TimeAlertDialog : DialogFragment() {
    interface Listener {
        fun getUp()
        fun snooze()
    }
    //変数listenerにListenerインターフェイスを実装したクラスだけを格納できるようになる。
    private var listener: Listener? = null

    //onAttachメソッドは、アクティビティからフラグメントが呼ばれた時に呼び出される。
    override fun onAttach(context: Context) {
        //引数contextに、このTimeAlertDialogを呼び出したアクティビティのコンテキストが格納されているので、
        super.onAttach(context)
        //受け取ったcontextがListenerインターフェイスを持っているアクティビティかどうかをチェックしておきましょう。
        //もしListenerインターフェイスを持っているなら、listener変数に入れておきます。
        when (context) {
            is Listener -> listener = context
        }
    }
    //DialogFragmentクラスのonCreateDialogメソッドをオーバーライドして、内部でダイアログを作成し、戻り値として返す。
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //表示するダイアログの内容は、まずAlertDialog.Builderのインスタンスを作成し、それを使って設定していく。
        //requireActivityメソッドで、フラグメントを呼び出したアクティビティを取得する
        val builder = AlertDialog.Builder(requireActivity())
        //AlertDialog.BuilderクラスのsetMessageで、「時間になりました!」という文字列をダイアログに表示している。
        builder.setMessage("時間になりました！")
        //ダイアログにボタンを表示したい場合は、setPositiveButton,setNegativeButtonメソッドを使う。
        //この2つのメソッドの機能は同じで、ボタンを1つだけ使う場合はsetPositiveButtonを、2つ使う場合は両方のメソッドを使う。
        //setPositiveButtonメソッドはダイアログに表示する1番目のボタンを設定 textにはボタンに表示する文字列を設定
        //{}内のdialogはタップが発生したダイアログが渡される。whichはタップが発生したボタンの種類が渡される。ボタンの種類はDialogInterfaceで定義された定数で、
        //setPositiveButtonの場合はBUTTON_POSITIVEになる。
        //タップされた後の処理をラムダ式で記述している。起きるボタンを表示し、ボタンをタップするとlistenerに保持しているアクティビティの
        //getUpメソッドを呼び出し(コールバック)しています。
        builder.setPositiveButton("起きる") { dialog, which ->
            listener?.getUp()
        }
        //setNegativeButtonメソッドはダイアログに表示する1番目のボタンを設定 textにはボタンに表示する文字列を設定
        //タップされた後の処理をラムダ式で記述している。あと5分ボタンを表示し、ボタンをタップするとlistenerに保持しているアクティビティの
        //snoozeメソッドを呼び出し(コールバック)しています。
        builder.setNegativeButton("あと5分") { dialog, which ->
            listener?.snooze()
        }
        //BuilderのcreateメソッドでAlartDialogオブジェクトを生成して返している。onCreateDialogでは必ず、
        // ダイアログのオブジェクトを返さなければならない
        return builder.create()
    }
}