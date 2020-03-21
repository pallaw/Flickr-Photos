package com.pallaw.flickrphotos.data.repository.recent

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.pallaw.flickrphotos.data.remote.FIRST_PAGE
import com.pallaw.flickrphotos.data.remote.ApiService
import com.pallaw.flickrphotos.data.model.Photo
import com.pallaw.flickrphotos.util.NetworkState
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class RecentPhotoDataSource (private val apiService : ApiService, private val compositeDisposable: CompositeDisposable)
    : PageKeyedDataSource<Int, Photo>(){

    private var page = FIRST_PAGE
    private var TAG = RecentPhotoDataSource::class.java.simpleName

    val networkState: MutableLiveData<NetworkState> = MutableLiveData()


    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Photo>) {

        networkState.postValue(NetworkState.LOADING)

        compositeDisposable.add(
            apiService.getRecentPhotos(page)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        callback.onResult(it.photos.photo, null, page+1)
                        networkState.postValue(NetworkState.LOADED)
                    },
                    {
                        networkState.postValue(NetworkState.ERROR)
                        Log.e(TAG, it.message)
                    }
                )
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Photo>) {
        networkState.postValue(NetworkState.LOADING)

        compositeDisposable.add(
            apiService.getRecentPhotos(params.key)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        if(it.photos.pages >= params.key) {
                            callback.onResult(it.photos.photo, params.key+1)
                            networkState.postValue(NetworkState.LOADED)
                        }
                        else{
                            networkState.postValue(NetworkState.ENDOFLIST)
                        }
                    },
                    {
                        networkState.postValue(NetworkState.ERROR)
                        Log.e(TAG, it.message)
                    }
                )
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Photo>) {

    }
}