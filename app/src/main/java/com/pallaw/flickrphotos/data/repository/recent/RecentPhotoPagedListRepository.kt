package com.pallaw.flickrphotos.data.repository.recent

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.pallaw.flickrphotos.data.remote.ITEM_PER_PAGE
import com.pallaw.flickrphotos.data.remote.ApiService
import com.pallaw.flickrphotos.util.NetworkState
import com.pallaw.flickrphotos.data.model.Photo
import io.reactivex.disposables.CompositeDisposable

class RecentPhotoPagedListRepository(private val apiService: ApiService) {

    lateinit var photoPagedList: LiveData<PagedList<Photo>>
    lateinit var photosDataSourceFactoryRecent: RecentPhotoDataSourceFactory

    fun fetchLiveRecentPhotoPagedList(compositeDisposable: CompositeDisposable): LiveData<PagedList<Photo>> {
        photosDataSourceFactoryRecent =
            RecentPhotoDataSourceFactory(
                apiService,
                compositeDisposable
            )

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(ITEM_PER_PAGE)
            .build()

        photoPagedList = LivePagedListBuilder(photosDataSourceFactoryRecent, config).build()

        return photoPagedList
    }

    fun getNetworkState(): LiveData<NetworkState> {
        return Transformations.switchMap<RecentPhotoDataSource, NetworkState>(
            photosDataSourceFactoryRecent.photosLiveDataSource, RecentPhotoDataSource::networkState
        )
    }
}