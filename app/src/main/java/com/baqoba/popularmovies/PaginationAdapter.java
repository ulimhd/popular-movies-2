package com.baqoba.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.baqoba.popularmovies.utilities.MovieModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ulimhd on 10/07/17.
 */

public class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private static final String BASE_URL_IMG = "http://image.tmdb.org/t/p/w185/";


    private List<MovieModel> movieResults;
    private Context context;

    private final PaginationAdapterOnClickHandler mClickHandler;

    private boolean isLoadingAdded = false;

    public PaginationAdapter(Context context, PaginationAdapterOnClickHandler clickHandler) {
        this.context = context;
        movieResults = new ArrayList<>();
        mClickHandler = clickHandler;
    }

    public List<MovieModel> getMovies() {
        return movieResults;
    }

    public void setMovies(List<MovieModel> movieResults) {
        this.movieResults = movieResults;
        notifyDataSetChanged();
    }

    public interface PaginationAdapterOnClickHandler {
        void onClick(MovieModel currentMovie);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.list_movie_poster, parent, false);
        viewHolder = new MovieVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        MovieModel result = movieResults.get(position); // Movie

        switch (getItemViewType(position)) {
            case ITEM:
                final MovieVH movieVH = (MovieVH) holder;
                Log.d("action_a", "--" + result.getPosterPath() + "--");

                if(result.getPosterPath().equals("null")){
                    Picasso.with(context).load(R.drawable.no_image).resize(185,277).into(movieVH.mPosterImg);
                } else {
                    Picasso.with(context).load("http://image.tmdb.org/t/p/w185/" + result.getPosterPath()).into(movieVH.mPosterImg);
                }

                break;

            case LOADING:
//                Do nothing
                break;
        }

    }

    @Override
    public int getItemCount() {
        return movieResults == null ? 0 : movieResults.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == movieResults.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }


    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(MovieModel r) {
        movieResults.add(r);
        notifyItemInserted(movieResults.size() - 1);
    }

    public void addAll(List<MovieModel> moveResults) {
        for (MovieModel result : moveResults) {
            add(result);
        }
    }

    public void remove(MovieModel r) {
        int position = movieResults.indexOf(r);
        if (position > -1) {
            movieResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new MovieModel());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = movieResults.size() - 1;
        MovieModel result = getItem(position);

        if (result != null) {
            movieResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public MovieModel getItem(int position) {
        return movieResults.get(position);
    }


   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
    protected class MovieVH extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView mPosterImg;

        public MovieVH(View itemView) {
            super(itemView);

            mPosterImg = (ImageView) itemView.findViewById(R.id.iv_poster);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
    /*        int adapterPosition = getAdapterPosition();
            String currentmovie = movieResults[adapterPosition];
            mClickHandler.onClick(currentmovie);

*/
            MovieModel currentMovie = movieResults.get(getAdapterPosition());
         //   mClickHandler.onClick(getAdapterPosition(), view);
            mClickHandler.onClick(currentMovie);
        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }
}
