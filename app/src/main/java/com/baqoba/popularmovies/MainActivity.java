package com.baqoba.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.baqoba.popularmovies.data.FavoriteContract;
import com.baqoba.popularmovies.utilities.EndlessRecyclerViewScrollListener;
import com.baqoba.popularmovies.utilities.MovieApi;
import com.baqoba.popularmovies.utilities.MovieService;
import com.baqoba.popularmovies.utilities.PaginationScrollListener;
import com.baqoba.popularmovies.utilities.PopularMovies;
import com.baqoba.popularmovies.utilities.MovieModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements PaginationAdapter.PaginationAdapterOnClickHandler{

    private static final String TAG = "MainActivity";
    private RecyclerView mRecyclerView;
    private ProgressBar mLoadingIndicator;
    private String sortBy;

    private EndlessRecyclerViewScrollListener scrollListener;

    SharedPreferences sharedPref;
    PaginationAdapter adapter;
    String isFavorite;

    public static final String PREFERENCES = "pref";
    public static final String SORT_BY = "sort_by";
    public static final int START_PAGE = 1;
    private int TOTAL_PAGES = 5;
    private int currentPage = START_PAGE;

    private boolean isLoading = false;
    private boolean isLastPage = false;

    private MovieService movieService;
    private ArrayList<MovieModel> mMovies = null;

    List<MovieModel> results;
    int scrollPosition = 0;

    private static final String[] MOVIE_COLUMNS = {
            FavoriteContract.FavoriteEntry._ID,
            FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID,
            FavoriteContract.FavoriteEntry.COLUMN_MOVIE_TITLE,
            FavoriteContract.FavoriteEntry.COLUMN_MOVIE_POSTER_PATH,
            FavoriteContract.FavoriteEntry.COLUMN_MOVIE_OVERVIEW,
            FavoriteContract.FavoriteEntry.COLUMN_MOVIE_VOTE_AVERAGE,
            FavoriteContract.FavoriteEntry.COLUMN_MOVIE_RELEASE_DATE,
            FavoriteContract.FavoriteEntry.COLUMN_MOVIE_BACKDROP_PATH
    };

    public static final int COL_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_MOVIE_TITLE = 2;
    public static final int COL_MOVIE_POSTER_PATH = 3;
    public static final int COL_MOVIE_OVERVIEW = 4;
    public static final int COL_MOVIE_VOTE_AVERAGE = 5;
    public static final int COL_MOVIE_RELEASE_DATE = 6;
    public static final int COL_MOVIE_BACKDROP_PATH = 7;

    private static final String BUNDLE_RECYCLER_LAYOUT = "classname.recycler.layout";

    GridLayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = getSharedPreferences(PREFERENCES,
                Context.MODE_PRIVATE);
        if (sharedPref.contains(SORT_BY)) {
            sortBy = sharedPref.getString(SORT_BY, "");
        }else{
            sortBy = "popular";
        }



        isFavorite="0";

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_posters);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        layoutManager
                = new GridLayoutManager(this, calculateNoOfColumns(this));

        adapter = new PaginationAdapter(getApplicationContext(), this);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                loadNextPage();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        movieService = MovieApi.getClient().create(MovieService.class);

        if(savedInstanceState!=null){
            scrollPosition = savedInstanceState.getInt("scroll_position");
            results = (List<MovieModel>) savedInstanceState.getSerializable("my_list");
            currentPage = savedInstanceState.getInt("current_page");
            adapter.addAll(results);
        }else {

            loadFirstPage(sortBy);
        }

        mRecyclerView.scrollToPosition(scrollPosition);

    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("scroll_position", scrollPosition);
        savedInstanceState.putInt("curent_page", currentPage);
        savedInstanceState.putSerializable("my_list", (Serializable) results);
      //  savedInstanceState.putParcelable(BUNDLE_RECYCLER_LAYOUT , mRecyclerView.getLayoutManager().onSaveInstanceState());

    }
/*
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }
*/

    @Override
    protected void onPause() {
        super.onPause();

        if(layoutManager != null){
            scrollPosition = layoutManager.findFirstVisibleItemPosition();
        }
    }

    private void loadFirstPage(String sortBy) {
        Log.d(TAG, "loadFirstPage: ");
        mLoadingIndicator.setVisibility(View.VISIBLE);

        if(sortBy.equals("favorite")){
            new FetchFavoriteMoviesTask(this).execute();
        }else {
            callPopularMoviesApi(sortBy).enqueue(new Callback<PopularMovies>() {
                @Override
                public void onResponse(Call<PopularMovies> call, Response<PopularMovies> response) {
                    // Got data. Send it to adapter

                    List<MovieModel> results = fetchResults(response);
                    mLoadingIndicator.setVisibility(View.GONE);
                    adapter.addAll(results);

                    if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
                    else isLastPage = true;
                }

                @Override
                public void onFailure(Call<PopularMovies> call, Throwable t) {
                    t.printStackTrace();
                    // TODO: 08/11/16 handle failure
                }
            });
        }
    }

    private List<MovieModel> fetchResults(Response<PopularMovies> response) {
        PopularMovies popularMovies = response.body();
        return popularMovies.getResults();
    }

    private void loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);
        Log.d("ISLOADING: " , String.valueOf(isLoading));
        if(sortBy.equals("favorite")){
            new FetchFavoriteMoviesTask(this).execute();
        }else {
            callPopularMoviesApi(sortBy).enqueue(new Callback<PopularMovies>() {
                @Override
                public void onResponse(Call<PopularMovies> call, Response<PopularMovies> response) {
                    adapter.removeLoadingFooter();
                    isLoading = false;

                    results = fetchResults(response);
                    adapter.addAll(results);

                    if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
                    else isLastPage = true;
                }

                @Override
                public void onFailure(Call<PopularMovies> call, Throwable t) {
                    t.printStackTrace();
                    // TODO: 08/11/16 handle failure
                }
            });
        }
    }

    private Call<PopularMovies> callPopularMoviesApi(String sortBy) {
        return movieService.getPopularMovies(
                sortBy,
                getString(R.string.my_api_key),
                "en_US",
                currentPage
        );
    }

    @Override
    public void onClick(MovieModel currentMovie) {

        String selection = FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ?";
        String[] selectionArgs = { currentMovie.getId().toString() };

        Cursor mCursor = getContentResolver().query(FavoriteContract.FavoriteEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
        null);

        if (mCursor == null)
            isFavorite="0";
        try {
            while (mCursor.moveToNext()) {
                isFavorite="1";
            }
        } finally {
            mCursor.close();
        }


        Intent intent = new Intent(this, MovieDetail.class);
        Bundle bundle = new Bundle();
        bundle.putString(Intent.EXTRA_TEXT, "not null");
        bundle.putString("id", currentMovie.getId().toString());
        bundle.putString("title", currentMovie.getTitle());
        bundle.putString("poster", currentMovie.getPosterPath());
        bundle.putString("overview", currentMovie.getOverview());
        bundle.putString("rating", currentMovie.getVoteAverage().toString());
        bundle.putString("release", currentMovie.getReleaseDate());
        bundle.putString("backdrop", currentMovie.getBackdropPath());
        bundle.putString("isFavorite", isFavorite);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_popular) {
            sortBy = "popular";

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(SORT_BY, sortBy);
            editor.apply();

            currentPage = START_PAGE;

            adapter = new PaginationAdapter(getApplicationContext(), this);
            mRecyclerView.setAdapter(adapter);

            loadFirstPage(sortBy);

            return true;
        }else if(id == R.id.action_sort_rating){
            sortBy = "top_rated";
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(SORT_BY, sortBy);
            editor.apply();

            currentPage = START_PAGE;

            adapter = new PaginationAdapter(getApplicationContext(), this);
            mRecyclerView.setAdapter(adapter);

            loadFirstPage(sortBy);

            return true;
        }else if(id == R.id.action_sort_favorite){
            sortBy = "favorite";
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(SORT_BY, sortBy);
            editor.apply();

            currentPage = 1;

            adapter = new PaginationAdapter(getApplicationContext(), this);
            mRecyclerView.setAdapter(adapter);

            loadFirstPage(sortBy);

        }

        return super.onOptionsItemSelected(item);
    }

    /*get proper number of grid based on proportion of devices */
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 100;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        return noOfColumns;
    }

    public class FetchFavoriteMoviesTask extends AsyncTask<Void, Void, List<MovieModel>> {

        private Context mContext;

        public FetchFavoriteMoviesTask(Context context) {
            mContext = context;
        }

        private List<MovieModel> getFavoriteMoviesDataFromCursor(Cursor cursor) {
            List<MovieModel> results = new ArrayList<>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    MovieModel movie = new MovieModel(cursor);
                    results.add(movie);
                } while (cursor.moveToNext());
                cursor.close();
            }
            return results;
        }

        @Override
        protected List<MovieModel> doInBackground(Void... params) {
            Cursor cursor = mContext.getContentResolver().query(
                    FavoriteContract.FavoriteEntry.CONTENT_URI,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );
            return getFavoriteMoviesDataFromCursor(cursor);
        }

        @Override
        protected void onPostExecute(List<MovieModel> movies) {
            if (movies != null) {
                if (adapter != null) {
                    adapter.setMovies(movies);

                }

                mMovies = new ArrayList<>();
                mMovies.addAll(movies);
            }else{
                Toast.makeText(mContext, "No favorite yet. Please tag in the Movie Detail", Toast.LENGTH_LONG).show();
            }
        }
    }


}
