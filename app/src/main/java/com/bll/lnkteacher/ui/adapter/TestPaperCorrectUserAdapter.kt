package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.testpaper.TestPaperClassUserList
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TestPaperCorrectUserAdapter(layoutResId: Int,val type:Int,val questionType:Int, data: List<TestPaperClassUserList.ClassUserBean>?) : BaseQuickAdapter<TestPaperClassUserList.ClassUserBean, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder, item: TestPaperClassUserList.ClassUserBean) {
        val score=if (type==1){
            if (item.status==2) DataBeanManager.getResultStandardStr(item.score,questionType) else ""
        }
        else{
            if (item.status==2) item.score.toString() else ""
        }

        helper.setText(R.id.tv_score,score)
        helper.setText(R.id.tv_name,item.name)

        helper.setBackgroundRes(R.id.tv_name,if (item.isCheck) R.drawable.bg_black_solid_5dp_corner else R.drawable.bg_gray_stroke_5dp_corner)

        val colorRes=if (item.isCheck){
            mContext.getColor(R.color.white)
        }
        else{
            if (item.status==3){
                mContext.getColor(R.color.gray)
            }
            else{
                mContext.getColor(R.color.black)
            }
        }
        helper.setTextColor(R.id.tv_name,colorRes)
    }

}
