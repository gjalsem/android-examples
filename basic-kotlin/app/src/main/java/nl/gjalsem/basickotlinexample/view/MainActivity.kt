package nl.gjalsem.basickotlinexample.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import nl.gjalsem.basickotlinexample.R
import nl.gjalsem.basickotlinexample.model.LoadingState
import nl.gjalsem.basickotlinexample.model.MainState
import nl.gjalsem.basickotlinexample.viewmodel.MainViewModel
import nl.gjalsem.basickotlinexample.viewmodel.MainViewModelFactory

/**
 * The app's main activity showing the list of fetched data.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: InfoItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this, MainViewModelFactory(this))
                .get(MainViewModel::class.java)

        adapter = InfoItemAdapter(viewModel.imageLoader)
        recyclerView.adapter = adapter

        viewModel.stateData.observe(this, Observer { state -> state?.let { update(it) } })
    }

    private fun update(state: MainState) {
        adapter.items = state.infoItems

        errorView.visibility =
                if (state.loadingState == LoadingState.ERROR) View.VISIBLE else View.GONE

        loadingView.visibility =
                if (state.loadingState == LoadingState.LOADING) View.VISIBLE else View.GONE
    }
}
