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
import org.json.simple.parser.JSONParser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.leaseweb.global.visitormanagement.config.Config.CHECKIN_EMPLOYEE;
import static com.leaseweb.global.visitormanagement.config.Config.DATABASE_PATH;
import static com.leaseweb.global.visitormanagement.config.Config.DB_HOST;
import static com.leaseweb.global.visitormanagement.config.Config.sharedPreferenceName;

public class CheckinEmployee extends Activity {
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    static final String TAG_SUCCESS = "success";
    private static String checkinEmployeeUrl;

    public EditText firstName;
    public EditText lastName;
    public EditText department;
    public EditText phoneNumber;
    public EditText badgeNumber;
    SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle bundle) {
        String dbHost = null;
        super.onCreate(bundle);
        setContentView(R.layout.checkin_employee);

        sharedPreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(DB_HOST) && ! sharedPreferences.getString(DB_HOST, "").equalsIgnoreCase("")) {
            dbHost = sharedPreferences.getString(DB_HOST, "");
            checkinEmployeeUrl = "http://" + dbHost + "/" + DATABASE_PATH + "/" + CHECKIN_EMPLOYEE;
            Log.e("url is ", checkinEmployeeUrl);
        }

    }

    @Override
    public void finish() {
        super.finish();
    }

    public void checkin(View view) {
        firstName = (EditText) findViewById(R.id.firstNameText);
        lastName = (EditText) findViewById(R.id.lastNameText);
        badgeNumber = (EditText) findViewById(R.id.badgeNumberText);
        department = (EditText) findViewById(R.id.departmentText);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);

        if (!validateFields(firstName, lastName, badgeNumber, department, phoneNumber)) {
            return;
        }

        Object result = null;
        try {
            result = new CheckinEmployeeHandler().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (result != null) {
            Toast.makeText(this, firstName.getText().toString() + " Checkedin successfully", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Unable to checkin " + firstName.getText().toString(), Toast.LENGTH_LONG).show();
        }
    }

    public boolean validateFields(EditText firstName, EditText lastName,
                                  EditText badgeNumber, EditText department, EditText phoneNumber) {
        if (firstName.getText() == null || firstName.getText().toString().length() == 0) {
            Toast.makeText(this, "First name cannot be empty", Toast.LENGTH_LONG).show();
            return false;
        }

        if (lastName.getText() == null || lastName.getText().toString().length() == 0) {
            Toast.makeText(this, "Last name cannot be empty", Toast.LENGTH_LONG).show();
            return false;
        }

        if (badgeNumber.getText() == null || badgeNumber.getText().toString().length() == 0) {
            Toast.makeText(this, "Please enter the Badge number", Toast.LENGTH_LONG).show();
            return false;
        }

        if (department.getText() == null || department.getText().toString().length() == 0) {
            Toast.makeText(this, "Please enter the department name", Toast.LENGTH_LONG).show();
            return  false;
        }

        if (phoneNumber.getText() == null || phoneNumber.getText().toString().length() == 0) {
            Toast.makeText(this, "Phone number cannot be empty", Toast.LENGTH_LONG).show();
            return false;
        }
        return  true;
    }

    public void clearDetails(View view) {
        EditText name = (EditText) findViewById(R.id.firstNameText);
        name.setText("");
        EditText lastName = (EditText) findViewById(R.id.lastNameText);
        lastName.setText("");
        EditText badgeNumber = (EditText) findViewById(R.id.badgeNumberText);
        badgeNumber.setText("");
        EditText department = (EditText) findViewById(R.id.departmentText);
        department.setText("");
        EditText phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        phoneNumber.setText("");
    }

    public void finish(View view) {
        super.finish();
    }


    class CheckinEmployeeHandler extends AsyncTask<String, String, String> {
        JSONObject json;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CheckinEmployee.this);
            pDialog.setMessage("Checkin Employee..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            String firstName = CheckinEmployee.this.firstName.getText().toString();
            String lastName = CheckinEmployee.this.lastName.getText().toString();
            String phone = CheckinEmployee.this.phoneNumber.getText().toString();
            String badgeNumber = CheckinEmployee.this.badgeNumber.getText().toString();
            String department = CheckinEmployee.this.department.getText().toString();

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String checkinTime = df.format(c.getTime());

            Map<String, String> employeeDetails = new HashMap<>();
            employeeDetails.put("firstName", firstName);
            employeeDetails.put("lastName", lastName);
            employeeDetails.put("phoneNumber", phone);
            employeeDetails.put("department", department);
            employeeDetails.put("badgeNumber", badgeNumber);
            employeeDetails.put("checkinTime", checkinTime);

            // getting JSON Object
            // Note that checkout employee url accepts POST method
            HttpJsonParser jsonParser = new HttpJsonParser();
            json = jsonParser.makeHttpRequest(checkinEmployeeUrl,
                    "POST", employeeDetails);

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Success: ", json.toString());
                    return "true";
                } else {
                    // failed to checkin employee
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
