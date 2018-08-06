package com.example.mostafa.surveysapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mostafa.surveysapp.data.models.Result;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ViewHolder>{

    private List<Result> results;
    private OnClickListener onClickListener;

    public ResultsAdapter(List<Result> results, OnClickListener onClickListener) {
        this.results = results;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ResultsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.results_list_item, parent, false);
        return new ResultsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultsAdapter.ViewHolder holder, int position) {
        final Result result = results.get(position);
        Picasso.get().load(result.getOwnerPic()).into(holder.profileImage);
        holder.usernameTextView.setText(result.getOwnerName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(result);
            }
        });
    }

    @Override
    public int getItemCount() {
        return results==null ? 0 : results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.profile_pic)CircleImageView profileImage;
        @BindView(R.id.username)TextView usernameTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public void addAll (List<Result> results){
        this.results.clear();
        this.results.addAll(results);
        notifyDataSetChanged();
    }

    public interface OnClickListener {
        void onClick(Result result);
    }
}
