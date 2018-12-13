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

import com.leaseweb.global.visitormanagement.helper.DatabaseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.leaseweb.global.visitormanagement.config.Config.CHECKIN_SUPPLIER;
import static com.leaseweb.global.visitormanagement.config.Config.DATABASE_PATH;
import static com.leaseweb.global.visitormanagement.config.Config.DB_HOST;
import static com.leaseweb.global.visitormanagement.config.Config.sharedPreferenceName;

public class CheckinSupplier extends Activity {

    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    static String CheckinSupplierURL;
    static final String TAG_SUCCESS = "success";

    public EditText firstName;
    public EditText lastName;
    public EditText company;
    public EditText licensePlate;
    public EditText phone;
    public EditText badgeNumber;
    public EditText contactPerson;
    SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle bundle) {
        String dbHost = null;
        super.onCreate(bundle);
        setContentView(R.layout.checkin_supplier);

        sharedPreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(DB_HOST) && ! sharedPreferences.getString(DB_HOST, "").equalsIgnoreCase("")) {
            dbHost = sharedPreferences.getString(DB_HOST, "");
            CheckinSupplierURL = "http://" + dbHost + "/" + DATABASE_PATH + "/" + CHECKIN_SUPPLIER;
        }
    }

    public void checkin(View view) {
        DatabaseHandler db = new DatabaseHandler(this);

        firstName = (EditText) findViewById(R.id.firstNameText);
        lastName = (EditText) findViewById(R.id.lastNameText);
        phone = (EditText) findViewById(R.id.phoneNumber);
        company = (EditText) findViewById(R.id.companyText);
        licensePlate = (EditText) findViewById(R.id.licensePlateText);
        badgeNumber = (EditText) findViewById(R.id.badgeNumberText);
        contactPerson = (EditText) findViewById(R.id.contactPerson);

        if (!validateFields(firstName, lastName, phone, company, contactPerson, licensePlate, badgeNumber)) {
            return;
        }

        Object result = null;
        try {
            result = new CheckinSupplier.CheckinSupplierHandler().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (result != null) {
            Toast.makeText(this, firstName.getText().toString() + " Checkedin successfully", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Unable to checkin supplier " + firstName.getText().toString(), Toast.LENGTH_LONG).show();
        }

    }

    public boolean validateFields(EditText firstName, EditText lastName, EditText phone, EditText company,
                                  EditText contactPerson, EditText licensePlate, EditText badgeNumber) {
        if (firstName.getText() == null || firstName.getText().toString().length() == 0) {
            Toast.makeText(this, "First name cannot be empty", Toast.LENGTH_LONG).show();
            return false;
        }

        if (lastName.getText() == null || lastName.getText().toString().length() == 0) {
            Toast.makeText(this, "Last name cannot be empty", Toast.LENGTH_LONG).show();
            return false;
        }

        if (phone.getText() == null || phone.getText().length() == 0) {
            Toast.makeText(this, "Phone number cannot be empty", Toast.LENGTH_LONG).show();
            return false;
        }

        if (company.getText() == null || company.getText().toString().length() == 0) {
            Toast.makeText(this, "Please enter the company you are coming from", Toast.LENGTH_LONG).show();
            return  false;
        }

        if (contactPerson.getText() == null || contactPerson.getText().length() == 0) {
            Toast.makeText(this, "Enter the person name you came to visit", Toast.LENGTH_LONG).show();
            return false;
        }

        if (licensePlate.getText().toString().length() > 10) {
            Toast.makeText(this, "Invalid license plate number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (badgeNumber.getText() == null || badgeNumber.getText().toString().length() == 0) {
            Toast.makeText(this, "Please enter the Badge number", Toast.LENGTH_LONG).show();
            return false;
        }

        return  true;
    }

    public void clearDetails(View view) {
        EditText name = (EditText) findViewById(R.id.firstNameText);
        name.setText("");
        EditText lastName = (EditText) findViewById(R.id.lastNameText);
        lastName.setText("");
        EditText phone = (EditText) findViewById(R.id.phoneNumber);
        phone.setText("");
        EditText contactPerson = (EditText) findViewById(R.id.contactPerson);
        contactPerson.setText("");
        EditText reasonForVisit = (EditText) findViewById(R.id.reasonForVisit);
        reasonForVisit.setText("");
        EditText licensePlate = (EditText) findViewById(R.id.licensePlateText);
        licensePlate.setText("");
        EditText badgeNumber = (EditText) findViewById(R.id.badgeNumberText);
        badgeNumber.setText("");
    }

    public void finish(View view) {
        super.finish();
    }

    class CheckinSupplierHandler extends AsyncTask<String, String, String> {
        JSONObject json;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CheckinSupplier.this);
            pDialog.setMessage("Checkin visitor..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            String firstName = CheckinSupplier.this.firstName.getText().toString();
            String lastName = CheckinSupplier.this.lastName.getText().toString();
            String phone = CheckinSupplier.this.phone.getText().toString();
            String company = CheckinSupplier.this.company.getText().toString();
            String licensePlate = CheckinSupplier.this.licensePlate.getText().toString();
            String badgeNumber = CheckinSupplier.this.badgeNumber.getText().toString();
            String contactPerson = CheckinSupplier.this.contactPerson.getText().toString();

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String checkinTime = df.format(c.getTime());

            Map<String, String> visitorDetails = new HashMap<>();
            visitorDetails.put("firstName", firstName);
            visitorDetails.put("lastName", lastName);
            visitorDetails.put("phoneNumber", phone);
            visitorDetails.put("company", company);
            visitorDetails.put("contactPerson", contactPerson);
            visitorDetails.put("licensePlate", licensePlate);
            visitorDetails.put("badgeNumber", badgeNumber);
            visitorDetails.put("checkinTime", checkinTime);

            // getting JSON Object
            // Note that checkout employee url accepts POST method
            HttpJsonParser jsonParser = new HttpJsonParser();
            json = jsonParser.makeHttpRequest(CheckinSupplierURL,
                    "POST", visitorDetails);

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Success: ", json.toString());
                    return "true";
                } else {
                    // failed to checkin visitor
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
