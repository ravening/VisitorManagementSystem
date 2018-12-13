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

import static com.leaseweb.global.visitormanagement.config.Config.CHECKIN_VISITOR;
import static com.leaseweb.global.visitormanagement.config.Config.DATABASE_PATH;
import static com.leaseweb.global.visitormanagement.config.Config.DB_HOST;
import static com.leaseweb.global.visitormanagement.config.Config.sharedPreferenceName;

public class checkinVisitor extends Activity {
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    static String checkinVisitorURL;
    static final String TAG_SUCCESS = "success";

    public EditText firstName;
    public EditText lastName;
    public EditText reasonForVisit;
    public EditText contactPerson;
    public EditText licensePlate;
    public EditText phone;
    public EditText badgeNumber;
    SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle bundle) {
        String dbHost = null;
        super.onCreate(bundle);
        setContentView(R.layout.checkin_visitor);

        sharedPreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(DB_HOST) && ! sharedPreferences.getString(DB_HOST, "").equalsIgnoreCase("")) {
            dbHost = sharedPreferences.getString(DB_HOST, "");
            checkinVisitorURL = "http://" + dbHost + "/" + DATABASE_PATH + "/" + CHECKIN_VISITOR;
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    public void checkin(View view) {
        DatabaseHandler db = new DatabaseHandler(this);

        firstName = (EditText) findViewById(R.id.firstNameText);
        lastName = (EditText) findViewById(R.id.lastNameText);
        phone = (EditText) findViewById(R.id.phoneNumber);
        contactPerson = (EditText) findViewById(R.id.contactPerson);
        reasonForVisit = (EditText) findViewById(R.id.reasonForVisit);
        licensePlate = (EditText) findViewById(R.id.licensePlateText);
        badgeNumber = (EditText) findViewById(R.id.badgeNumberText);

        if (!validateFields(firstName, lastName, phone, contactPerson, reasonForVisit, licensePlate, badgeNumber)) {
            return;
        }

        Object result = null;
        try {
            result = new CheckinVisitorHandler().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (result != null) {
            Toast.makeText(this, firstName.getText().toString() + " Checkedin successfully", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Unable to checkin visitor " + firstName.getText().toString(), Toast.LENGTH_LONG).show();
        }

    }

    public boolean validateFields(EditText firstName, EditText lastName, EditText phone, EditText contactPerson,
                                  EditText reason, EditText licensePlate, EditText badgeNumber) {
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

        if (contactPerson.getText() == null || contactPerson.getText().toString().length() == 0) {
            Toast.makeText(this, "Please enter the person name you came to meet", Toast.LENGTH_LONG).show();
            return  false;
        }

        if (contactPerson.getText().toString().length() > 50) {
            Toast.makeText(this, "Please enter valid name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (reason.getText() == null || reason.getText().toString().length() == 0) {
            Toast.makeText(this, "Please enter your reason for visit", Toast.LENGTH_LONG).show();
            return false;
        }

        if (reason.getText().toString().length() > 60) {
            Toast.makeText(this, "Reason for visit too long", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (badgeNumber.getText() == null || badgeNumber.getText().toString().length() == 0) {
            Toast.makeText(this, "Please enter the Visitor Badge number", Toast.LENGTH_LONG).show();
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

    class CheckinVisitorHandler extends AsyncTask<String, String, String> {
        JSONObject json;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(checkinVisitor.this);
            pDialog.setMessage("Checkin visitor..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            String firstName = checkinVisitor.this.firstName.getText().toString();
            String lastName = checkinVisitor.this.lastName.getText().toString();
            String phone = checkinVisitor.this.phone.getText().toString();
            String reasonForVisit = checkinVisitor.this.reasonForVisit.getText().toString();
            String contactPerson = checkinVisitor.this.contactPerson.getText().toString();
            String licensePlate = checkinVisitor.this.licensePlate.getText().toString();
            String badgeNumber = checkinVisitor.this.badgeNumber.getText().toString();

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String checkinTime = df.format(c.getTime());

            Map<String, String> visitorDetails = new HashMap<>();
            visitorDetails.put("firstName", firstName);
            visitorDetails.put("lastName", lastName);
            visitorDetails.put("phoneNumber", phone);
            visitorDetails.put("reasonForVisit", reasonForVisit);
            visitorDetails.put("contactPerson", contactPerson);
            visitorDetails.put("licensePlate", licensePlate);
            visitorDetails.put("badgeNumber", badgeNumber);
            visitorDetails.put("checkinTime", checkinTime);

            // getting JSON Object
            // Note that checkout employee url accepts POST method
            HttpJsonParser jsonParser = new HttpJsonParser();
            json = jsonParser.makeHttpRequest(checkinVisitorURL,
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
