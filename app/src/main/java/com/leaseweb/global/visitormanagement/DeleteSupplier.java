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
import static com.leaseweb.global.visitormanagement.config.Config.DELETE_SUPPLIER;
import static com.leaseweb.global.visitormanagement.config.Config.sharedPreferenceName;

public class DeleteSupplier extends Activity {
    private ProgressDialog pDialog;
    private static String deleteSupplierUrl;
    private static final String TAG_SUCCESS = "success";
    public String duration = "";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String dbHost = null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_supplier);

        sharedPreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(DB_HOST) && ! sharedPreferences.getString(DB_HOST, "").equalsIgnoreCase("")) {
            dbHost = sharedPreferences.getString(DB_HOST, "");
            deleteSupplierUrl = "http://" + dbHost + "/" + DATABASE_PATH + "/" + DELETE_SUPPLIER;
        }

        if (dbHost == null || dbHost.equalsIgnoreCase("")) {
            Toast.makeText(getApplicationContext(),
                    "Unable to connect to database", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void finish(View view) {
        super.finish();
    }
    public void deleteAllSuppliers(View view) {
        duration = "all";
        delete();
        Toast.makeText(this, "All suppliers deleted successfully", Toast.LENGTH_LONG).show();
    }

    public void deleteSuppliersOlderThanThreeMonths(View view) {
        duration = "threeMonths";
        delete();
        Toast.makeText(this, "All suppliers older than three months deleted successfully", Toast.LENGTH_LONG).show();
    }

    public void deleteSuppliersOlderThanOneWeek(View view) {
        duration = "oneWeek";
        delete();
        Toast.makeText(this, "All suppliers older than one week deleted successfully", Toast.LENGTH_LONG).show();
    }

    public void deleteSingleSupplier(View view) {
        Intent deleteSupplier = new Intent(this, DeleteGuest.class);
        deleteSupplier.putExtra("guestType", 2);
        startActivity(deleteSupplier);
    }

    public void deleteSupplierByDateRange(View view) {
        Intent deleteSupplier = new Intent(this, DeleteGuestRange.class);
        deleteSupplier.putExtra("guestType", 2);
        startActivity(deleteSupplier);
    }

    public void delete() {
        Object result = "";
        try {
            result = new DeleteSupplier.DeleteSupplierHandler().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    class DeleteSupplierHandler extends AsyncTask<String, String, String> {
        JSONObject json;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DeleteSupplier.this);
            pDialog.setMessage("Deleting supplier..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            String duration = DeleteSupplier.this.duration;

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
            json = jsonParser.makeHttpRequest(deleteSupplierUrl,
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
