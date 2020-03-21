package com.pallaw.flickrphotos.ui.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.pallaw.flickrphotos.R
import com.pallaw.flickrphotos.data.remote.ApiService
import com.pallaw.flickrphotos.data.remote.TheMovieDBClient
import com.pallaw.flickrphotos.data.repository.recent.RecentPhotoPagedListRepository
import com.pallaw.flickrphotos.util.NetworkState
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainActivityViewModel

    lateinit var recentPhotoRepository: RecentPhotoPagedListRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val apiService: ApiService = TheMovieDBClient.getClient()

        recentPhotoRepository =
            RecentPhotoPagedListRepository(
                apiService
            )

        viewModel = getViewModel()

        val photosAdapter = RecentPhotosPagedListAdapter(this)

        val gridLayoutManager = GridLayoutManager(this, 2)

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = photosAdapter.getItemViewType(position)
                if (viewType == photosAdapter.PHOTO_VIEW_TYPE) return 1    // Movie_VIEW_TYPE will occupy 1 out of 3 span
                else return 2                                             // NETWORK_VIEW_TYPE will occupy all 3 span
            }
        }


        rv_photo_list.layoutManager = gridLayoutManager
        rv_photo_list.setHasFixedSize(true)
        rv_photo_list.adapter = photosAdapter

        viewModel.recentPagedList.observe(this, Observer {
            photosAdapter.submitList(it)
        })

        viewModel.networkState.observe(this, Observer {
            progress_bar.visibility =
                if (viewModel.listIsEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE
            txt_error.visibility =
                if (viewModel.listIsEmpty() && it == NetworkState.ERROR) View.VISIBLE else View.GONE

            if (!viewModel.listIsEmpty()) {
                photosAdapter.setNetworkState(it)
            }
        })

    }


    private fun getViewModel(): MainActivityViewModel {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MainActivityViewModel(recentPhotoRepository) as T
            }
        })[MainActivityViewModel::class.java]
    }

}
