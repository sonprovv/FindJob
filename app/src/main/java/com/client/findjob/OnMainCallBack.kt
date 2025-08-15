package com.client.findjob

import java.util.Objects

interface OnMainCallBack {
    fun showFragment(tag:String, data: Objects?, isBack:Boolean,viewID:Int)
}
