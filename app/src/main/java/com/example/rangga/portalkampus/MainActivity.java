package com.example.rangga.portalkampus

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog pDialog;

    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> DaftarBerita = new ArrayList<HashMap<String, String>>();

    private static String url_berita = "http://10.0.3.2/lokomedia/lokoandro/berita.php";


    public static final String TAG_ID = "id";
    public static final String TAG_JUDUL = "judul";
    public static final String TAG_GAMBAR = "gambar";


    JSONArray string_json = null;

    ListView list;
    LazyAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DaftarBerita = new ArrayList<HashMap<String, String>>();

        new AmbilData().execute();

        list = (ListView) findViewById(R.id.list);

        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                HashMap<String, String> map = DaftarBerita.get(position);

                // Starting new intent
                Intent in = new Intent(getApplicationContext(), DetailBerita.class);

                in.putExtra(TAG_ID, map.get(TAG_ID));
                in.putExtra(TAG_GAMBAR, map.get(TAG_GAMBAR));
                startActivity(in);
            }
        });

    }


    public void SetListViewAdapter(ArrayList<HashMap<String, String>> berita) {
        adapter = new LazyAdapter(this, berita);
        list.setAdapter(adapter);
    }


    class AmbilData extends AsyncTask <String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Mohon tunggu...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            JSONObject json = jParser.makeHttpRequest(url_berita, "GET",params);

            try {


                string_json = json.getJSONArray("berita");

                for (int i = 0; i < string_json.length(); i++) {
                    JSONObject c = string_json.getJSONObject(i);

                    String id_berita = c.getString(TAG_ID);
                    String judul = c.getString(TAG_JUDUL);
                    String link_image = c.getString(TAG_GAMBAR);

                    HashMap<String, String> map = new HashMap<String, String>();

                    map.put(TAG_ID, id_berita);
                    map.put(TAG_JUDUL, judul);
                    map.put(TAG_GAMBAR, link_image);

                    DaftarBerita.add(map);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {

            pDialog.dismiss();

            runOnUiThread(new Runnable() {
                public void run() {

                    SetListViewAdapter(DaftarBerita);

                    //Update Time..

                    // Current Date
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String formattedDate = df.format(c.getTime());

                    TextView updateTime = (TextView) findViewById(R.id.update);
                    updateTime.setText("Terakhir di Update : " + formattedDate);

                }
            });

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.loko_utama, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.refresh:
                // Single menu item is selected do something
                // Ex: launching new activity/screen or show alert message
                finish();
                startActivity(getIntent());
                Toast.makeText(MainActivity.this, "Update Data Berita", Toast.LENGTH_SHORT).show();
                getnotification();
                return true;

            case R.id.exit:
                keluar();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void keluar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah Anda Ingin" + " keluar?")
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();
    }

    public void getnotification(){

        NotificationManager notificationmgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pintent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        //   PendingIntent pintent = PendingIntent.getActivities(this,(int)System.currentTimeMillis(),intent, 0);


        Notification notif = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_menu_home)
                .setContentTitle("Berita Baru")
                .setContentText("Portal Kampus telah mengupdate berita baru")
                .setContentIntent(pintent)
                .getNotification();


        notificationmgr.notify(0,notif);
    }
}
