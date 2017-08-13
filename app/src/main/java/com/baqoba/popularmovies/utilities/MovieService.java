package com.baqoba.popularmovies.utilities;

import com.baqoba.popularmovies.utilities.PopularMovies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by ulimhd on 11/07/17.
 */

public interface MovieService {

    @GET("movie/{sortBy}")
    Call<PopularMovies> getPopularMovies(
            @Path("sortBy") String sortBy,
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int pageIndex
    );

    @GET("movie/{id}/videos")
    Call<Trailers> getTrailers(
            @Path("id") String id,
            @Query("api_key") String apiKey
    );

    @GET("movie/{id}/reviews")
    Call<Reviews> getReviews(
            @Path("id") String id,
            @Query("api_key") String apiKey
    );
}
