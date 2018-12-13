package com.leaseweb.global.visitormanagement;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.leaseweb.global.visitormanagement.config.Config.DATABASE_PATH;
import static com.leaseweb.global.visitormanagement.config.Config.DB_HOST;
import static com.leaseweb.global.visitormanagement.config.Config.DELETE_GUEST;
import static com.leaseweb.global.visitormanagement.config.Config.sharedPreferenceName;

public class DeleteGuest extends Activity {
    int guestType;
    private static final int VISITOR = 0;
    private static final int EMPLOYEE = 1;
    private static final int supplier = 2;
    private ProgressDialog pDialog;

    private static String deleteGuestUrl;
    private static final String TAG_SUCCESS = "success";

    EditText lastName;
    EditText phoneNumber;

    private int success;
    SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle bundle) {
        String dbHost = null;
        super.onCreate(bundle);
        setContentView(R.layout.delete_single_guest);

        Bundle result = getIntent().getExtras();
        guestType = result.getInt("guestType");

        sharedPreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(DB_HOST) && ! sharedPreferences.getString(DB_HOST, "").equalsIgnoreCase("")) {
            dbHost = sharedPreferences.getString(DB_HOST, "");
            String databasePath = DATABASE_PATH;
            String fileName = DELETE_GUEST;
            deleteGuestUrl = "http://" + dbHost + "/" + DATABASE_PATH + "/" + DELETE_GUEST;
        }

        if (dbHost == null || dbHost.equalsIgnoreCase("")) {
            Toast.makeText(getApplicationContext(),
                    "Unable to connect to database", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void deleteGuest(View view) {
        lastName = (EditText) findViewById(R.id.lastNameText);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);

        if (lastName.getText() == null || lastName.getText().toString().length() == 0) {
            Toast.makeText(this, "Last name cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }

        if (phoneNumber.getText() == null || phoneNumber.getText().length() == 0) {
            Toast.makeText(this, "Phone number cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }

        Object result = "";
        try {
            result = new DeleteGuestHandler().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        String returnStatus = (String)result;
        if (returnStatus.equalsIgnoreCase("true")) {
            Toast.makeText(this, lastName.getText().toString() + " deleted successfully", Toast.LENGTH_LONG).show();
            lastName.setText("");
            phoneNumber.setText("");
        } else if (returnStatus.equalsIgnoreCase("notFound")){
            Toast.makeText(this, lastName.getText().toString() + " not found", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Unable to delete" + lastName.getText().toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void finish(View view) {
        super.finish();
    }

    @Override
    public void finish() {
        super.finish();
    }

    class DeleteGuestHandler extends AsyncTask<String, String, String> {
        JSONObject json;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DeleteGuest.this);
            pDialog.setMessage("Deleting guest..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            String lastName = DeleteGuest.this.lastName.getText().toString();
            String phone = DeleteGuest.this.phoneNumber.getText().toString();

            Map<String, String> guestDetails = new HashMap<>();
            guestDetails.put("lastName", lastName);
            guestDetails.put("phone", phone);
            if (DeleteGuest.this.guestType == EMPLOYEE) {
                guestDetails.put("employee", "true");
            } else if (DeleteGuest.this.guestType == VISITOR){
                guestDetails.put("visitor", "true");
            } else {
                guestDetails.put("supplier", "true");
            }

            // getting JSON Object
            // Note that create product url accepts POST method
            HttpJsonParser jsonParser = new HttpJsonParser();
            json = jsonParser.makeHttpRequest(deleteGuestUrl,
                    "POST", guestDetails);

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Success: ", json.toString());
                    return "true";
                } else if (success == 404){
                    Log.e("Error: ", json.toString());
                    return "notFound";
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
