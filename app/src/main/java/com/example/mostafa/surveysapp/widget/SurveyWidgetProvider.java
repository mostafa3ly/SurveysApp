package com.example.mostafa.surveysapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.example.mostafa.surveysapp.R;
import com.example.mostafa.surveysapp.ResultsActivity;

public class SurveyWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.survey), Context.MODE_PRIVATE);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.survey_widget);
        String count = sharedPreferences.getString(context.getString(R.string.results), "");
        String title = sharedPreferences.getString(context.getString(R.string.title), "");
        String id = sharedPreferences.getString(context.getString(R.string.id), "");
        boolean pin = sharedPreferences.getBoolean(context.getString(R.string.pin), false);
        Intent intent = new Intent(context, ResultsActivity.class);
        intent.putExtra(context.getString(R.string.id), id);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        if (pin) {
            views.setOnClickPendingIntent(R.id.results_count, pendingIntent);
            views.setTextViewText(R.id.title, title);
            views.setTextViewText(R.id.results_count, count + " " + context.getString(R.string.results));
        } else {
            views.setTextViewText(R.id.results_count, context.getString(R.string.no_pinned_survey));
            views.setTextViewText(R.id.title, "");
            views.setOnClickPendingIntent(R.id.results_count,null);
        }
        appWidgetManager.updateAppWidget(appWidgetId, views);
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