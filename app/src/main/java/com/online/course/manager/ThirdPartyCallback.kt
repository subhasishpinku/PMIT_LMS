package com.online.course.manager

import com.online.course.model.BaseResponse
import com.online.course.model.Data
import com.online.course.model.Response
import com.online.course.model.ThirdPartyLogin

interface ThirdPartyCallback {
    fun onThirdPartyLogin(res: Data<Response>, provider: Int, thirdPartyLogin: ThirdPartyLogin)
    fun onErrorOccured(error: BaseResponse)
}