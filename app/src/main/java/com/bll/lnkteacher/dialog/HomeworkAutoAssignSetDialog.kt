package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.homework.HomeworkAssignItem
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.SToast


class HomeworkAutoAssignSetDialog(private val context: Context,private var isAllowCorrect:Boolean,private var item:HomeworkAssignItem?) {
    private var dialog:Dialog?=null
    private var taskState=0
    private var isCorrect=false
    private var isCommit=false
    private var assignTime=0L
    private var commitTime=0L

    fun builder(): HomeworkAutoAssignSetDialog {
        dialog =Dialog(context)
        dialog?.setContentView(R.layout.dialog_homework_auto_assgin)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()

        val cancleTv = dialog?.findViewById<TextView>(R.id.tv_cancel)
        val okTv = dialog?.findViewById<TextView>(R.id.tv_ok)

        val tvCommitTime = dialog?.findViewById<TextView>(R.id.tv_commit_time)
        val tvAssignTime = dialog?.findViewById<TextView>(R.id.tv_assign_time)

        val cbShow= dialog?.findViewById<CheckBox>(R.id.cb_show)
        val cbCorrect = dialog?.findViewById<CheckBox>(R.id.cb_correct)
        val cbCommit = dialog?.findViewById<CheckBox>(R.id.cb_commit)
        if (item!=null){
            taskState=item!!.taskState
            isCorrect=item!!.selfBatchStatus==1
            isCommit=item!!.showStatus==0
            assignTime=item!!.assignTime*1000
            commitTime=item!!.endTime*1000
            if (isCommit){
                tvCommitTime?.visibility=if (isCommit) View.VISIBLE else View.GONE
            }
        }
        cbShow?.isChecked=taskState==2
        cbShow?.setOnCheckedChangeListener { compoundButton, b ->
            taskState = if (b) 2 else 1
        }

        cbCorrect?.isChecked=isCorrect
        if (!isAllowCorrect){
            cbCorrect?.isEnabled=false
            cbCorrect?.isChecked=false
        }
        cbCorrect?.setOnCheckedChangeListener { compoundButton, b ->
            isCorrect=b
        }

        cbCommit?.isChecked=isCommit
        cbCommit?.setOnCheckedChangeListener { compoundButton, b ->
            isCommit=b
            tvCommitTime?.visibility=if (isCommit) View.VISIBLE else View.GONE
        }

        tvAssignTime?.text=DateUtils.longToStringNoYear1(assignTime)
        tvAssignTime?.setOnClickListener {
            DateSelectorDialog(context).builder().setOnDateListener{ timeStr,timeLong->
                tvAssignTime.text=timeStr
                assignTime=timeLong
                commitTime=DateUtils.getStartOfDayInMillis(timeLong)+Constants.dayLong
                tvCommitTime?.text=DateUtils.longToStringWeek(commitTime)
            }
        }

        tvCommitTime?.text=DateUtils.longToStringWeek(commitTime)
        tvCommitTime?.setOnClickListener {
            CalendarSingleDialog(context).builder().setOnDateListener{
                commitTime=it
                tvCommitTime.text=DateUtils.longToStringWeek(commitTime)
            }
        }

        cancleTv?.setOnClickListener { dismiss() }
        okTv?.setOnClickListener {
            if (assignTime==0L){
                SToast.showText("自动发送时间未设置")
                return@setOnClickListener
            }
            if (assignTime<=System.currentTimeMillis()){
                SToast.showText("自动发送时间设置必须大于当前时间")
                return@setOnClickListener
            }
            if (isCommit&&commitTime<=assignTime){
                SToast.showText("提交时间必须大于发送时间")
                return@setOnClickListener
            }
            val item=HomeworkAssignItem()
            item.taskState=taskState
            item.selfBatchStatus=if (isCorrect) 1 else 0
            item.showStatus=if (isCommit)0 else 1
            item.assignTime=assignTime/1000
            item.endTime=if (commitTime>0) commitTime/1000 else 0
            listener?.onClick(item)
            dialog?.dismiss()
        }
        return this
    }


    fun show() {
        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }

    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(item: HomeworkAssignItem)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener) {
        this.listener = listener
    }
}