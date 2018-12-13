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

import com.leaseweb.global.visitormanagement.model.Employee;

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
import static com.leaseweb.global.visitormanagement.config.Config.DISPLAY_EMPLOYEES;
import static com.leaseweb.global.visitormanagement.config.Config.sharedPreferenceName;

//import org.json.simple.parser.JSONParser;

public class DisplayEmployees extends Activity {
    private ListView userList;
    private List<Employee> allEmployees = new ArrayList<>();
    private ProgressDialog pDialog;

    private static String urlAllEmployees;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "employees";
    private int success;

    private static final String KEY_ID = "id";
    private static final String KEY_FIRST_NAME = "firstName";
    private static final String KEY_LAST_NAME = "lastName";
    private static final String KEY_DEPARTMENT = "department";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_BADGE = "badgeNumber";
    private static final String KEY_CHECKIN_TIME = "checkinTime";
    private static final String KEY_CHECKOUT_TIME = "checkoutTime";
    SharedPreferences sharedpreferences;
    JSONArray products = null;

    @Override
    public void onCreate(Bundle bundle) {
        String dbHost = null;
        super.onCreate(bundle);
        setContentView(R.layout.display_employees);

        userList = (ListView) findViewById(R.id.List);
        ArrayList<HashMap<String, String>> Employees = new ArrayList<HashMap<String, String>>();

        sharedpreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(DB_HOST) && ! sharedpreferences.getString(DB_HOST, "").equalsIgnoreCase("")) {
            dbHost = sharedpreferences.getString(DB_HOST, "");
            urlAllEmployees = "http://" + dbHost + "/" + DATABASE_PATH + "/" + DISPLAY_EMPLOYEES;
        }

        if (dbHost == null || dbHost.equalsIgnoreCase("")) {
            Toast.makeText(getApplicationContext(),
                    "Unable to connect to database", Toast.LENGTH_LONG).show();
            finish();
        }

        try {
            Object result = new LoadAllEmployees().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //get all the employees
        List<Employee> allEmployeeList = new ArrayList<Employee>();

        Bundle result = getIntent().getExtras();
        String dateRange = null;
        Log.e("BUNDLE is ", String.valueOf(result));
        if (result != null) {
            dateRange = result.getString("dateRange");
        }
        Log.e("DATE RANGE", " is " + dateRange );

        if (dateRange != null) {
            if (dateRange.equalsIgnoreCase("today")) {
                allEmployeeList = getTodaysEmployeeList();
            } else if (dateRange.equalsIgnoreCase("thisWeek")) {
                allEmployeeList = getThisWeekEmployeeList();
            } else if (dateRange.equalsIgnoreCase("lastMonth")) {
                allEmployeeList = getLastMonthEmployeeList();
            } else if (dateRange.equalsIgnoreCase("lastThreeMonths")) {
                allEmployeeList = getLastThreeMonthsEmployeeList();
            } else if (dateRange.equalsIgnoreCase("range")) {
                allEmployeeList = getDateRangeEmployeeList(result);
            } else {
                allEmployeeList = allEmployees;
            }
        }

        if (allEmployeeList == null || allEmployeeList.size() == 0) {
            Toast.makeText(getApplicationContext(),
                    "No result found", Toast.LENGTH_LONG).show();
            finish();
        }

        if (allEmployeeList != null) {
            for (Employee employee : allEmployeeList) {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("firstName", employee.getFirstName());
                map.put("lastName", employee.getLastName());
                Log.d("Employee id is ", (employee.getId()) + " and name is " + employee.getFirstName());
                Employees.add(map);
            }
        }

        final List<Employee> employeeList = allEmployeeList;
        if (employeeList == null || employeeList.size() == 0) {
            Toast.makeText(getApplicationContext(),
                    "No Employees to display", Toast.LENGTH_LONG).show();
            finish();
        }
        ListAdapter adapter = new SimpleAdapter(this, Employees, R.layout.list_visitor,
                new String[]{"firstName", "lastName"}, new int[]{R.id.firstName, R.id.lastName});
        userList.setAdapter(adapter);
        Toast.makeText(getApplicationContext(), "Total employees are " + allEmployeeList.size(), Toast.LENGTH_LONG).show();

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Employee searchItem = employeeList.get(position);

                Intent intent = new Intent(getApplicationContext(), DisplaySearchEmployee.class);

                intent.putExtra("firstName", searchItem.getFirstName());
                intent.putExtra("lastName", searchItem.getLastName());
                intent.putExtra("badgeNumber", searchItem.getBadgeNumber());
                intent.putExtra("department", searchItem.getDepartment());
                intent.putExtra("phoneNumber", searchItem.getPhoneNumber());
                intent.putExtra("checkinTime", searchItem.getCheckinTime());
                intent.putExtra("checkoutTime", searchItem.getCheckoutTime());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

    }

    public List<Employee> getTodaysEmployeeList() {
        List<Employee> employeeList = new ArrayList<Employee>();
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


        for (Employee employee : allEmployees) {
            Log.e("employee is ", employee.getId() + " " + employee.getFirstName());
            String checkinTime = employee.getCheckinTime();
            try {
                dateTimeToEpoch = dateFormat.parse(checkinTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Long checkinTimeEpoch = dateTimeToEpoch.getTime();
            Log.e("EPOCH TIMe", "today epoch is " + todayEpoch);
            Log.e("EPOCH TIMe", "CHECKIN  epoch is " + checkinTimeEpoch);
            Log.e("Times", "today time is " + dateTimeToday + " and checkin time is " + checkinTime);
            if (checkinTimeEpoch > todayEpoch) {
                Log.e("today checkin", "YES");
                employeeList.add(employee);
            }
        }

        return employeeList;
    }

    public List<Employee> getThisWeekEmployeeList() {
        List<Employee> employeeList = new ArrayList<Employee>();
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


        for (Employee employee : allEmployees) {
            Log.e("employee is ", employee.getId() + " " + employee.getFirstName());
            String checkinTime = employee.getCheckinTime();
            try {
                dateTimeToEpoch = dateFormat.parse(checkinTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Long checkinTimeEpoch = dateTimeToEpoch.getTime();
            Log.e("EPOCH TIMe", "today epoch is " + todayEpoch);
            Log.e("EPOCH TIMe", "CHECKIN  epoch is " + checkinTimeEpoch);
            Log.e("Times", "today time is " + dateTimeToday + " and checkin time is " + checkinTime);
            if (checkinTimeEpoch > todayEpoch) {
                Log.e("today checkin", "YES");
                employeeList.add(employee);
            }
        }

        return employeeList;
    }

    public List<Employee> getLastMonthEmployeeList() {
        List<Employee> employeeList = new ArrayList<Employee>();
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


        for (Employee employee : allEmployees) {
            Log.e("employee is ", employee.getId() + " " + employee.getFirstName());
            String checkinTime = employee.getCheckinTime();
            try {
                dateTimeToEpoch = dateFormat.parse(checkinTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Long checkinTimeEpoch = dateTimeToEpoch.getTime();
            Log.e("EPOCH TIMe", "today epoch is " + todayEpoch);
            Log.e("EPOCH TIMe", "CHECKIN  epoch is " + checkinTimeEpoch);
            Log.e("Times", "today time is " + dateTimeToday + " and checkin time is " + checkinTime);
            if (checkinTimeEpoch > todayEpoch) {
                Log.e("today checkin", "YES");
                employeeList.add(employee);
            }
        }

        return employeeList;
    }

    public List<Employee> getLastThreeMonthsEmployeeList() {
        List<Employee> employeeList = new ArrayList<Employee>();
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


        for (Employee employee : allEmployees) {
            Log.e("employee is ", employee.getId() + " " + employee.getFirstName());
            String checkinTime = employee.getCheckinTime();
            try {
                dateTimeToEpoch = dateFormat.parse(checkinTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Long checkinTimeEpoch = dateTimeToEpoch.getTime();
            Log.e("EPOCH TIMe", "today epoch is " + todayEpoch);
            Log.e("EPOCH TIMe", "CHECKIN  epoch is " + checkinTimeEpoch);
            Log.e("Times", "today time is " + dateTimeToday + " and checkin time is " + checkinTime);
            if (checkinTimeEpoch > todayEpoch) {
                Log.e("today checkin", "YES");
                employeeList.add(employee);
            }
        }

        return employeeList;
    }

    public List<Employee> getDateRangeEmployeeList(Bundle bundle) {
        List<Employee> employeeList = new ArrayList<Employee>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateTimeToEpoch = new Date();
        Long todayEpoch = dateTimeToEpoch.getTime();

        String fromDate = bundle.getString("fromDate");
        String toDate = bundle.getString("toDate");
        Long fromDateEpoch = getEpochValue(fromDate);
        Long toDateEpoch = getEpochValue(toDate);
        //long toDate = Long.getLong(bundle.getString("toDate"));

        for (Employee employee : allEmployees) {
            Log.e("employee is ", employee.getId() + " " + employee.getFirstName());
            String checkinTime = employee.getCheckinTime();
            try {
                dateTimeToEpoch = dateFormat.parse(checkinTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long checkinTimeEpoch = dateTimeToEpoch.getTime();
            Log.e("FROm time", " is " + fromDate);
            Log.e("To time ", " is " + toDate);
            Log.e("EPOCH TIMe", "today epoch is " + todayEpoch);
            Log.e("EPOCH TIMe", "CHECKIN  epoch is " + checkinTimeEpoch);


            if ((checkinTimeEpoch > fromDateEpoch) && (checkinTimeEpoch < toDateEpoch)) {
                Log.e("today checkin", "YES");
                employeeList.add(employee);
            }
        }

        return employeeList;
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

    public Long getDiffEpochTime(int diffYear, int diffMonth, int diffDate) {
        Date today = new Date();
        int hour = 0;
        int minutes = 0;
        int seconds = 0;

        int year = (1900 + today.getYear()) - diffYear;
        int month = today.getMonth() - diffMonth;
        int date = today.getDate() - diffDate;

        String dateTimeToday = year + "-" + (month + 1) + "-" + date + " " + hour + ":" + minutes + ":" + seconds;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateTimeToEpoch = new Date();
        try {
            dateTimeToEpoch = dateFormat.parse(dateTimeToday);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Long diffEpoch = dateTimeToEpoch.getTime();
        return diffEpoch;
    }
    public void finish() {
        super.finish();
    }

    class LoadAllEmployees extends AsyncTask<String, String, String> {
        JSONObject response;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DisplayEmployees.this);
            pDialog.setMessage("Loading Employees. Please wait...");
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

            response = jsonParser.makeHttpRequest(urlAllEmployees,"GET",null);
            Log.e("==message===", response.toString());
            try {
                success = response.getInt(TAG_SUCCESS);
                JSONArray employeeArray = response.getJSONArray(TAG_PRODUCTS);
                for (int i =0; i< employeeArray.length(); i++) {
                    Employee employee = new Employee();
                    JSONObject employeeObj = employeeArray.getJSONObject(i);
                    employee.setId(employeeObj.getLong(KEY_ID));
                    employee.setFirstName(employeeObj.getString(KEY_FIRST_NAME));
                    employee.setLastName(employeeObj.getString(KEY_LAST_NAME));
                    employee.setDepartment(employeeObj.getString(KEY_DEPARTMENT));
                    employee.setPhoneNumber(employeeObj.getString(KEY_PHONE));
                    employee.setBadgeNumber(employeeObj.getInt(KEY_BADGE));
                    employee.setCheckinTime(employeeObj.getString(KEY_CHECKIN_TIME));
                    employee.setCheckoutTime(employeeObj.getString(KEY_CHECKOUT_TIME));
                    allEmployees.add(employee);
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
                        JSONArray employeeArray = response.getJSONArray(TAG_PRODUCTS);
                        for (int i =0; i< employeeArray.length(); i++) {
                            Employee employee = new Employee();
                            JSONObject employeeObj = employeeArray.getJSONObject(i);
                            employee.setId(employeeObj.getLong(KEY_ID));
                            employee.setFirstName(employeeObj.getString(KEY_FIRST_NAME));
                            employee.setLastName(employeeObj.getString(KEY_LAST_NAME));
                            employee.setDepartment(employeeObj.getString(KEY_DEPARTMENT));
                            employee.setPhoneNumber(employeeObj.getString(KEY_PHONE));
                            employee.setBadgeNumber(employeeObj.getInt(KEY_BADGE));
                            employee.setCheckinTime(employeeObj.getString(KEY_CHECKIN_TIME));
                            employee.setCheckoutTime(employeeObj.getString(KEY_CHECKOUT_TIME));
                            allEmployees.add(employee);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

    }
}



//    implementation 'com.google.http-client:google-http-client-android:+'
//            implementation 'com.google.api-client:google-api-client-android:+'
//            implementation 'com.google.api-client:google-api-client-gson:+'
//            implementation 'org.apache.httpcomponents:httpcore:4.4.1'