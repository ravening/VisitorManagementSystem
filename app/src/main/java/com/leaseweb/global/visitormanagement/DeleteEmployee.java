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
import static com.leaseweb.global.visitormanagement.config.Config.DELETE_EMPLOYEE;
import static com.leaseweb.global.visitormanagement.config.Config.sharedPreferenceName;

public class DeleteEmployee extends Activity {
    private ProgressDialog pDialog;
    private static String deleteEmployeeUrl;
    private static final String TAG_SUCCESS = "success";
    public String duration = "";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String dbHost = null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_employee);

        sharedPreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(DB_HOST) && ! sharedPreferences.getString(DB_HOST, "").equalsIgnoreCase("")) {
            dbHost = sharedPreferences.getString(DB_HOST, "");
            deleteEmployeeUrl = "http://" + dbHost + "/" + DATABASE_PATH + "/" + DELETE_EMPLOYEE;
        }

    }

    public void finish(View view) {
        super.finish();
    }

    public void deleteAllEmployees(View view) {
        duration = "all";
        delete();
        Toast.makeText(this, "All employees deleted successfully", Toast.LENGTH_LONG).show();
    }

    public void deleteEmployeesOlderThanThreeMonths(View view) {
        duration = "threeMonths";
        delete();
        Toast.makeText(this, "All employees older than three months deleted successfully", Toast.LENGTH_LONG).show();
    }

    public void deleteEmployeesOlderThanOneWeek(View view) {
        duration = "oneWeek";
        delete();
        Toast.makeText(this, "All employees older than one week deleted successfully", Toast.LENGTH_LONG).show();
    }

    public void deleteSingleEmployee(View view) {
        Intent deleteEmployee = new Intent(this, DeleteGuest.class);
        deleteEmployee.putExtra("guestType", 1);
        startActivity(deleteEmployee);
    }

    public void deleteEmployeeByDateRange(View view) {
        Intent deleteEmployee = new Intent(this, DeleteGuestRange.class);
        deleteEmployee.putExtra("guestType", 1);
        startActivity(deleteEmployee);
    }

    public void delete() {
        try {
            Object result = new DeleteEmployeeHandler().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    class DeleteEmployeeHandler extends AsyncTask<String, String, String> {
        JSONObject json;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DeleteEmployee.this);
            pDialog.setMessage("Deleting Employee..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            String duration = DeleteEmployee.this.duration;

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
            json = jsonParser.makeHttpRequest(deleteEmployeeUrl,
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
