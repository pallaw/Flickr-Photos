package com.pallaw.flickrphotos.data.repository.search

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.pallaw.flickrphotos.data.remote.ApiService
import com.pallaw.flickrphotos.data.model.Photo
import io.reactivex.disposables.CompositeDisposable

class SearchPhotoDataSourceFactory(
    private val tag: String,
    private val apiService: ApiService,
    private val compositeDisposable: CompositeDisposable
) : DataSource.Factory<Int, Photo>() {

    val photosLiveDataSource = MutableLiveData<SearchPhotoDataSource>()

    override fun create(): DataSource<Int, Photo> {
        val photoDataSource =
            SearchPhotoDataSource(
                tag,
                apiService,
                compositeDisposable
            )

        photosLiveDataSource.postValue(photoDataSource)
        return photoDataSource
    }
}