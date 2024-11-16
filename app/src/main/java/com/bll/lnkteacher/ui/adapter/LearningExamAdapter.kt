package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R.id.tv_date_commit
import com.bll.lnkteacher.R.id.tv_date_create
import com.bll.lnkteacher.R.id.tv_exam_class
import com.bll.lnkteacher.R.id.tv_exam_type
import com.bll.lnkteacher.mvp.model.exam.ExamList
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class LearningExamAdapter(layoutResId: Int, data: List<ExamList.ExamBean>?) : BaseQuickAdapter<ExamList.ExamBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ExamList.ExamBean) {
        helper.setText(tv_exam_type,item.examName)
        helper.setText(tv_date_create,"考试时间："+ DateUtils.longToStringWeek(DateUtils.dateStrToLong(item.createTime)))
        helper.setText(tv_date_commit,"提交时间："+ DateUtils.longToStringNoYear1(item.endTime))
        if (!item.classList.isNullOrEmpty()){
            var classStr=""
            for(ite in item.classList){
                classStr+=ite.className+"   "
            }
            helper.setText(tv_exam_class, "布置班级：$classStr")
        }
    }

}
