package com.bll.lnkteacher.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.homework.HomeworkClassSelectItem
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.KeyboardUtils
import com.bll.lnkteacher.widget.SpaceItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkPublishDialog(val context: Context,val grade: Int,val typeId:Int) {

    private var endTime=0L
    private var isCommit=false
    private var classPops= mutableListOf<PopupBean>()

    @SuppressLint("SuspiciousIndentation")
    fun builder(): HomeworkPublishDialog {

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_homework_publish)
        dialog.setCanceledOnTouchOutside(false)
        val window=dialog.window!!
            window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,600f))/2
        dialog.show()

        val tv_send = dialog.findViewById<TextView>(R.id.tv_ok)
        val tv_cancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val cb_commit=dialog.findViewById<CheckBox>(R.id.cb_commit)
        val tv_date = dialog.findViewById<TextView>(R.id.tv_date)
        val etContent = dialog.findViewById<EditText>(R.id.et_content)
        val rvList=dialog.findViewById<RecyclerView>(R.id.rv_list)

        val classSelectBean=MethodManager.getCommitClass(typeId)
        endTime=System.currentTimeMillis()+Constants.dayLong
        tv_date.text=DateUtils.longToStringWeek(endTime)
        classPops=MethodManager.getCommitClassGroupPops(grade, typeId)

        if (classSelectBean!=null){
            if (classSelectBean.isCommit){
                isCommit=classSelectBean.isCommit
                cb_commit.isChecked=isCommit
            }
        }

        cb_commit.setOnCheckedChangeListener { compoundButton, b ->
            isCommit=b
        }

        tv_date.setOnClickListener {
            CalendarSingleDialog(context).builder().setOnDateListener{
                if (it>System.currentTimeMillis()){
                    endTime=it
                    tv_date.text=DateUtils.longToStringWeek(endTime)
                }
            }
        }

        val mAdapter= MyAdapter(R.layout.item_publish_classgroup_selector, classPops)
        rvList.layoutManager = LinearLayoutManager(context)//创建布局管理
        rvList.adapter = mAdapter
        mAdapter.bindToRecyclerView(rvList)
        rvList.addItemDecoration(SpaceItemDeco(20))
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            val item=classPops[position]
            when (view.id){
                R.id.cb_class->{
                    item.isCheck=!item.isCheck
                    mAdapter.notifyItemChanged(position)
                }
            }
        }

        tv_cancel.setOnClickListener {
            dialog.dismiss()
        }

        tv_send.setOnClickListener {
            val contentStr = etContent.text.toString()
            if (contentStr.isNotEmpty()) {
                val classIds= mutableListOf<Int>()
                for (item in classPops){
                    if (item.isCheck){
                        classIds.add(item.id)
                    }
                }
                if (classIds.isNotEmpty())
                {
                    val classSelect= HomeworkClassSelectItem()
                    classSelect.classIds=classIds
                    classSelect.commitDate=if (isCommit) endTime else 0L
                    classSelect.isCommit=isCommit
                    listener?.onSend(contentStr,classSelect)
                    dialog.dismiss()
                }
            }
        }

        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }

        return this
    }

    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onSend(contentStr:String,classSelect: HomeworkClassSelectItem)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

    class MyAdapter(layoutResId: Int, data: List<PopupBean>?) : BaseQuickAdapter<PopupBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: PopupBean) {
            helper.setText(R.id.cb_class,"  "+item.name)
            helper.addOnClickListener(R.id.cb_class)
        }
    }

}