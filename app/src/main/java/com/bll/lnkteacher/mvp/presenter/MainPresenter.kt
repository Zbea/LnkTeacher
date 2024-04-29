package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.FriendList
import com.bll.lnkteacher.mvp.model.Message
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.BasePresenter
import com.bll.lnkteacher.net.BaseResult
import com.bll.lnkteacher.net.Callback
import com.bll.lnkteacher.net.RetrofitManager

class MainPresenter(view: IContractView.IMainView, val screen:Int=0):BasePresenter<IContractView.IMainView>(view) {


    fun getList(map: HashMap<String,Any>){
        val list=RetrofitManager.service.getMessages(map)
        doRequest(list, object : Callback<Message>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Message>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Message>) {
                if (tBaseResult.data!=null)
                    view.onList(tBaseResult.data)
            }
        },false)
    }

    fun getFriends() {
        val editName = RetrofitManager.service.onFriendList()
        doRequest(editName, object : Callback<FriendList>(view) {
            override fun failed(tBaseResult: BaseResult<FriendList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<FriendList>) {
                if (tBaseResult.data!=null)
                    view.onListFriend(tBaseResult.data)
            }
        }, false)
    }

}