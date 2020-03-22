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
    lateinit var photosDataSourceFactory: RecentPhotoDataSourceFactory

    fun fetchLiveRecentPhotoPagedList(compositeDisposable: CompositeDisposable): LiveData<PagedList<Photo>> {
        photosDataSourceFactory =
            RecentPhotoDataSourceFactory(
                apiService,
                compositeDisposable
            )

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(ITEM_PER_PAGE)
            .build()

        photoPagedList = LivePagedListBuilder(photosDataSourceFactory, config).build()

        return photoPagedList
    }

    fun getNetworkState(): LiveData<NetworkState> {
        return Transformations.switchMap<RecentPhotoDataSource, NetworkState>(
            photosDataSourceFactory.photosLiveDataSource, RecentPhotoDataSource::networkState
        )
    }
}