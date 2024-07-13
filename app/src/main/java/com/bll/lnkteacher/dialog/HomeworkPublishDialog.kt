package com.bll.lnkteacher.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.homework.HomeworkClassSelectItem
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.KeyboardUtils
import com.bll.lnkteacher.utils.SToast
import com.bll.lnkteacher.widget.SpaceItemDeco

class HomeworkPublishDialog(val context: Context,val grade: Int,val typeId:Int) {

    private var selectClasss= mutableListOf<HomeworkClassSelectItem>()
    private var initDatas= mutableListOf<HomeworkClassSelectItem>()

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

        val classs= DataBeanManager.getGradeClassGroups(grade)
        val homeworkClasss=MethodManager.getCommitClass(typeId)

        for (item in classs){
            initDatas.add(HomeworkClassSelectItem().apply {
                    className=item.name
                    classId=item.id
                    date= DateUtils.getStartOfDayInMillis()+ Constants.dayLong
            })
        }

        for (item in initDatas){
            for (selectItem in homeworkClasss){
                if (item.classId==selectItem.classId){
                    item.isCheck=selectItem.isCheck
                    item.isCommit=selectItem.isCommit
                }
            }
        }

        val tv_send = dialog.findViewById<TextView>(R.id.tv_ok)
        val tv_cancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val etContent = dialog.findViewById<EditText>(R.id.et_content)

        val rvList=dialog.findViewById<RecyclerView>(R.id.rv_list)
        val mAdapter= HomeworkPublishClassGroupSelectDialog.MyAdapter(
            R.layout.item_publish_classgroup_selector,
            initDatas
        )
        rvList.layoutManager = LinearLayoutManager(context)//创建布局管理
        rvList.adapter = mAdapter
        mAdapter.bindToRecyclerView(rvList)
        rvList.addItemDecoration(SpaceItemDeco(0,0,0, 20))
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            val item=initDatas[position]
            when (view.id){
                R.id.tv_date->{
                    CalendarSingleDialog(context).builder().setOnDateListener{
                        item.date=it
                        mAdapter?.notifyDataSetChanged()
                    }
                }
                R.id.cb_class->{
                    item.isCheck=!item.isCheck
                    if (!item.isCheck){
                        item.isCommit=false
                    }
                    mAdapter?.notifyDataSetChanged()
                }
                R.id.cb_commit->{
                    item.isCommit=!item.isCommit
                    mAdapter?.notifyDataSetChanged()
                }
            }

        }

        tv_cancel.setOnClickListener {
            dialog.dismiss()
        }

        tv_send.setOnClickListener {
            val contentStr = etContent.text.toString()
            selectClasss=getSelectClass()
            if (contentStr.isNotEmpty()) {
                if (selectClasss.isNotEmpty())
                {
                    dialog.dismiss()
                    listener?.onSend(contentStr,selectClasss)
                }
                else{
                    SToast.showText("未选中班级")
                }
            }
        }

        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }

        return this
    }

    /**
     * 得到选中的班级信息
     */
    private fun getSelectClass():MutableList<HomeworkClassSelectItem>{
        val items= mutableListOf<HomeworkClassSelectItem>()
        for (item in initDatas){
            if (item.isCheck){
                if (!item.isCommit)
                    item.date=0
                items.add(item)
            }
        }
        return items
    }

    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onSend(contentStr:String,classs:List<HomeworkClassSelectItem>)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

}