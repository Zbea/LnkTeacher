package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.R.id.tv_date_commit
import com.bll.lnkteacher.R.id.tv_date_create
import com.bll.lnkteacher.R.id.tv_exam_class
import com.bll.lnkteacher.R.id.tv_exam_type
import com.bll.lnkteacher.R.id.tv_self_correct
import com.bll.lnkteacher.R.id.tv_title
import com.bll.lnkteacher.mvp.model.testpaper.CorrectBean
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class LearningHomeworkAdapter(layoutResId: Int, data: List<CorrectBean>?) : BaseQuickAdapter<CorrectBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: CorrectBean) {
        helper.setText(tv_title,item.title)
        helper.setText(tv_exam_type,item.subTypeName)
        helper.setText(tv_self_correct,if (item.selfBatchStatus==1)"自批" else "")
        helper.setText(tv_date_create,mContext.getString(R.string.teaching_assign_time)+"："+ DateUtils.longToStringWeek(DateUtils.dateStrToLong(item.createTime)))
        helper.setText(tv_date_commit,"提交时间："+ if (item.taskType==1) DateUtils.longToStringWeek(item.endTime) else DateUtils.longToStringNoYear1(item.endTime))
        if (!item.examList.isNullOrEmpty()){
            var classStr=""
            for(ite in item.examList){
                classStr+=ite.name+"   "
            }
            helper.setText(tv_exam_class, "布置班级：$classStr")
        }
    }


}
