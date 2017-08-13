package com.baqoba.popularmovies.utilities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.baqoba.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 08/08/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<TrailerModel> trailerResults;
    private Context context;

    private final TrailerAdapterOnClickHandler mClickHandler;

    public TrailerAdapter(Context context, TrailerAdapterOnClickHandler clickHandler) {
        this.context = context;
        trailerResults= new ArrayList<>();
        mClickHandler = clickHandler;
    }

    public interface TrailerAdapterOnClickHandler {
        void onClick(TrailerModel currentMovie);
    }

    protected class MyItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView trailerImage;


        public MyItemHolder(View itemView) {
            super(itemView);
            Log.d("Test4", "test4");
            trailerImage = (ImageView) itemView.findViewById(R.id.iv_trailer);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
    /*        int adapterPosition = getAdapterPosition();
            String currentmovie = movieResults[adapterPosition];
            mClickHandler.onClick(currentmovie);

*/
            TrailerModel currentMovie = trailerResults.get(getAdapterPosition());
            //   mClickHandler.onClick(getAdapterPosition(), view);
            mClickHandler.onClick(currentMovie);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        Log.d("Test5", "Test5");
        viewHolder = getViewHolder(parent, inflater);
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        Log.d("TESTcreate", "bbbbb");
        View view = inflater.inflate(R.layout.list_trailer, parent, false);
        viewHolder = new MyItemHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TrailerModel result = trailerResults.get(position);
        String id = result.getKey();

        String thumbnailUrl = "http://img.youtube.com/vi/".concat(id).concat("/hqdefault.jpg");
        Log.d("THUMB:", thumbnailUrl);
        final MyItemHolder trailerVH = (MyItemHolder) holder;

        if(result.getId().equals("null")){
            Picasso.with(context).load(R.drawable.no_image).resize(185,277).into(trailerVH.trailerImage);
        } else {
            Picasso.with(context).load(thumbnailUrl).into(trailerVH.trailerImage);
        }

    }

    @Override
    public int getItemCount() {
        return trailerResults == null ? 0 : trailerResults.size();
    }

    public void add(TrailerModel r) {
        trailerResults.add(r);
        Log.d("Test6", r.toString());
        notifyItemInserted(trailerResults.size() - 1);
    }

    public void addAll(List<TrailerModel> trailerResults) {
        for (TrailerModel result : trailerResults) {
            Log.d("result: ", result.toString());
            add(result);
            Log.d("Test7", "Test7");
        }
    }
}
