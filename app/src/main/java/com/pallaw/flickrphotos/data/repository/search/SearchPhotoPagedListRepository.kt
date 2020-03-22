package com.pallaw.flickrphotos.data.repository.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.pallaw.flickrphotos.data.model.Photo
import com.pallaw.flickrphotos.data.remote.ApiService
import com.pallaw.flickrphotos.data.remote.ITEM_PER_PAGE
import com.pallaw.flickrphotos.util.NetworkState
import io.reactivex.disposables.CompositeDisposable

class SearchPhotoPagedListRepository(private val apiService: ApiService) {

    lateinit var photoPagedList: LiveData<PagedList<Photo>>
    var photosDataSourceFactory: SearchPhotoDataSourceFactory? = null

    fun fetchLiveSearchPhotoPagedList(
        searchTagLive: MutableLiveData<String>,
        compositeDisposable: CompositeDisposable
    ): LiveData<PagedList<Photo>> {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(ITEM_PER_PAGE)
            .build()


        photoPagedList = Transformations.switchMap<String, PagedList<Photo>>(
            searchTagLive
        ) { searchInput: String ->
            photosDataSourceFactory =
                SearchPhotoDataSourceFactory(
                    searchInput,
                    apiService,
                    compositeDisposable
                )

            return@switchMap LivePagedListBuilder(photosDataSourceFactory!!, config).build()
//            if (input == null || input == "" || input == "%%") {
//                //check if the current value is empty load all data else search
//                return@switchMap LivePagedListBuilder<Any?, Any?>(
//                    teamDao.loadAllTeam(), config
//                )
//                    .build()
//            } else {
//                println("CURRENTINPUT: $input")
//                return@switchMap LivePagedListBuilder<Any?, Any?>(
//                    teamDao.loadAllTeamByName(input), config
//                )
//                    .build()
//            }
//        }
//         LivePagedListBuilder(photosDataSourceFactory, config).build()
        }


//        photoPagedList = LivePagedListBuilder(photosDataSourceFactory, config).build()
//
        return photoPagedList
    }

    fun getNetworkState(): LiveData<NetworkState>? {
        photosDataSourceFactory?.let {
            return Transformations.switchMap<SearchPhotoDataSource, NetworkState>(
                it.photosLiveDataSource, SearchPhotoDataSource::networkState
            )
        }
        return null
    }
}