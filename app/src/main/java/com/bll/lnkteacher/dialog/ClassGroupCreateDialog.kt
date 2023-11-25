package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.utils.KeyboardUtils

class ClassGroupCreateDialog(val context: Context,var classGroup:ClassGroup?) {

    private var grade=0
    private var job=0
    private var tv_grade:TextView?=null
    private var tv_job:TextView?=null
    private var jobBeans= mutableListOf<PopupBean>()
    private var grades= mutableListOf<PopupBean>()

    fun builder(): ClassGroupCreateDialog {

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_classgroup_create)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        val et_name = dialog.findViewById<EditText>(R.id.et_name)
        val btn_ok = dialog.findViewById<TextView>(R.id.tv_ok)
        val btn_cancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        tv_grade = dialog.findViewById(R.id.tv_grade)
        tv_job = dialog.findViewById(R.id.tv_job)

        btn_ok.text=if (classGroup==null) "确定" else "修改"

        grades=if (classGroup==null) DataBeanManager.popupGrades else DataBeanManager.popupGrades(classGroup?.grade!!)
        for (i in DataBeanManager.groupJobs.indices){
            jobBeans.add(PopupBean(i+1,DataBeanManager.groupJobs[i],if (classGroup==null) false else i==classGroup?.job!!-1))
        }

        if (classGroup!=null)
        {
            grade=classGroup?.grade!!
            job=classGroup?.job!!
            et_name.setText(classGroup?.name)
            tv_grade?.text=DataBeanManager.getGradeStr(classGroup?.grade!!)
            tv_job?.text=if (classGroup?.job==1) context.getString(R.string.classGroup_headteacher) else context.getString(R.string.classGroup_teacher)
        }

        tv_grade?.setOnClickListener {
            PopupRadioList(context, grades, tv_grade!!,tv_grade?.width!!,  5).builder().setOnSelectListener { item ->
                tv_grade?.text=item.name
                grade=item.id
            }
        }

        tv_job?.setOnClickListener {
            PopupRadioList(context, jobBeans, tv_job!!,tv_job?.width!!,  5).builder().setOnSelectListener { item ->
                tv_job?.text=item.name
                job=item.id
            }
        }

        btn_cancel.setOnClickListener {
            dialog.dismiss()
        }
        btn_ok.setOnClickListener {
            val name=et_name.text.toString()
            if (name.isNotEmpty()&&grade>0)
            {
                dialog.dismiss()
                listener?.onClick(name,grade,job)
            }
        }

        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }
        return this
    }

    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(str: String,grade:Int,job:Int)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener) {
        this.listener = listener
    }

}