package com.pallaw.flickrphotos.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.pallaw.flickrphotos.data.model.Photo
import com.pallaw.flickrphotos.data.repository.recent.RecentPhotoPagedListRepository
import com.pallaw.flickrphotos.data.repository.search.SearchPhotoPagedListRepository
import com.pallaw.flickrphotos.util.NetworkState
import io.reactivex.disposables.CompositeDisposable

class MainActivityViewModel(
    private val recentPhotoRepository: RecentPhotoPagedListRepository,
    searchPhotoRepository: SearchPhotoPagedListRepository
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val searchTag = MutableLiveData<String>()


    val recentPagedList: LiveData<PagedList<Photo>> by lazy {
        recentPhotoRepository.fetchLiveRecentPhotoPagedList(compositeDisposable)
    }

    val recentPhotosNetworkState: LiveData<NetworkState> by lazy {
        recentPhotoRepository.getNetworkState()
    }

    fun isRecentListEmpty(): Boolean {
        return recentPagedList.value?.isEmpty() ?: true
    }


    val searchPagedList: LiveData<PagedList<Photo>> by lazy {
        searchPhotoRepository.fetchLiveSearchPhotoPagedList(searchTag, compositeDisposable)
    }

    val searchPhotosNetworkState: LiveData<NetworkState>? by lazy {
        searchPhotoRepository.getNetworkState()
    }

    fun isSearchListEmpty(): Boolean {
        return searchPagedList.value?.isEmpty() ?: true
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun searchTag(tag: String) {
        searchTag.postValue(tag)
    }

}