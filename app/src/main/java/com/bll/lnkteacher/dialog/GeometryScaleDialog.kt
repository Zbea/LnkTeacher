package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.R
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.KeyboardUtils
import com.bll.lnkteacher.utils.SToast

class GeometryScaleDialog(val context: Context, val currentGeometry: Int,val type:Int,val location:Int) {

    fun builder(): GeometryScaleDialog? {

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_geometry_scale)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val window=dialog.window!!
        val layoutParams=window.attributes
        layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
        if (location==1){
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.LEFT
        }
        layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,460f))/2
        dialog.show()

        val btn_ok = dialog.findViewById<TextView>(R.id.tv_ok)
        val btn_cancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val et_width = dialog.findViewById<EditText>(R.id.et_width)
        val et_height = dialog.findViewById<EditText>(R.id.et_height)
        when(currentGeometry){
            1->{
                et_width.hint = "输入直线距离(mm)"
                et_height.visibility= View.GONE
            }
            2->{
                et_width.hint = "输入矩形宽度(mm)"
                et_height.hint = "输入矩形高度(mm)"
            }
            3->{
                if (type==0){
                    et_width.hint = "输入圆半径(mm)"
                }
                else{
                    et_width.hint = "输入圆直径(mm)"
                }
                et_height.visibility= View.GONE
            }
            5->{
                et_width.hint = "输入椭圆半宽度(mm)"
                et_height.hint = "输入椭圆半高度(mm)"
            }
            7->{
                et_width.hint = "输入抛物线大小"
                et_height.visibility= View.GONE
            }
            8->{
                et_width.hint = "输入角度"
                et_height.visibility= View.GONE
            }
            9->{
                et_width.hint = "输入每格刻度"
                et_height.visibility= View.GONE
            }
        }


        btn_cancel.setOnClickListener {
            dialog.dismiss()
        }
        btn_ok.setOnClickListener {
            val width = et_width.text.toString()
            val height=et_height.text.toString()
            if (width.isNotEmpty()) {
                dialog.dismiss()
                if (currentGeometry==2||currentGeometry==5||currentGeometry==9){
                    if (height.isNotEmpty()){
                        dialog.dismiss()
                        listener?.onClick(width.toFloat(),height.toFloat())
                    }
                }
                else{
                    val num=width.toFloat()
                    if (currentGeometry==8&&num>360){
                        SToast.showText(location,"角度需要小于360°")
                    }
                    else{
                        dialog.dismiss()
                        listener?.onClick(num,0f)
                    }
                }
            }
        }
        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }

        return this
    }

    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(width: Float,height:Float)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

}