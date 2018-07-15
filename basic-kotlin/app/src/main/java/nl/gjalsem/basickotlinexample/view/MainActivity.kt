package nl.gjalsem.basickotlinexample.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import nl.gjalsem.basickotlinexample.R
import nl.gjalsem.basickotlinexample.viewmodel.MainViewModel
import nl.gjalsem.basickotlinexample.viewmodel.MainViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel

    private val adapter = InfoItemAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        recyclerView.adapter = adapter

        mainViewModel = ViewModelProviders.of(this, MainViewModelFactory(this))
                .get(MainViewModel::class.java)

        mainViewModel.stateData.observe(this, Observer { state ->
            adapter.items = state?.infoItems ?: listOf()
        })
    }
}
