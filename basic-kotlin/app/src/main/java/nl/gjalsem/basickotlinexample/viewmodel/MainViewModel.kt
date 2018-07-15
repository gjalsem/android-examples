package nl.gjalsem.basickotlinexample.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import nl.gjalsem.basickotlinexample.model.LoadingState
import nl.gjalsem.basickotlinexample.model.MainState
import nl.gjalsem.basickotlinexample.model.RpcClient

class MainViewModel(val rpcClient: RpcClient) : ViewModel() {
    val stateData = MutableLiveData<MainState>()

    init {
        stateData.value = MainState(listOf(), LoadingState.LOADING)
        rpcClient.fetch { state -> stateData.value = state }
    }

    override fun onCleared() {
        rpcClient.cancel()
    }
}
