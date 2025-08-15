package com.client.findjob.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.client.findjob.base.BaseViewModel
import com.client.findjob.data.model.CheckNotiMes
import com.client.findjob.data.repository.NotiMesRepo
import kotlinx.coroutines.launch

class NotiMesViewModel : BaseViewModel() {
    private val repository = NotiMesRepo()

    private val _checkResult = MutableLiveData<CheckNotiMes>()
    val checkResult: LiveData<CheckNotiMes> get() = _checkResult

    fun checkFirebase() {
        viewModelScope.launch {
            val response = repository.checkFirebase()
            if (response.isSuccessful) {
                _checkResult.postValue(response.body())
            }
        }
    }
}