package com.example.mostafa.surveysapp.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.example.mostafa.surveysapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SurveyWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager,
                                final int appWidgetId) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.survey), Context.MODE_PRIVATE);

        final String title = sharedPreferences.getString(context.getString(R.string.title), "");
        final String id = sharedPreferences.getString(context.getString(R.string.id), null);
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.survey_widget);
        if(id!=null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.surveys))
                    .child(id).child(context.getString(R.string.results));
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    views.setTextViewText(R.id.results_count,dataSnapshot.getChildrenCount()+" " + context.getString(R.string.results));
                    views.setTextViewText(R.id.title, title);
                    appWidgetManager.updateAppWidget(appWidgetId, views);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            reference.addValueEventListener(valueEventListener);
        }
        else {
            views.setTextViewText(R.id.results_count, context.getString(R.string.no_pinned_survey));
            views.setTextViewText(R.id.title, "");
            views.setOnClickPendingIntent(R.id.results_count, null);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}