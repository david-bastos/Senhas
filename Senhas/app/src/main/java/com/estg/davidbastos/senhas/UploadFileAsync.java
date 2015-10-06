package com.estg.davidbastos.senhas;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.util.Arrays;

/**
 * Created by David Bastos on 24-08-2015.
 */
public class UploadFileAsync extends AsyncTask<String, String, String> {

    private ProgressDialog mProgressDialog;
    private Context context;
    private static final String TAG = "SenhasLog";
    private MainActivity activity;
    private static Drive mService;
    private java.io.File file;
    private Handler handler;

    public UploadFileAsync(MainActivity activity, Context context, java.io.File file, Handler handler) {
        this.context = context;
        this.activity = activity;
        this.file = file;
        this.handler = handler;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Uploading file..");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);

    }

    protected String doInBackground(String... params) {
        String token = "nNull";
        String account = "senhas.estg@gmail.com";

        try {
            GoogleAccountCredential credential =
                    GoogleAccountCredential.usingOAuth2(context, Arrays.asList(DriveScopes.DRIVE));
            credential.setSelectedAccountName(account);
            // Trying to get a token right away to see if we are authorized
            credential.getToken();
            mService = new Drive.Builder(AndroidHttp.newCompatibleTransport(),
                    new GsonFactory(), credential).build();
            Log.d(TAG, mService.toString());
            return update("0B7VfzzfiyneBNzk5TVpzZHlqeHc", file);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            // If the Exception is User Recoverable, we display a notification that will trigger the
            // intent to fix the issue.
            if (e instanceof UserRecoverableAuthException) {
                UserRecoverableAuthException exception = (UserRecoverableAuthException) e;
                NotificationManager notificationManager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                Intent authorizationIntent = exception.getIntent();
                authorizationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(
                        Intent.FLAG_FROM_BACKGROUND);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                        authorizationIntent, 0);
                Notification notification = new Notification.Builder(context)
                        .setSmallIcon(android.R.drawable.ic_dialog_alert)
                        .setTicker("Permission requested")
                        .setContentTitle("Permission requested")
                        .setContentText("for account " + account)
                        .setContentIntent(pendingIntent).setAutoCancel(true).build();
                notificationManager.notify(0, notification);
            } else {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static String update(String resId, java.io.File file) {
        File gFl = null;
        if (mService != null && resId != null)
            try {
                File meta = new File();

                gFl = mService.files().update(resId, meta, new FileContent(null, file)).execute();

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        return gFl == null ? null : gFl.getId();
    }

    protected void onPostExecute(String result) {
        try {
            super.onPostExecute(result);
            mProgressDialog.dismiss();
            if (result != null)
                handler.sendEmptyMessage(1);
            else
                handler.sendEmptyMessage(2);
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(TAG, e.getMessage());
        }
    }
}
