package com.example.rangga.portalkampus;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * Created by Rangga on 20/08/2016.
 */
public class DetailBerita extends Activity {


    JSONArray string_json = null;

    String idberita;

    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    public static final String TAG_ID = "id";
    public static final String TAG_JUDUL = "judul";
    public static final String TAG_GAMBAR = "gambar";
    private static final String url_detail_berita = "http://10.0.3.2/lokomedia/lokoandro/detailberita.php";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_list_item);

        Intent i = getIntent();

        idberita = i.getStringExtra(TAG_ID);


        new AmbilDetailBerita().execute();

    }

    class AmbilDetailBerita extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DetailBerita.this);
            pDialog.setMessage("Mohon Tunggu ... !");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... params) {

            try {

                List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                params1.add(new BasicNameValuePair("idberita",idberita));

                JSONObject json = jsonParser.makeHttpRequest(
                        url_detail_berita, "GET", params1);
                string_json = json.getJSONArray("berita");

                runOnUiThread(new Runnable() {
                    public void run() {

                        ImageView thumb_image = (ImageView) findViewById(R.id.imageView1);
                        TextView judul = (TextView) findViewById(R.id.judul);
                        TextView detail = (TextView) findViewById(R.id.detail);
                        TextView isi = (TextView) findViewById(R.id.content);

                        try {
                            // ambil objek member pertama dari JSON Array
                            JSONObject ar = string_json.getJSONObject(0);
                            String judul_d = ar.getString("judul");
                            String detail_d = ar.getString("hari")+" , "+ar.getString("tanggal")+" Diposting Oleh : "+ar.getString("username");
                            String isi_d = ar.getString("isi");

                            judul.setText(judul_d);
                            detail.setText(detail_d);
                            isi.setText(isi_d);
                            ImageLoader imageLoader = new ImageLoader(getApplicationContext());
                            imageLoader.DisplayImage(ar.getString(TAG_GAMBAR),thumb_image);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {

            pDialog.dismiss();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.home:
                // Single menu item is selected do something
                // Ex: launching new activity/screen or show alert message
                finish();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
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

}
