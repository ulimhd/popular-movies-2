package com.baqoba.popularmovies.utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baqoba.popularmovies.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 08/08/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<ReviewModel> reviewResults;
    private Context context;

    private final ReviewAdapterOnClickHandler mClickHandler;

    //  String author;
    // String content;

    public ReviewAdapter(Context context, ReviewAdapterOnClickHandler clickHandler) {
        this.context = context;
        reviewResults= new ArrayList<>();
        mClickHandler = clickHandler;
    }

    public interface ReviewAdapterOnClickHandler {
        void onClick(ReviewModel currentMovie);
    }

    protected class MyItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mAuthor, mContent;


        public MyItemHolder(View itemView) {
            super(itemView);
            Log.d("Test4", "test4");

            mAuthor = (TextView) itemView.findViewById(R.id.tv_review_author);
            mContent = (TextView) itemView.findViewById(R.id.tv_review_content);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
    /*        int adapterPosition = getAdapterPosition();
            String currentmovie = movieResults[adapterPosition];
            mClickHandler.onClick(currentmovie);

*/
            ReviewModel currentMovie = reviewResults.get(getAdapterPosition());
            //   mClickHandler.onClick(getAdapterPosition(), view);
            mClickHandler.onClick(currentMovie);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        //    LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        Log.d("Test5", "Test5");

        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_review, parent, false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }
    /*
        @NonNull
        private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
            RecyclerView.ViewHolder viewHolder;
            View view = inflater.inflate(R.layout.list_review, parent, false);
            viewHolder = new MyItemHolder(view);
            return viewHolder;
        }
    */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ReviewModel result = reviewResults.get(position);
        String id = result.getId();
        String author=result.getAuthor();
        String content=result.getContent();
        Log.d("author ", author);
        Log.d("content ", content);
        final MyItemHolder reviewVH = (MyItemHolder) holder;

        reviewVH.mAuthor.setText(author);
        reviewVH.mContent.setText(content);
        Log.d("aaaaa", reviewVH.mAuthor.getText().toString());
        Log.d("bbbb", reviewVH.mContent.getText().toString());

    }

    @Override
    public int getItemCount() {
        return reviewResults == null ? 0 : reviewResults.size();
    }

    public void add(ReviewModel r, int position) {
        reviewResults.add(r);
        //       Log.d("Test6", r.getAuthor());
        //      Log.d("Test8", r.getContent());
        //    author = r.getAuthor();
        //   content = r.getContent();
        notifyItemInserted(position);
    }

    public void addAll(List<ReviewModel> reviewResults) {
        for (int i=0; i<reviewResults.size(); ++i) {
            Log.d("result: ", reviewResults.toString());
            add(reviewResults.get(i), i); Log.d("Test7", "Test7");
        }
    }
/*
    public void addAll(List<ReviewModel> reviewResults) {
        for (ReviewModel result : reviewResults) {
            Log.d("result: ", result.toString());
            add(result);
            Log.d("Test7", "Test7");
        }
    }
*/
    public ReviewModel getItem(int position) {
        return reviewResults.get(position);
    }

}

