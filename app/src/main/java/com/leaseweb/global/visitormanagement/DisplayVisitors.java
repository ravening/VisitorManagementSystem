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
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.leaseweb.global.visitormanagement.model.Visitor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.leaseweb.global.visitormanagement.config.Config.DATABASE_PATH;
import static com.leaseweb.global.visitormanagement.config.Config.DB_HOST;
import static com.leaseweb.global.visitormanagement.config.Config.DISPLAY_VISITORS;
import static com.leaseweb.global.visitormanagement.config.Config.sharedPreferenceName;

public class DisplayVisitors extends Activity {
    private ListView userList;
    private List<Visitor> allVisitors = new ArrayList<>();
    private ProgressDialog pDialog;

    //private static String urlAllSuppliers = "http://10.11.60.100/android_connect/get_all_visitors.php";
    public String urlAllSuppliers;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_VISITORS = "visitors";
    private int success;

    SharedPreferences sharedpreferences;
    private static final String KEY_ID = "id";
    private static final String KEY_FIRST_NAME = "firstName";
    private static final String KEY_LAST_NAME = "lastName";
    private static final String KEY_REASON_FOR_VISIT = "reasonForVisit";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_BADGE = "badgeNumber";
    private static final String KEY_CONTACT_PERSON = "contactPerson";
    private static final String KEY_LICENSE_PLATE = "licensePlate";
    private static final String KEY_CHECKIN_TIME = "checkinTime";
    private static final String KEY_CHECKOUT_TIME = "checkoutTime";

    @Override
    public void onCreate(Bundle bundle) {
        String dbHost = null;
        super.onCreate(bundle);
        setContentView(R.layout.display_visitors);

        sharedpreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(DB_HOST) && ! sharedpreferences.getString(DB_HOST, "").equalsIgnoreCase("")) {
            dbHost = sharedpreferences.getString(DB_HOST, "");
            urlAllSuppliers = "http://" + dbHost + "/" + DATABASE_PATH + "/" + DISPLAY_VISITORS;
            Log.e("url is ", urlAllSuppliers);
        }

        //Log.e("db host is ", dbHost);
        if (dbHost == null) {

                Toast.makeText(getApplicationContext(),
                        "Unable to connect to database", Toast.LENGTH_LONG).show();
                super.finish();
        }

        userList = (ListView) findViewById(R.id.List);
        ArrayList<HashMap<String, String>> Visitors = new ArrayList<HashMap<String, String>>();

        if (dbHost != null && !dbHost.equalsIgnoreCase("")) {
            Log.e("db host is ", dbHost);
            try {
                Object result = new LoadAllVisitors().execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Unable to connect to database", Toast.LENGTH_LONG).show();
            super.finish();
        }

        List<Visitor> allVisitorsList = new ArrayList<Visitor>();

        Bundle result = getIntent().getExtras();
        String dateRange = null;
        if (result != null) {
            dateRange = result.getString("dateRange");
        }
        Log.e("DATE RANGE", " is " + dateRange );

        if (dateRange != null) {
            if (dateRange.equalsIgnoreCase("today")) {
                allVisitorsList = getTodaysVisitors();
            } else if (dateRange.equalsIgnoreCase("thisWeek")) {
                allVisitorsList = getThisWeekVisitors();
            } else if (dateRange.equalsIgnoreCase("lastMonth")) {
                allVisitorsList = getLastMonthVisitors();
            } else if (dateRange.equalsIgnoreCase("lastThreeMonths")) {
                allVisitorsList = getLastThreeMonthsVisitors();
            } else if (dateRange.equalsIgnoreCase("range")) {
                allVisitorsList = getDateRangeVisitorList(result);
            } else {
                allVisitorsList = allVisitors;
            }
        }

        if (allVisitorsList == null || allVisitorsList.size() == 0) {
            Toast.makeText(getApplicationContext(),
                    "No result found", Toast.LENGTH_LONG).show();
            finish();
        }

        if (allVisitorsList != null) {
            for (Visitor visitor : allVisitorsList) {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("firstName", visitor.getFirstName());
                map.put("lastName", visitor.getLastName());
                Log.d("Visitor id is ", (visitor.getId()) + " and name is " + visitor.getFirstName());
                Visitors.add(map);
            }
        }

        //get all the visitors
        final List<Visitor> visitorList = allVisitorsList;

        if (visitorList == null && visitorList.size() == 0) {
            Toast.makeText(getApplicationContext(),
                    "No Visitors to display", Toast.LENGTH_LONG).show();
            finish();
        }

        ListAdapter adapter = new SimpleAdapter(this, Visitors, R.layout.list_visitor,
                new String[]{"firstName", "lastName"}, new int[]{R.id.firstName, R.id.lastName});
        userList.setAdapter(adapter);
        Toast.makeText(getApplicationContext(), "Total visitors are " + allVisitorsList.size(), Toast.LENGTH_LONG).show();

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Visitor searchItem = visitorList.get(position);

                Intent intent = new Intent(getApplicationContext(), DisplaySearchVisitor.class);

                intent.putExtra("firstName", searchItem.getFirstName());
                intent.putExtra("lastName", searchItem.getLastName());
                intent.putExtra("phone", searchItem.getPhone());
                intent.putExtra("reasonForVisit", searchItem.getReasonForVisit());
                intent.putExtra("contactPerson", searchItem.getContactPerson());
                intent.putExtra("licensePlate", searchItem.getLicensePlate());
                intent.putExtra("badgeNumber", searchItem.getBadgeNumber());
                intent.putExtra("checkinTime", searchItem.getCheckinTime());
                intent.putExtra("checkoutTime", searchItem.getCheckoutTime());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

    }

    public List<Visitor> getTodaysVisitors() {
        List<Visitor> visitorList = new ArrayList<Visitor>();
        Date today = new Date();
        int hour = 0;
        int minutes = 0;
        int seconds = 0;

        int year = 1900 + today.getYear();
        int month = today.getMonth();
        int date = today.getDate();

        String dateTimeToday = year + "-" + (month + 1) + "-" + date + " " + hour + ":" + minutes + ":" + seconds;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateTimeToEpoch = new Date();
        try {
            dateTimeToEpoch = dateFormat.parse(dateTimeToday);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Long todayEpoch = dateTimeToEpoch.getTime();


        for (Visitor visitor : allVisitors) {
            //Log.e("visitor is ", visitor.getId() + " " + visitor.getFirstName());
            String checkinTime = visitor.getCheckinTime();
            try {
                dateTimeToEpoch = dateFormat.parse(checkinTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Long checkinTimeEpoch = dateTimeToEpoch.getTime();
            if (checkinTimeEpoch > todayEpoch) {
                visitorList.add(visitor);
            }
        }

        return visitorList;
    }

    public List<Visitor> getThisWeekVisitors() {
        List<Visitor> visitorList = new ArrayList<Visitor>();
        Date today = new Date();
        int hour = 0;
        int minutes = 0;
        int seconds = 0;

        int year = 1900 + today.getYear();
        int month = today.getMonth();
        int date = today.getDate() - 6;

        String dateTimeToday = year + "-" + (month + 1) + "-" + date + " " + hour + ":" + minutes + ":" + seconds;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateTimeToEpoch = new Date();
        try {
            dateTimeToEpoch = dateFormat.parse(dateTimeToday);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Long todayEpoch = dateTimeToEpoch.getTime();


        for (Visitor visitor : allVisitors) {
            Log.e("visitor is ", visitor.getId() + " " + visitor.getFirstName());
            String checkinTime = visitor.getCheckinTime();
            try {
                dateTimeToEpoch = dateFormat.parse(checkinTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Long checkinTimeEpoch = dateTimeToEpoch.getTime();
            if (checkinTimeEpoch > todayEpoch) {
                visitorList.add(visitor);
            }
        }

        return visitorList;
    }

    public List<Visitor> getLastMonthVisitors() {
        List<Visitor> visitorList = new ArrayList<Visitor>();
        Date today = new Date();
        int hour = 0;
        int minutes = 0;
        int seconds = 0;

        int year = 1900 + today.getYear();
        int month = today.getMonth() - 1;
        int date = today.getDate();

        String dateTimeToday = year + "-" + (month + 1) + "-" + date + " " + hour + ":" + minutes + ":" + seconds;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateTimeToEpoch = new Date();
        try {
            dateTimeToEpoch = dateFormat.parse(dateTimeToday);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Long todayEpoch = dateTimeToEpoch.getTime();


        for (Visitor visitor : allVisitors) {
            Log.e("visitor is ", visitor.getId() + " " + visitor.getFirstName());
            String checkinTime = visitor.getCheckinTime();
            try {
                dateTimeToEpoch = dateFormat.parse(checkinTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Long checkinTimeEpoch = dateTimeToEpoch.getTime();
            if (checkinTimeEpoch > todayEpoch) {
                visitorList.add(visitor);
            }
        }

        return visitorList;
    }

    public List<Visitor> getLastThreeMonthsVisitors() {
        List<Visitor> visitorList = new ArrayList<Visitor>();
        Date today = new Date();
        int hour = 0;
        int minutes = 0;
        int seconds = 0;

        int year = 1900 + today.getYear();
        int month = today.getMonth() - 3;
        int date = today.getDate();

        String dateTimeToday = year + "-" + (month + 1) + "-" + date + " " + hour + ":" + minutes + ":" + seconds;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateTimeToEpoch = new Date();
        try {
            dateTimeToEpoch = dateFormat.parse(dateTimeToday);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Long todayEpoch = dateTimeToEpoch.getTime();


        for (Visitor visitor : allVisitors) {
            String checkinTime = visitor.getCheckinTime();
            try {
                dateTimeToEpoch = dateFormat.parse(checkinTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Long checkinTimeEpoch = dateTimeToEpoch.getTime();
            if (checkinTimeEpoch > todayEpoch) {
                visitorList.add(visitor);
            }
        }

        return visitorList;
    }

    public List<Visitor> getDateRangeVisitorList(Bundle bundle) {
        List<Visitor> visitorList = new ArrayList<Visitor>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateTimeToEpoch = new Date();
        Long todayEpoch = dateTimeToEpoch.getTime();

        String fromDate = bundle.getString("fromDate");
        String toDate = bundle.getString("toDate");
        Long fromDateEpoch = getEpochValue(fromDate);
        Long toDateEpoch = getEpochValue(toDate);

        for (Visitor visitor : allVisitors) {
            Log.e("visitor is ", visitor.getId() + " " + visitor.getFirstName());
            String checkinTime = visitor.getCheckinTime();
            try {
                dateTimeToEpoch = dateFormat.parse(checkinTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Long checkinTimeEpoch = dateTimeToEpoch.getTime();

            if ((checkinTimeEpoch > fromDateEpoch) && (checkinTimeEpoch < toDateEpoch) ) {
                visitorList.add(visitor);
            }
        }

        return visitorList;
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

    class LoadAllVisitors extends AsyncTask<String, String, String> {
        JSONObject response;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DisplayVisitors.this);
            pDialog.setMessage("Loading visitors. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // getting JSON string from URL
            HttpJsonParser jsonParser = new HttpJsonParser();

            response = jsonParser.makeHttpRequest(urlAllSuppliers,"GET",null);
            Log.e("message", response.toString());
            try {
                success = response.getInt(TAG_SUCCESS);
                JSONArray employeeArray = response.getJSONArray(TAG_VISITORS);
                for (int i =0; i< employeeArray.length(); i++) {
                    Visitor visitor = new Visitor();
                    JSONObject employeeObj = employeeArray.getJSONObject(i);
                    visitor.setId(employeeObj.getLong(KEY_ID));
                    visitor.setFirstName(employeeObj.getString(KEY_FIRST_NAME));
                    visitor.setLastName(employeeObj.getString(KEY_LAST_NAME));
                    visitor.setPhone(employeeObj.getString(KEY_PHONE));
                    visitor.setReasonForVisit(employeeObj.getString(KEY_REASON_FOR_VISIT));
                    visitor.setContactPerson(employeeObj.getString(KEY_CONTACT_PERSON));
                    visitor.setLicensePlate(employeeObj.getString(KEY_LICENSE_PLATE));
                    visitor.setBadgeNumber(employeeObj.getInt(KEY_BADGE));
                    visitor.setCheckinTime(employeeObj.getString(KEY_CHECKIN_TIME));
                    visitor.setCheckoutTime(employeeObj.getString(KEY_CHECKOUT_TIME));
                    allVisitors.add(visitor);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return  null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String result) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        //*JSONArray employeeArray = response.getJSONArray(T);
                        JSONArray employeeArray = response.getJSONArray(TAG_VISITORS);
                        for (int i =0; i< employeeArray.length(); i++) {
                            Visitor visitor = new Visitor();
                            JSONObject employeeObj = employeeArray.getJSONObject(i);
                            visitor.setId(employeeObj.getLong(KEY_ID));
                            visitor.setFirstName(employeeObj.getString(KEY_FIRST_NAME));
                            visitor.setLastName(employeeObj.getString(KEY_LAST_NAME));
                            visitor.setPhone(employeeObj.getString(KEY_PHONE));
                            visitor.setReasonForVisit(employeeObj.getString(KEY_REASON_FOR_VISIT));
                            visitor.setContactPerson(employeeObj.getString(KEY_CONTACT_PERSON));
                            visitor.setLicensePlate(employeeObj.getString(KEY_LICENSE_PLATE));
                            visitor.setBadgeNumber(employeeObj.getInt(KEY_BADGE));
                            visitor.setCheckinTime(employeeObj.getString(KEY_CHECKIN_TIME));
                            visitor.setCheckoutTime(employeeObj.getString(KEY_CHECKOUT_TIME));
                            allVisitors.add(visitor);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

    }
}
