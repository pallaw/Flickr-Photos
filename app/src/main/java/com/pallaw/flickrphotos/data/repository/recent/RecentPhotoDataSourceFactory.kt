package com.pallaw.flickrphotos.data.repository.recent

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.pallaw.flickrphotos.data.remote.ApiService
import com.pallaw.flickrphotos.data.model.Photo
import io.reactivex.disposables.CompositeDisposable

class RecentPhotoDataSourceFactory(
    private val apiService: ApiService,
    private val compositeDisposable: CompositeDisposable
) : DataSource.Factory<Int, Photo>() {

    val photosLiveDataSource = MutableLiveData<RecentPhotoDataSource>()

    override fun create(): DataSource<Int, Photo> {
        val photoDataSource =
            RecentPhotoDataSource(
                apiService,
                compositeDisposable
            )

        photosLiveDataSource.postValue(photoDataSource)
        return photoDataSource
    }
}