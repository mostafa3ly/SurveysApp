package com.example.mostafa.surveysapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mostafa.surveysapp.data.models.Result;
import com.example.mostafa.surveysapp.ui.adapters.ResultsAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultsActivity extends AppCompatActivity implements ResultsAdapter.OnClickListener{

    @BindView(R.id.results_list)RecyclerView resultsRecyclerView;
    @BindView(R.id.progress_bar)ProgressBar progressBar;
    @BindView(R.id.message)TextView messageTextView;

    private ResultsAdapter mResultsAdapter;
    private ValueEventListener mValueEventListener ;
    private DatabaseReference mResultsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        ButterKnife.bind(this);
        String id = getIntent().getStringExtra(getString(R.string.id));
        mResultsAdapter = new ResultsAdapter(new ArrayList<Result>(),this);
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        resultsRecyclerView.setAdapter(mResultsAdapter);
        mResultsReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.surveys)).child(id).child(getString(R.string.results));
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Result> results = new ArrayList<>();
                for (DataSnapshot snapshot :dataSnapshot.getChildren()) {
                    Result result = snapshot.getValue(Result.class);
                    results.add(result);
                }
                mResultsAdapter.addAll(results);
                progressBar.setVisibility(View.GONE);
                if(results.isEmpty()) messageTextView.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        };
    }
    @Override
    protected void onResume() {
        super.onResume();
        mResultsReference.addValueEventListener(mValueEventListener);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.position),resultsRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        resultsRecyclerView.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(getString(R.string.position)));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mResultsReference.removeEventListener(mValueEventListener);
    }

    @Override
    public void onClick(Result result) {
        Intent intent = new Intent(this,ResultActivity.class);
        intent.putExtra(getString(R.string.result),result);
        startActivity(intent);
    }

}
