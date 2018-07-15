package nl.gjalsem.basickotlinexample.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import nl.gjalsem.basickotlinexample.model.BitmapCache
import nl.gjalsem.basickotlinexample.model.RpcClient

class MainViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val requestQueue = Volley.newRequestQueue(context)
        val rpcClient = RpcClient(requestQueue)
        val imageLoader = ImageLoader(requestQueue, BitmapCache())
        return MainViewModel(rpcClient, imageLoader) as T
    }
}
