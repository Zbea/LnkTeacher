package com.bll.lnkteacher.mvp.presenter

import android.util.Pair
import com.bll.lnkteacher.mvp.model.exam.ExamClassUserList
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*

/**
 * 考卷批改详细功能
 */
class ExamAnalyseTeachingPresenter(view: IContractView.IExamAnalyseTeachingView, val screen:Int):BasePresenter<IContractView.IExamAnalyseTeachingView>(view) {

    fun getExamPaperClassPapers(id:Int,classId:Int){
        val map=HashMap<String,Any>()
        map["schoolExamJobId"]=id
        map["classId"]=classId
        val list = RetrofitManager.service.getExamClass(map)
        doRequest(list, object : Callback<ExamClassUserList>(view,screen) {
            override fun failed(tBaseResult: BaseResult<ExamClassUserList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<ExamClassUserList>) {
                view.onClassPapers(tBaseResult.data)
            }
        }, true)
    }

    /**
     * 创建子群
     */
    fun createGroupChild(classId: Int, name: String,ids:List<Int>) {

        val body = RequestUtils.getBody(
            Pair.create("id", classId),
            Pair.create("name", name),
            Pair.create("studentIds", ids.toIntArray())
        )
        val list = RetrofitManager.service.createClassGroupChild(body)
        doRequest(list, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<Any>) {
                view.onCreateSuccess()
            }
        }, true)
    }

    /**
     * 刷新子群
     */
    fun refreshGroupChild(id: Int,classId: Int,ids:List<Int>) {

        val body = RequestUtils.getBody(
            Pair.create("classGroupId", id),
            Pair.create("classId", classId),
            Pair.create("studentIds", ids.toIntArray())
        )
        val list = RetrofitManager.service.refreshClassGroupChild(body)
        doRequest(list, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<Any>) {
                view.onRefreshSuccess()
            }
        }, true)
    }

}