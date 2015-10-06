package com.estg.davidbastos.senhas;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by David Bastos on 05-08-2015.
 */

public class DownloadFileAsync extends AsyncTask<String, String, ArrayList<Senha>> {
    private ProgressDialog mProgressDialog;
    private InputStream file;
    private Handler handler;
    private static final String TAG = "SenhasLog";
    private ArrayList<Senha> senhas = null;

    private Context context;
    private MainActivity activity;

    public DownloadFileAsync(MainActivity activity, Context context, Handler handler) {
        this.context = context;
        this.activity = activity;
        this.handler = handler;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Downloading file..");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog.show();
    }

    @Override
    protected ArrayList<Senha> doInBackground(String... params) {
        String token = "nNull";
        String account = "senhas.estg@gmail.com";

        try {
            GoogleAccountCredential credential =
                    GoogleAccountCredential.usingOAuth2(context, Arrays.asList(DriveScopes.DRIVE));
            credential.setSelectedAccountName(account);
            // Trying to get a token right away to see if we are authorized
            credential.getToken();
            Drive mService = new Drive.Builder(AndroidHttp.newCompatibleTransport(),
                    new GsonFactory(), credential).build();
            Log.d(TAG, mService.toString());

            File aux = mService.files().get("0B7VfzzfiyneBNzk5TVpzZHlqeHc").execute();
            //

            file = downloadFile(mService, aux);
            setFile(file);
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
        return senhas;
    }

    private static InputStream downloadFile(Drive service, File file) {
        if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0) {
            try {
                // uses alt=media query parameter to request content
                return service.files().get(file.getId()).executeMediaAsInputStream();
            } catch (IOException e) {
                // An error occurred.
                e.printStackTrace();
                return null;
            }
        } else {
            // The file doesn't have any content stored on Drive.
            return null;
        }
    }

    protected void setFile(InputStream file) {
        XmlPullParserFactory pullParserFactory;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(file, null);

            parseXML(parser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseXML(XmlPullParser parser) throws XmlPullParserException, IOException {
        int eventType = parser.getEventType();
        Senha senhaActual = null;

        try {
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        senhas = new ArrayList<Senha>();
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("senha")) {
                            senhaActual = new Senha();
                        } else if (senhaActual != null) {
                            if (name.equals("periodo")) {
                                senhaActual.setPeriodo(parser.nextText());
                            } else if (name.equals("docente")) {
                                if (parser.nextText().equals("Sim"))
                                    senhaActual.setDocente(true);
                                else
                                    senhaActual.setDocente(false);
                            } else if (name.equals("cantina")) {
                                senhaActual.setCantina(parser.nextText());
                            } else if (name.equals("preco")) {
                                senhaActual.setPreco(parser.nextText());
                            } else if (name.equals("refeicao")) {
                                senhaActual.setRefeicao(parser.nextText());
                            } else if (name.equals("data")) {
                                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                                try {
                                    Date date = format.parse(parser.nextText());
                                    senhaActual.setData(date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } else if (name.equals("telemovel")) {
                                senhaActual.setTelemovel(parser.nextText());
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("senha") && senhaActual != null) {
                            senhas.add(senhaActual);
                        }
                }
                eventType = parser.next();
            }
            //return senhas;
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(TAG, e.getMessage());
        }
    }


    protected void onPostExecute(ArrayList<Senha> result) {
        try {
            super.onPostExecute(result);
            mProgressDialog.dismiss();
            if (result.isEmpty() || result == null)
                handler.sendMessage(Message.obtain(handler, 3));
            else
                handler.sendMessage(Message.obtain(handler, 0, result));
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(TAG, e.getMessage());
        }
    }

}
