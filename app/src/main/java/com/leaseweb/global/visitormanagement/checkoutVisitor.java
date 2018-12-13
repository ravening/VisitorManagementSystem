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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.leaseweb.global.visitormanagement.config.Config.CHECKOUT_VISITOR;
import static com.leaseweb.global.visitormanagement.config.Config.DATABASE_PATH;
import static com.leaseweb.global.visitormanagement.config.Config.DB_HOST;
import static com.leaseweb.global.visitormanagement.config.Config.sharedPreferenceName;

public class checkoutVisitor extends Activity {
    static String checkoutVisitorURL;
    static final String TAG_SUCCESS = "success";
    public EditText badgeNumber;
    private ProgressDialog pDialog;

    private static final String KEY_FIRST_NAME = "firstName";
    private static final String KEY_LAST_NAME = "lastName";
    private static final String TAG_VISITOR = "visitors";

    public String firstName = "";
    public String lastName = "";
    SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle bundle) {
        String dbHost = null;
        super.onCreate(bundle);
        setContentView(R.layout.checkout_visitor);

        sharedPreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(DB_HOST) && ! sharedPreferences.getString(DB_HOST, "").equalsIgnoreCase("")) {
            dbHost = sharedPreferences.getString(DB_HOST, "");
            checkoutVisitorURL = "http://" + dbHost + "/" + DATABASE_PATH + "/" + CHECKOUT_VISITOR;
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    public void finish(View view) {
        super.finish();
    }

    public void checkout(View view) {
        badgeNumber = (EditText) findViewById(R.id.badgeNumberText);

        if (badgeNumber.getText() == null || badgeNumber.getText().toString().length() == 0) {
            Toast.makeText(this, "Please enter the Badge number", Toast.LENGTH_LONG).show();
            return;
        }

        Object result = "";
        try {
            result = new CheckoutVisitorHandler().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        String returnStatus = (String)result;
        if (returnStatus.equalsIgnoreCase("true")) {
            Toast.makeText(this, firstName + " " + lastName + " Checkedout successfully", Toast.LENGTH_LONG).show();
            finish();
        } else if (returnStatus.equalsIgnoreCase("notfound")) {
            Toast.makeText(this, "Badge number doesn't exist or employee already checked out ", Toast.LENGTH_LONG).show();
        }

    }

    class CheckoutVisitorHandler extends AsyncTask<String, String, String> {
        JSONObject json;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(checkoutVisitor.this);
            pDialog.setMessage("Checkout Visitor..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            String badgeNumber = checkoutVisitor.this.badgeNumber.getText().toString();

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String checkoutTime = df.format(c.getTime());

            Log.e("badge number is ", badgeNumber);
            Log.e("checkout time is ", checkoutTime);
            Map<String, String> visitorDetails = new HashMap<>();
            visitorDetails.put("badgeNumber", badgeNumber);
            visitorDetails.put("checkoutTime", checkoutTime);

            // getting JSON Object
            // Note that create product url accepts POST method
            HttpJsonParser jsonParser = new HttpJsonParser();
            json = jsonParser.makeHttpRequest(checkoutVisitorURL,
                    "POST", visitorDetails);

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    JSONArray employeeArray = json.getJSONArray(TAG_VISITOR);
                    for (int i = 0; i< employeeArray.length(); i++) {
                        JSONObject employeeObj = employeeArray.getJSONObject(i);
                        firstName = (employeeObj.getString(KEY_FIRST_NAME));
                        lastName = (employeeObj.getString(KEY_LAST_NAME));
                    }
                    Log.d("Success: ", json.toString());
                    return "true";
                } else if (success == 404){
                    // failed to find visitor
                    Log.e("Error: ", json.toString());
                    return "notfound";
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
