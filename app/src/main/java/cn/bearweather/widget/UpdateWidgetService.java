package cn.bearweather.widget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.widget.RemoteViews;

import java.util.Timer;
import java.util.TimerTask;

import cn.bearweather.MainActivity;
import cn.bearweather.R;

public class UpdateWidgetService extends Service {
    private Timer timer;
    private TimerTask timerTask;

    public UpdateWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new Binder();
    }

    @Override
    public void onCreate() {

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timerTask.cancel();
        timer = null;
        timerTask = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String city = intent.getStringExtra("city");
        String tmp = intent.getStringExtra("tmp");
        ComponentName componentName = new ComponentName(UpdateWidgetService.this, WeatherAppWidget.class);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.weather_app_widget);
        //需要接受传来的天气 然后再设置
        //设置text
        remoteViews.setTextViewText(R.id.appwidget_text, city + " " + tmp + "℃");

        //设置图片
        remoteViews.setImageViewResource(R.id.appwidget_img, R.drawable.weather_icon_100);

        //打开activity
        Intent startActivityIntent = new Intent(UpdateWidgetService.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivities(UpdateWidgetService.this, 0, new Intent[]{startActivityIntent}, 0);
        remoteViews.setOnClickPendingIntent(R.id.rv_layout, pendingIntent);

        //AppWidgetManager处理Widget
        AppWidgetManager awm = AppWidgetManager.getInstance(getApplicationContext());
        awm.updateAppWidget(componentName, remoteViews);

        return super.onStartCommand(intent, flags, startId);
    }
}
