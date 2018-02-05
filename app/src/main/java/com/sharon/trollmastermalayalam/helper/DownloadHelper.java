package com.sharon.trollmastermalayalam.helper;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.sharon.trollmastermalayalam.R;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

public class DownloadHelper {

    private static final int NOTIFICATION_ID = 6789;
    ThinDownloadManager downloadManager;
    private int downloadId;
    private Activity activity;
    private String url, id, link, captionmessage, ext;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder builder;

    public DownloadHelper(Activity activity, String url, String id, String type) {
        this.activity = activity;
        this.id = id;
        this.url = url;
        this.link = link;
        this.captionmessage = captionmessage;
        if ("photo".equals(type)) {
            ext = Constants.image_extention;
        } else if ("video".equals(type)) {
            ext = Constants.video_extention;
        }
        downloadManager = new ThinDownloadManager();
    }

    public void startDownload() {
        Uri downloadUri = Uri.parse(url);
        Uri destinationUri = Uri.parse(Constants.folder_main_path + Constants.folder_name + id + ext);
        DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                .addCustomHeader("Auth-Token", "YourTokenApiKey")
                .setRetryPolicy(new DefaultRetryPolicy())
                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                .setDownloadContext(activity)//Optional
                .setStatusListener(new DownloadStatusListenerV1() {
                    @Override
                    public void onDownloadComplete(DownloadRequest downloadRequest) {
                        endNotification();
                        Toast.makeText(activity, "ഡൌൺലോഡ് ചെയ്തു കഴിഞ്ഞു ചേട്ടായി :)", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
                        cancelNotification();
                        Toast.makeText(activity, "TMM: Download Failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
                        updateNotification(progress);
                    }

                });
        int downloadId = downloadManager.add(downloadRequest);

        startNotification();
    }

    private void startNotification() {
        String id = "trollmaster_intent";
        mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(activity, id)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(false)
                .setChannelId(id)
                .setPriority(Notification.PRIORITY_HIGH);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            builder.setColor(Color.BLUE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "TrollMaster";
            String description = "Downloading Trolls";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            mChannel.setDescription(description);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void updateNotification(int progress) {
        builder.setProgress(100, progress, false)
                .setOngoing(false)
                .setContentTitle("Downloading your trolls :)")
                .setLargeIcon(BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher));
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void endNotification() {
        builder.setContentTitle("Downloaded Successfully")
                .setProgress(0, 0, false)
                .setLargeIcon(BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher))
                .setOngoing(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            builder.setColor(Color.WHITE);
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void cancelNotification() {
        builder.setContentTitle("Download error")
                .setProgress(0, 0, false)
                .setLargeIcon(BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher))
                .setOngoing(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            builder.setColor(Color.WHITE);
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
