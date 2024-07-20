package com.bll.lnkteacher.mvp.presenter

import android.util.Pair
import com.bll.lnkteacher.mvp.model.testpaper.TypeList
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*

/**
 * 考卷布置
 */
class TestPaperAssignPresenter(view: IContractView.ITestPaperAssignView) : BasePresenter<IContractView.ITestPaperAssignView>(view) {

    /**
     * 获取分类
     */
    fun getType(map: HashMap<String, Any>) {

        val type = RetrofitManager.service.getPaperType(map)
        doRequest(type, object : Callback<TypeList>(view) {
            override fun failed(tBaseResult: BaseResult<TypeList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<TypeList>) {
                if (tBaseResult.data!=null){
                    view.onType(tBaseResult.data)
                }
            }
        }, false)
    }

    fun addType(name:String,grade:Int){
        val body= RequestUtils.getBody(
            Pair.create("name",name),
            Pair.create("type",1),
            Pair.create("grade",grade)
        )
        val add = RetrofitManager.service.addPaperType(body)
        doRequest(add, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onTypeSuccess()
            }
        }, true)
    }

    /**
     * 删除作业分类
     */
    fun deleteType(map: HashMap<String,Any>) {
        val body=RequestUtils.getBody(map)
        val type = RetrofitManager.service.deleteHomeworkType(body)
        doRequest(type, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onDeleteSuccess()
            }
        }, true)
    }

}