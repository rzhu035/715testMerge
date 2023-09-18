package de.tadris.fitness.service;

import java.util.List;

import de.tadris.fitness.model.BirdData;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface BirdClassifierService {
    @Headers({
            "X-RapidAPI-Key: 710ead6ec6mshc00b61b145a0e83p1e7f3djsn3da619a9e71a",
            "X-RapidAPI-Host: bird-classifier.p.rapidapi.com"
    })
    @GET("BirdClassifier/birdNames")
    Call<ResponseBody> getBirdNames();

    @Headers({
            "X-RapidAPI-Key: 710ead6ec6mshc00b61b145a0e83p1e7f3djsn3da619a9e71a",
            "X-RapidAPI-Host: bird-classifier.p.rapidapi.com"
    })
    @Multipart
    @POST("BirdClassifier/prediction")
    Call<List<BirdData>> getPredictions(
            @Query("results") int results,
            @Part MultipartBody.Part image
    );
}