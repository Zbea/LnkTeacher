package com.bll.lnkteacher.ui.adapter

import android.widget.TextView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.testpaper.ExamScoreItem
import com.bll.lnkteacher.widget.LongClickButton
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ExamScoreAdapter(layoutResId: Int, data: List<ExamScoreItem>?) : BaseQuickAdapter<ExamScoreItem, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ExamScoreItem) {
        helper.setText(R.id.tv_sort,item.sort.toString())
        val et_score=helper.getView<TextView>(R.id.et_score)
        val bt_up=helper.getView<LongClickButton>(R.id.bt_up)
        et_score.text=""
        var score=10
        if (item.score!=null){
            et_score.text=item.score
            score=item.score.toInt()
        }
        else{
            bt_up.setLongClickRepeatListener ({
                if (score<99){
                    score+=1
                    et_score.text= score.toString()
                    item.score=et_score.text.toString()
                }
            },400)
            bt_up.setOnClickListener {
                if (score<99){
                    score+=1
                    et_score.text= score.toString()
                    item.score=et_score.text.toString()
                }
            }
            val bt_down=helper.getView<LongClickButton>(R.id.bt_down)
            bt_down.setLongClickRepeatListener ({
                if (score>0){
                    score-=1
                    et_score.text= score.toString()
                    item.score=et_score.text.toString()
                }
            },400)
            bt_down.setOnClickListener {
                if (score>0){
                    score-=1
                    et_score.text= score.toString()
                    item.score=et_score.text.toString()
                }
            }
        }
    }

}
