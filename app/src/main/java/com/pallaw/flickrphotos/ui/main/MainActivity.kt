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
import com.pallaw.flickrphotos.data.remote.ApiClient
import com.pallaw.flickrphotos.data.repository.recent.RecentPhotoPagedListRepository
import com.pallaw.flickrphotos.data.repository.search.SearchPhotoPagedListRepository
import com.pallaw.flickrphotos.util.NetworkState
import com.pallaw.flickrphotos.util.SearchObservable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Predicate
import io.reactivex.observers.DisposableObserver
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_main.view.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainActivityViewModel

    val recentPhotoRepository: RecentPhotoPagedListRepository by lazy { RecentPhotoPagedListRepository(apiService) }
    val searchPhotoRepository: SearchPhotoPagedListRepository by lazy { SearchPhotoPagedListRepository(apiService) }
    val apiService: ApiService by lazy { ApiClient.getClient() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = getViewModel()

        //setup list to show items from recent API
        setupRecentPhotosList()

        //setup list to show  items from search API
        setupSearchPhotosList()


        //Observe search text changes to switch between search list and recent photo list
        observeSearchText()

    }

    private fun setupRecentPhotosList() {

        val photosAdapter = RecentPhotosPagedListAdapter(this)

        val gridLayoutManager = GridLayoutManager(this, 2)

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = photosAdapter.getItemViewType(position)
                if (viewType == photosAdapter.PHOTO_VIEW_TYPE) return 1
                else return 2
            }
        }

        recentList.rv_photo_list.layoutManager = gridLayoutManager
        recentList.rv_photo_list.setHasFixedSize(true)
        recentList.rv_photo_list.adapter = photosAdapter

        viewModel.recentPagedList.observe(this, Observer {
            photosAdapter.submitList(it)
        })

        viewModel.recentPhotosNetworkState.observe(this, Observer {
            progress_bar.visibility =
                if (viewModel.isRecentListEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE
            txt_error.visibility =
                if (viewModel.isRecentListEmpty() && it == NetworkState.ERROR) View.VISIBLE else View.GONE

            if (!viewModel.isRecentListEmpty()) {
                photosAdapter.setNetworkState(it)
            }
        })
    }

    private fun setupSearchPhotosList() {

        val photosAdapter = SearchPhotosPagedListAdapter(this)

        val gridLayoutManager = GridLayoutManager(this, 2)

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = photosAdapter.getItemViewType(position)
                if (viewType == photosAdapter.PHOTO_VIEW_TYPE) return 1
                else return 2
            }
        }

        searchList.rv_photo_list.layoutManager = gridLayoutManager
        searchList.rv_photo_list.setHasFixedSize(true)
        searchList.rv_photo_list.adapter = photosAdapter

        viewModel.searchPagedList?.observe(this, Observer {
            photosAdapter.submitList(it)
        })

        viewModel.searchPhotosNetworkState?.observe(this, Observer {
            progress_bar.visibility =
                if (viewModel.isSearchListEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE
            txt_error.visibility =
                if (viewModel.isSearchListEmpty() && it == NetworkState.ERROR) View.VISIBLE else View.GONE

            if (!viewModel.isSearchListEmpty()) {
                photosAdapter.setNetworkState(it)
            }
        })
    }

    private fun observeSearchText() {
        SearchObservable.fromView(searchView)
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .filter(object : Predicate<String> {
                override fun test(text: String): Boolean {
                    if (text.isEmpty()) {
                        toggleSearchResult(false)
                        return false
                    } else {
                        toggleSearchResult(true)
                        return true
                    }
                }
            })
            .distinctUntilChanged()
            .subscribe(object : DisposableObserver<String>() {
                override fun onComplete() {
                    TODO("Not yet implemented")
                }

                override fun onNext(t: String) {
                    viewModel.searchTag(t)
                }

                override fun onError(e: Throwable) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun toggleSearchResult(showSearchResult: Boolean) {
        searchList.visibility = if (showSearchResult) View.VISIBLE else View.GONE
        recentList.visibility = if (!showSearchResult) View.VISIBLE else View.GONE
    }


    private fun getViewModel(): MainActivityViewModel {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MainActivityViewModel(recentPhotoRepository, searchPhotoRepository) as T
            }
        })[MainActivityViewModel::class.java]
    }

}
