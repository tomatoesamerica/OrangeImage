package kotlincodes.com.retrofitwithkotlin.retrofit

import com.unsplash.pickerandroid.photopicker.data.UnsplashPhoto
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("/photos/")
    fun getPhotos(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 8,
        @Query("client_id") clientID: String = "ZCrQRuxnXBxzR_sl0WeHvj9nMEdw5y-ySr5wbWDp7Sw"
//    ): ArrayList<UnsplashPhoto?>
    ): Observable<ArrayList<UnsplashPhoto?>>


}