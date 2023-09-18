package de.tadris.fitness.client;

import java.io.File;
import java.util.List;

import de.tadris.fitness.model.BirdData;
import de.tadris.fitness.service.BirdClassifierService;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BirdClassifierApiClient {

    private static String BASE_URL = "https://bird-classifier.p.rapidapi.com/";
    private BirdClassifierService birdClassifierService;

    public BirdClassifierApiClient() {
        OkHttpClient client = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        birdClassifierService = retrofit.create(BirdClassifierService.class);
    }
    public Call<List<BirdData>> getPredictions(File quizImage){

        // Create the request body
        MediaType mediaType = MediaType.parse("multipart/form-data");
        RequestBody requestBody = RequestBody.create(mediaType, quizImage);

        // Create the MultipartBody.Part for the image
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", quizImage.getName(), requestBody);

        return  birdClassifierService.getPredictions(5, imagePart);
    }

}
