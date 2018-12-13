package com.leaseweb.global.visitormanagement;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.leaseweb.global.visitormanagement.config.Config.DATABASE_PATH;
import static com.leaseweb.global.visitormanagement.config.Config.DB_HOST;
import static com.leaseweb.global.visitormanagement.config.Config.DELETE_EMPLOYEE;
import static com.leaseweb.global.visitormanagement.config.Config.DELETE_SUPPLIER;
import static com.leaseweb.global.visitormanagement.config.Config.DELETE_VISITOR;
import static com.leaseweb.global.visitormanagement.config.Config.sharedPreferenceName;

public class DeleteGuestRange extends Activity {
    DatePickerFragment customFrom;
    DatePickerFragment customTo;
    Button btnEventDateTimeFrom;
    Button btnEventDateTimeTo;
    String epochValueFrom = null;
    String epochValueTo = null;
    int guestType;
    private static final int VISITOR = 0;
    private static final int EMPLOYEE = 1;

    private ProgressDialog pDialog;
    private static String deleteVisitorUrl;
    private static String deleteEmployeeUrl;
    private static String deleteSupplierUrl;
    private static final String TAG_SUCCESS = "success";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String dbHost = null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_range_picker);
        btnEventDateTimeFrom = ((Button) findViewById(R.id.pickDateFrom));
        btnEventDateTimeTo = (Button) findViewById(R.id.pickDateTo);

        sharedPreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(DB_HOST) && ! sharedPreferences.getString(DB_HOST, "").equalsIgnoreCase("")) {
            dbHost = sharedPreferences.getString(DB_HOST, "");
            deleteEmployeeUrl = "http://" + dbHost + "/" + DATABASE_PATH + "/" + DELETE_EMPLOYEE;
            deleteVisitorUrl = "http://" + dbHost + "/" + DATABASE_PATH + "/" + DELETE_VISITOR;
            deleteSupplierUrl = "http://" + dbHost + "/" + DATABASE_PATH + "/" + DELETE_SUPPLIER;
        }

        if (dbHost == null || dbHost.equalsIgnoreCase("")) {
            Toast.makeText(getApplicationContext(),
                    "Unable to connect to database", Toast.LENGTH_LONG).show();
            finish();
        }
        Bundle result = getIntent().getExtras();
        guestType = result.getInt("guestType");

        customFrom = new DatePickerFragment(this,
                new DatePickerFragment.ICustomDateTimeListener() {
                    @Override
                    public void onSet(Dialog dialog, Calendar calendarSelected,
                                      Date dateSelected, int year, String monthFullName,
                                      String monthShortName, int monthNumber, int date,
                                      String weekDayFullName, String weekDayShortName,
                                      int hour24, int hour12, int min, int sec,
                                      String AM_PM) {
                        TextView showDate = ((TextView) findViewById(R.id.showDateFrom));
                        String fromDateTime = year + "-" + (monthNumber + 1) + "-" + calendarSelected.get(Calendar.DAY_OF_MONTH) + " " + hour24 + ":" + min + ":" + sec;
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date fromDateTimeEpoch = new Date();
                        try {
                            fromDateTimeEpoch = dateFormat.parse(fromDateTime);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        epochValueFrom = fromDateTime;
                        showDate.setText(fromDateTime);
                    }

                    @Override
                    public void onCancel() {

                    }
                });

        customTo = new DatePickerFragment(this,
                new DatePickerFragment.ICustomDateTimeListener() {

                    @Override
                    public void onSet(Dialog dialog, Calendar calendarSelected,
                                      Date dateSelected, int year, String monthFullName,
                                      String monthShortName, int monthNumber, int date,
                                      String weekDayFullName, String weekDayShortName,
                                      int hour24, int hour12, int min, int sec,
                                      String AM_PM) {
                        TextView showDate = ((TextView) findViewById(R.id.showDateTo));
                        String dateTimeTo = year + "-" + (monthNumber + 1) + "-" + calendarSelected.get(Calendar.DAY_OF_MONTH) + " " + hour24 + ":" + min + ":" + sec;
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date dateTimeToEpoch = new Date();
                        try {
                            dateTimeToEpoch = dateFormat.parse(dateTimeTo);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        epochValueTo = dateTimeTo;

                        Log.e("current time is ", String.valueOf(dateTimeTo));
                        Log.e("getTime is ", String.valueOf(dateTimeToEpoch.getTime()));

                        showDate.setText(dateTimeTo);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
        /**
         * Pass Directly current time format it will return AM and PM if you set
         * false
         */
        customFrom.set24HourFormat(true);
        customTo.set24HourFormat(true);
        /**
         * Pass Directly current data and time to show when it pop up
         */
        customFrom.setDate(Calendar.getInstance());
        btnEventDateTimeFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customFrom.showDialog();
            }
        });
        customTo.setDate(Calendar.getInstance());
        btnEventDateTimeTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customTo.showDialog();
            }
        });
    }

    public void displayRange(View view) {
        if (epochValueFrom == null || epochValueTo == null) {
            Toast.makeText(this, "Please select both From and To dates", Toast.LENGTH_LONG).show();
            return;
        }

        if (getEpochValue(epochValueFrom) > getEpochValue(epochValueTo)) {
            Toast.makeText(this, "From date cannot be greater than To date", Toast.LENGTH_LONG).show();
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

        String resultString = (String)result;
        String guest = (guestType == EMPLOYEE) ? "Employees" : "Visitors";

        if (resultString.equalsIgnoreCase("true")) {
            Toast.makeText(this, guest + " deleted succesfully", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "No " + guest + " found in this range", Toast.LENGTH_LONG).show();
        }
    }

    public Long getEpochValue(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateTimeToEpoch = new Date();
        try {
            dateTimeToEpoch = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTimeToEpoch.getTime();
    }

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
            pDialog = new ProgressDialog(DeleteGuestRange.this);
            pDialog.setMessage("Deleting guest..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            Map<String, String> guestDetails = new HashMap<>();
            guestDetails.put("range", "true");
            guestDetails.put("from", epochValueFrom);
            guestDetails.put("to", epochValueTo);

            String url = "";
            if (DeleteGuestRange.this.guestType == VISITOR) {
                url = deleteVisitorUrl;
            } else if (DeleteGuestRange.this.guestType == EMPLOYEE) {
                url = deleteEmployeeUrl;
            } else {
                url = deleteSupplierUrl;
            }
            // getting JSON Object
            // Note that create product url accepts POST method
            HttpJsonParser jsonParser = new HttpJsonParser();
            json = jsonParser.makeHttpRequest(url,
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
