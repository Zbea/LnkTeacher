package com.bll.lnkteacher.mvp.presenter

import android.util.Pair
import com.bll.lnkteacher.mvp.model.testpaper.TestPaperClassUserList
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*

/**
 * 考卷批改详细功能
 */
class TestPaperAnalyseTeachingPresenter(view: IContractView.IAnalyseTeachingView, val screen:Int):BasePresenter<IContractView.IAnalyseTeachingView>(view) {

    fun getClassPapers(taskId:Int){
        val map=HashMap<String,Any>()
        map["examChangeId"]=taskId
        map["size"]=100
        val list = RetrofitManager.service.getPaperCorrectClassList(map)
        doRequest(list, object : Callback<TestPaperClassUserList>(view,screen) {
            override fun failed(tBaseResult: BaseResult<TestPaperClassUserList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<TestPaperClassUserList>) {
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