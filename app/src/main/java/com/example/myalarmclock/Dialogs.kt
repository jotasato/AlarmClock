package com.example.myalarmclock

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import java.util.*

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

//DatePickerDialogを使う場合、DialogFragmentクラスを継承する他に、DatePickerDialog.OnDateSetListenerインターフェイスを実装します。
class DatePickerFragment : DialogFragment(),
    DatePickerDialog.OnDateSetListener {
    //インターフェイスを用意する処理は、TimeAlertDialogと同じです。このインターフェイスはアクティビティに実装して、
    //オーバーライドしたonSelectedメソッドに日付が選択された時の処理を記述する。
    interface OnDateSelectedListener {
        //onSelectedメソッドは年、月、日を引数にとる。
        fun onSelected(year: Int, month: Int, date: Int)
    }

    private var listener: OnDateSelectedListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        when (context) {
            is OnDateSelectedListener -> listener = context
        }
    }

    //onCreateDialogではDatePickerDialogのインスタンスを返すが、その前に現在の日付を初期値として設定している。
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //カレンダークラスのgetメソッドを使って、年、月、日の値を変数に取得している。
        val c = Calendar.getInstance()
        //.getメソッドでCalendarオブジェクトの各フィールドの値を返す。
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val date = c.get(Calendar.DAY_OF_MONTH)
        //DatePickerDialogクラスのコンストラクタ
        //第1引数はダイアログを表示する対象のコンテキスト。第2引数は、日付がセットされた時に呼ばれるリスナー。
        //第3引数は初期設定をする年、第4引数は初期設定をする月、第5引数は初期設定をする日
        return DatePickerDialog(requireActivity(), this, year, month, date)
    }

    //日付が選択された時に呼ばれる。
    override fun onDateSet(
        view: DatePicker?, year: Int,
        month: Int, dayOfMonth: Int
    ) {
        listener?.onSelected(year, month, dayOfMonth)
    }
}

//基本的な流れは、DatePickerDialogの場合と同じ
//TimePickerDialogを使う場合、DialogFragmentクラスを継承する他に、TimePickerDialog.OnTimeSetListenerインターフェイスを実装する

class TimePickerFragment : DialogFragment(),
    TimePickerDialog.OnTimeSetListener {
    interface OnTimeSelectedListener {
        fun onSelected(hourOfDay: Int, minute: Int)
    }

    private var listener: OnTimeSelectedListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        when (context) {
            is OnTimeSelectedListener -> listener = context
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        //TimePickerDialogのインスタンスを返している。
        //コンストラクタの第1引数は、ダイアログを表示する対象のアクティビティ、第2引数は、時刻がセットされた時に呼ばれるリスナー、
        //第3引数は初期設定する時、第4引数は初期設定する分、第5引数はtrueなら24時間表記、falseならAM/PM表記
        return TimePickerDialog(context, this, hour, minute, true)
    }

    //onTimeSetメソッドは、時刻が選択された時に呼ばれる。
    //第1引数viewはダイアログに関連老けられたTimePickerビュー、第2引数は時、第3引数は分
    //内部ではonSelectedメソッドを実行しているだけ。このメソッドの中身もアクティビティで実装する。
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        listener?.onSelected(hourOfDay, minute)
    }


}