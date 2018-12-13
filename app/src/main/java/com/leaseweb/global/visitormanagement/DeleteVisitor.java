package com.leaseweb.global.visitormanagement;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.leaseweb.global.visitormanagement.config.Config.DATABASE_PATH;
import static com.leaseweb.global.visitormanagement.config.Config.DB_HOST;
import static com.leaseweb.global.visitormanagement.config.Config.DELETE_VISITOR;
import static com.leaseweb.global.visitormanagement.config.Config.sharedPreferenceName;

public class DeleteVisitor extends Activity {
    private ProgressDialog pDialog;
    private static String deleteVisitorUrl = "http://10.11.60.100/android_connect/delete_visitors.php";
    private static final String TAG_SUCCESS = "success";
    public String duration = "";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String dbHost = null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_visitor);

        sharedPreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(DB_HOST) && ! sharedPreferences.getString(DB_HOST, "").equalsIgnoreCase("")) {
            dbHost = sharedPreferences.getString(DB_HOST, "");
            deleteVisitorUrl = "http://" + dbHost + "/" + DATABASE_PATH + "/" + DELETE_VISITOR;
        }

        if (dbHost == null || dbHost.equalsIgnoreCase("")) {
            Toast.makeText(getApplicationContext(),
                    "Unable to connect to database", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void deleteAllVisitors(View view) {
        duration = "all";
        delete();
        Toast.makeText(this, "All visitors deleted successfully", Toast.LENGTH_LONG).show();
    }

    public void deleteVisitorsOlderThanThreeMonths(View view) {
        duration = "threeMonths";
        delete();
        Toast.makeText(this, "All visitors older than three months deleted successfully", Toast.LENGTH_LONG).show();
    }

    public void deleteVisitorsOlderThanOneWeek(View view) {
        duration = "oneWeek";
        delete();
        Toast.makeText(this, "All visitors older than one week deleted successfully", Toast.LENGTH_LONG).show();
    }

    public void deleteSingleVisitor(View view) {
        Intent deleteVisitor = new Intent(this, DeleteGuest.class);
        deleteVisitor.putExtra("guestType", 0);
        startActivity(deleteVisitor);
    }

    public void deleteVisitorByDateRange(View view) {
        Intent deleteVisitor = new Intent(this, DeleteGuestRange.class);
        deleteVisitor.putExtra("guestType", 0);
        startActivity(deleteVisitor);
    }

    public void delete() {
        Object result = "";
        try {
            result = new DeleteVisitorHandler().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void finish(View view) {
        super.finish();
    }

    class DeleteVisitorHandler extends AsyncTask<String, String, String> {
        JSONObject json;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DeleteVisitor.this);
            pDialog.setMessage("Deleting visitor..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            String duration = DeleteVisitor.this.duration;

            Map<String, String> guestDetails = new HashMap<>();
            if (duration.equalsIgnoreCase("all")) {
                guestDetails.put("all", "true");
            } else if (duration.equalsIgnoreCase("threeMonths")) {
                guestDetails.put("threeMonths", "true");
            } else if (duration.equalsIgnoreCase("oneWeek")) {
                guestDetails.put("oneWeek", "true");
            }

            // getting JSON Object
            // Note that create product url accepts POST method
            HttpJsonParser jsonParser = new HttpJsonParser();
            json = jsonParser.makeHttpRequest(deleteVisitorUrl,
                    "POST", guestDetails);

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Success: ", json.toString());
                    return "true";
                } else {
                    Log.e("Error: ", json.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }
    }
}
