package com.baqoba.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baqoba.popularmovies.utilities.MovieApi;
import com.baqoba.popularmovies.utilities.MovieService;
import com.baqoba.popularmovies.utilities.ReviewAdapter;
import com.baqoba.popularmovies.utilities.ReviewModel;
import com.baqoba.popularmovies.utilities.Reviews;
import com.baqoba.popularmovies.utilities.TrailerAdapter;
import com.baqoba.popularmovies.utilities.TrailerModel;
import com.baqoba.popularmovies.utilities.Trailers;
import com.baqoba.popularmovies.utilities.Utility;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.baqoba.popularmovies.data.FavoriteContract;

public class MovieDetail extends AppCompatActivity implements TrailerAdapter.TrailerAdapterOnClickHandler, ReviewAdapter.ReviewAdapterOnClickHandler{
    private static final String TAG = "DetailActivity";
    public static final String IS_FAVORITE = "1";
    private TextView mTitle, mReleaseDate, mRating, mOverview;
    private ImageView mThumbnail;
    private Button mFavoriteBtn;

    private String idPath,titlePath, posterPath, overviewPath, ratingPath, releasePath;
    private String isFavorite = "0";

    TrailerAdapter trailerAdapter;
    ReviewAdapter reviewAdapter;
    private RecyclerView mTrailerRv, mReviewRv;
    private ProgressBar mTrailerIndicator, mReviewIndicator;

    private MovieService movieService;

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mTitle = (TextView) findViewById(R.id.tv_movie_title);
        mReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        mRating = (TextView) findViewById(R.id.tv_rating);
        mOverview = (TextView) findViewById(R.id.tv_overview);
        mThumbnail = (ImageView) findViewById(R.id.iv_movie_thumb);
        mFavoriteBtn = (Button) findViewById(R.id.btn_favorite);

        Intent intent = getIntent();

        if(intent.hasExtra(Intent.EXTRA_TEXT)){

            idPath = getIntent().getExtras().getString("id");
            titlePath =  getIntent().getExtras().getString("title");
            posterPath = getIntent().getExtras().getString("poster");
            overviewPath = getIntent().getExtras().getString("overview");
            ratingPath = getIntent().getExtras().getString("rating");
            releasePath = getIntent().getExtras().getString("release");
         //   isFavorite = getIntent().getExtras().getString("isFavorite");

            Log.d("path", posterPath);

            mTitle.setText(titlePath);
            mReleaseDate.setText(releasePath);
            mRating.setText(ratingPath);
            mOverview.setText(overviewPath);

            if(posterPath.equals("null")){
                Picasso.with(getApplicationContext()).load(R.drawable.no_image).resize(154,231).into(mThumbnail);
            } else {
                Picasso.with(getApplicationContext()).load("http://image.tmdb.org/t/p/w185/" + posterPath).into(mThumbnail);
            }

            if(isFavorite.equals("0")){
                mFavoriteBtn.setText(R.string.set_favorite);
            }else{
                mFavoriteBtn.setText(R.string.remove_favorite);
            }

            mReviewRv = (RecyclerView) findViewById(R.id.rv_reviews);
            mReviewIndicator = (ProgressBar) findViewById(R.id.pb_review_indicator);

            mTrailerRv = (RecyclerView) findViewById(R.id.rv_trailers);
            mTrailerIndicator = (ProgressBar) findViewById(R.id.pb_trailer_indicator);

            LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
            LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

            reviewAdapter = new ReviewAdapter(getApplicationContext(), this);
            trailerAdapter = new TrailerAdapter(getApplicationContext(), this);

            mReviewRv.setLayoutManager(reviewLayoutManager);
         //   mReviewRv.setHasFixedSize(true);
            mReviewRv.setItemAnimator(new DefaultItemAnimator());
            mReviewRv.setAdapter(reviewAdapter);

            mTrailerRv.setLayoutManager(trailerLayoutManager);
        //    mTrailerRv.setHasFixedSize(true);
            mTrailerRv.setItemAnimator(new DefaultItemAnimator());
            mTrailerRv.setAdapter(trailerAdapter);

            movieService = MovieApi.getClient().create(MovieService.class);

            loadReviews(idPath);
            loadTrailers(idPath);

            mFavoriteBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "button clicked", Toast.LENGTH_SHORT).show();
                    setToFavorite();
                }
            });

        }

    }

    private void loadReviews(String idPath) {
        Log.d("test1", "TEST 1 working");
        callReviewsApi(idPath).enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                // Got data. Send it to adapter

                List<ReviewModel> results = fetchResult(response);
                mReviewIndicator.setVisibility(View.GONE);
                reviewAdapter.addAll(results);
                Log.d("Resutl" , results.toString());

            }

            @Override
            public void onFailure(Call<Reviews> call, Throwable t) {
                t.printStackTrace();
                // TODO: 08/11/16 handle failure
            }
        });

    }

    private List<ReviewModel> fetchResult(Response<Reviews> response) {
        Reviews popularMovies = response.body();
        return popularMovies.getResults();
    }



    private Call<Reviews> callReviewsApi(String idPath) {
        return movieService.getReviews(
                idPath,
                getString(R.string.my_api_key)
        );
    }

    private void loadTrailers(String idPath) {
        Log.d(TAG, "loadFirstPage: ");
        mTrailerIndicator.setVisibility(View.VISIBLE);

        callTrailersApi(idPath).enqueue(new Callback<Trailers>() {
            @Override
            public void onResponse(Call<Trailers> call, Response<Trailers> response) {
                // Got data. Send it to adapter
                Log.d("Test8:" , "Test8");

                List<TrailerModel> results = fetchResults(response);
                mTrailerIndicator.setVisibility(View.GONE);
                trailerAdapter.addAll(results);
            }

            @Override
            public void onFailure(Call<Trailers> call, Throwable t) {
                t.printStackTrace();
                // TODO: 08/11/16 handle failure
            }
        });

    }

    private List<TrailerModel> fetchResults(Response<Trailers> responseTrailer) {
        Trailers trailers = responseTrailer.body();
        return trailers.getResults();
    }

    private Call<Trailers> callTrailersApi(String idPath) {
        return movieService.getTrailers(
                idPath,
                getString(R.string.my_api_key)
        );
    }

    @Override
    public void onClick(TrailerModel currentMovie) {
        String url = "https://www.youtube.com/watch?v=".concat(currentMovie.getKey());
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }

    @Override
    public void onClick(ReviewModel currentMovie) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(currentMovie.getUrl()));
        startActivity(i);

    }

    public void setToFavorite(){
/*
        if(isFavorite.equals("0")) {
            new AsyncTask<Void, Void, Uri>() {
                @Override
                protected Uri doInBackground(Void... params) {
                    ContentValues contentValues = new ContentValues();

                    contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID, idPath);
                    contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_TITLE, titlePath);
                 //   contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_POSTER_PATH, posterPath);
                 //   contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_OVERVIEW, overviewPath);
                 //   contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_VOTE_AVERAGE, ratingPath);
                 //   contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_RELEASE_DATE, releasePath);
                 //   c ontentValues.put(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_IS_FAVORITE, IS_FAVORITE);

                    return getContentResolver().insert(FavoriteContract.FavoriteEntry.CONTENT_URI,
                            contentValues);

                }

                @Override
                protected void onPostExecute(Uri returnUri) {
                    //   item.setIcon(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
                    if (mToast != null) {
                        mToast.cancel();
                    }

                    mToast = Toast.makeText(getBaseContext(), "added to favorites", Toast.LENGTH_LONG);
                    mToast.show();
                    isFavorite="1";
                    mFavoriteBtn.setText(R.string.remove_favorite);
                }
            }.execute();
        }else{
            new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... params) {
                    return getContentResolver().delete(
                            FavoriteContract.FavoriteEntry.CONTENT_URI,
                            FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ?",
                            new String[]{idPath}
                    );
                }

                @Override
                protected void onPostExecute(Integer rowsDeleted) {
                    //       item.setIcon(R.drawable.abc_btn_rating_star_off_mtrl_alpha);
                    if (mToast != null) {
                        mToast.cancel();
                    }
                    mToast = Toast.makeText(getBaseContext(), "removed from favorites", Toast.LENGTH_SHORT);
                    mToast.show();
                    isFavorite="0";
                    mFavoriteBtn.setText(R.string.set_favorite);
                }
            }.execute();
        }
*/
        ContentValues contentValues = new ContentValues();
        // Put the task description and selected mPriority into the ContentValues
        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID, idPath);
        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_TITLE, titlePath);
        Log.d("idpath: ", FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID);
        Log.d("titlePath: ", FavoriteContract.FavoriteEntry.COLUMN_MOVIE_TITLE);
        // Insert the content values via a ContentResolver
        Uri uri = getContentResolver().insert(FavoriteContract.FavoriteEntry.CONTENT_URI, contentValues);

        // Display the URI that's returned with a Toast
        // [Hint] Don't forget to call finish() to return to MainActivity after this insert is complete
        if(uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
    }




    }
}
