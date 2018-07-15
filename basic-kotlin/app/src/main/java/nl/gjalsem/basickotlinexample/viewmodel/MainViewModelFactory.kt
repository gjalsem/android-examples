package nl.gjalsem.basickotlinexample.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.android.volley.toolbox.Volley
import nl.gjalsem.basickotlinexample.model.RpcClient

class MainViewModelFactory(val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val rpcClient = RpcClient(Volley.newRequestQueue(context))
        return MainViewModel(rpcClient) as T
    }
}
