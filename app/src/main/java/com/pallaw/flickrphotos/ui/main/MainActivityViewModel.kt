package com.pallaw.flickrphotos.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.pallaw.flickrphotos.data.model.Photo
import com.pallaw.flickrphotos.data.repository.recent.RecentPhotoPagedListRepository
import com.pallaw.flickrphotos.util.NetworkState
import io.reactivex.disposables.CompositeDisposable

class MainActivityViewModel(
    private val recentPhotoRepository: RecentPhotoPagedListRepository
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val recentPagedList: LiveData<PagedList<Photo>> by lazy {
        recentPhotoRepository.fetchLiveRecentPhotoPagedList(compositeDisposable)
    }

    val networkState: LiveData<NetworkState> by lazy {
        recentPhotoRepository.getNetworkState()
    }

    fun listIsEmpty(): Boolean {
        return recentPagedList.value?.isEmpty() ?: true
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}