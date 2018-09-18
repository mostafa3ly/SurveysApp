package com.example.mostafa.surveysapp.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mostafa.surveysapp.R;
import com.example.mostafa.surveysapp.data.models.Survey;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MySurveysAdapter extends RecyclerView.Adapter<MySurveysAdapter.ViewHolder> {

    private List<Survey> mySurveys;
    private OnClickListener onClickListener;
    private Context context;

    public MySurveysAdapter(List<Survey> mySurveys, OnClickListener onClickListener, Context context) {
        this.mySurveys = mySurveys;
        this.onClickListener = onClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public MySurveysAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_surveys_list_item, parent, false);
        return new MySurveysAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MySurveysAdapter.ViewHolder holder, int position) {
        final Survey survey = mySurveys.get(position);
        holder.titleTextView.setText(survey.getTitle());
        String str = String.valueOf(survey.getQuestions().size()) + " " + context.getString(R.string.questions);
        holder.questionsCountTextView.setText(str);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(survey.getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mySurveys==null ? 0 : mySurveys.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.survey_title)TextView titleTextView;
        @BindView(R.id.questions_count)TextView questionsCountTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }


    public void addAll(List<Survey> surveys)
    {
        mySurveys = new ArrayList<>();
        mySurveys.addAll(surveys);
        notifyDataSetChanged();
    }


    public interface OnClickListener{
        void onClick(String id);
    }

}