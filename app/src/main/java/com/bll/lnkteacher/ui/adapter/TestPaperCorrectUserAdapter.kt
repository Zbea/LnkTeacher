package com.bll.lnkteacher.ui.adapter

import android.widget.TextView
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.testpaper.TestPaperClassUserList
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TestPaperCorrectUserAdapter(layoutResId: Int,val type:Int,val questionType:Int, data: List<TestPaperClassUserList.ClassUserBean>?) : BaseQuickAdapter<TestPaperClassUserList.ClassUserBean, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder, item: TestPaperClassUserList.ClassUserBean) {
        val score=if (item.status==2) " "+DataBeanManager.getResultStandardStr(item.score,questionType) else ""
        if (type==1){
            val tvScore=helper.getView<TextView>(R.id.tv_score)
            tvScore.textSize=22f
        }
        helper.setText(R.id.tv_score,score)
        helper.setText(R.id.tv_name,item.name)
        helper.setGone(R.id.iv_share,item.shareType==1)

        var colorRes=0
        var bgRes=0
        var shareResId=0

        if (item.isCheck){
            colorRes=mContext.getColor(R.color.white)
            bgRes=R.drawable.bg_black_solid_5dp_corner
            shareResId=R.mipmap.icon_share_type_white
        }
        else{
            shareResId=R.mipmap.icon_share_type_black
            if (item.status==3){
                colorRes=mContext.getColor(R.color.gray)
                bgRes=R.drawable.bg_gray_stroke_5dp_corner
            }
            else{
                colorRes=mContext.getColor(R.color.black)
                bgRes=R.drawable.bg_black_stroke_5dp_corner
            }
        }
        helper.setTextColor(R.id.tv_name,colorRes)
        helper.setBackgroundRes(R.id.tv_name,bgRes)
        helper.setImageResource(R.id.iv_share,shareResId)
    }

}
