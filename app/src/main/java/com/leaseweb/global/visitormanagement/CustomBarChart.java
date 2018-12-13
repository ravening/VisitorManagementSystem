package com.leaseweb.global.visitormanagement;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.leaseweb.global.visitormanagement.model.Employee;
import com.leaseweb.global.visitormanagement.model.Supplier;
import com.leaseweb.global.visitormanagement.model.Visitor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.leaseweb.global.visitormanagement.config.Config.DATABASE_PATH;
import static com.leaseweb.global.visitormanagement.config.Config.DB_HOST;
import static com.leaseweb.global.visitormanagement.config.Config.DISPLAY_EMPLOYEES;
import static com.leaseweb.global.visitormanagement.config.Config.DISPLAY_SUPPLIERS;
import static com.leaseweb.global.visitormanagement.config.Config.DISPLAY_VISITORS;
import static com.leaseweb.global.visitormanagement.config.Config.sharedPreferenceName;

public class CustomBarChart extends Activity {
    BarChart barChart;

    private ProgressDialog pDialog;

    private static String urlAllEmployees;
    private static String urlAllVisitors;
    private static String urlAllSuppliers;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_EMPLOYEES = "employees";
    private static final String TAG_VISITORS = "visitors";

    private int success;
    private static final String KEY_CHECKIN_TIME = "checkinTime";

    List<Employee> employeeList = new ArrayList<>();
    List<Visitor> visitorList = new ArrayList<>();
    List<Supplier> supplierList = new ArrayList<>();

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String dbHost = null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_barchart);
        barChart = (BarChart) findViewById(R.id.chart1);

        sharedPreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(DB_HOST) && ! sharedPreferences.getString(DB_HOST, "").equalsIgnoreCase("")) {
            dbHost = sharedPreferences.getString(DB_HOST, "");
            urlAllEmployees = "http://" + dbHost + "/" + DATABASE_PATH + "/" + DISPLAY_EMPLOYEES;
            urlAllVisitors = "http://" + dbHost + "/" + DATABASE_PATH + "/" + DISPLAY_VISITORS;
            urlAllSuppliers = "http://" + dbHost + "/" + DATABASE_PATH + "/" + DISPLAY_SUPPLIERS;
        }

        HashMap<Integer, String> months = new HashMap<>();
        months.put(0, "January");
        months.put(1, "February");
        months.put(2, "March");
        months.put(3, "April");
        months.put(4, "May");
        months.put(5, "June");
        months.put(6, "July");
        months.put(7, "August");
        months.put(8, "September");
        months.put(9, "October");
        months.put(10, "November");
        months.put(11, "December");
        //DatabaseHandler db = new DatabaseHandler(this);

        Object result = "";
        try {
            result = new LoadAllEmployeesVisitors().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        HashMap<Integer, Integer> employees = new HashMap<>();
        HashMap<Integer, Integer> visitors = new HashMap<>();
        HashMap<Integer, Integer> suppliers = new HashMap<>();

        for (int i =0; i < 3; i++) {
            if (employees.get(i) == null) {
                employees.put(i, 0);
                visitors.put(i, 0);
                suppliers.put(i, 0);
            }
        }
        Date currentTime = Calendar.getInstance().getTime();
        int currentMonth = currentTime.getMonth();
        Log.e("current month", " is " + currentMonth);
        for (Employee employee : employeeList) {
            String checkinTime = employee.getCheckinTime();
            int month = getMonth(checkinTime);
            Log.e("employee checkin month", " is " + month);
            if (employees.get(currentMonth - month) == null) {
                employees.put(currentMonth-month, 1);
            } else {
                employees.put(currentMonth - month, employees.get(currentMonth - month) + 1);
            }
        }

        for (Visitor visitor : visitorList) {
            String checkinTime = visitor.getCheckinTime();
            int month = getMonth(checkinTime);
            Log.e("visitor checkin month", " is " + month);
            if (visitors.get(currentMonth - month) == null) {
                visitors.put(currentMonth-month, 1);
            } else {
                visitors.put(currentMonth - month, visitors.get(currentMonth - month) + 1);
            }
        }

        for (Supplier supplier : supplierList) {
            String checkinTime = supplier.getCheckinTime();
            int month = getMonth(checkinTime);
            Log.e("supplier checkin month", " is " + month);
            if (suppliers.get(currentMonth - month) == null) {
                suppliers.put(currentMonth-month, 1);
            } else {
                suppliers.put(currentMonth - month, suppliers.get(currentMonth - month) + 1);
            }
        }

        ArrayList<BarEntry> employeeEntry = new ArrayList<>();
        ArrayList<BarEntry> visitorEntry = new ArrayList<>();
        ArrayList<BarEntry> supplierEntry = new ArrayList<>();

        employeeEntry.add(new BarEntry(1, employees.get(2)));
        employeeEntry.add(new BarEntry(2, employees.get(1)));
        employeeEntry.add(new BarEntry(3, employees.get(0)));

        visitorEntry.add(new BarEntry(1, visitors.get(2)));
        visitorEntry.add(new BarEntry(2, visitors.get(1)));
        visitorEntry.add(new BarEntry(3, visitors.get(0)));

        supplierEntry.add(new BarEntry(1, suppliers.get(2)));
        supplierEntry.add(new BarEntry(2, suppliers.get(1)));
        supplierEntry.add(new BarEntry(3, suppliers.get(0)));

        BarDataSet barDataSet = new BarDataSet(employeeEntry,"Employees");
        barDataSet.setColor(Color.parseColor("#F44336"));
        BarDataSet barDataSet1 = new BarDataSet(visitorEntry,"Visitors");
        barDataSet1.setColors(Color.parseColor("#9C27B0"));
        BarDataSet barDataSet3 = new BarDataSet(supplierEntry,"Suppliers");
        barDataSet3.setColors(Color.parseColor("#42f46e"));
        /*BarDataSet barDataSet2 = new BarDataSet(barEntries2,"DATA SET 3");
        barDataSet1.setColors(Color.parseColor("#e241f4"));
        BarDataSet barDataSet3 = new BarDataSet(barEntries3,"DATA SET 4");
        barDataSet1.setColors(Color.parseColor("#42f46e"));*/

        String[] displayMonths = new String[] {months.get(currentMonth-2), months.get(currentMonth - 1), months.get(currentMonth)};
        BarData data = new BarData(barDataSet,barDataSet1, barDataSet3);
        barChart.setData(data);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(displayMonths));
        barChart.getAxisLeft().setAxisMinimum(0);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularityEnabled(true);

        float barSpace = 0.02f;
        float groupSpace = 0.5f;
        int groupCount = 3;

        data.setBarWidth(0.15f);
        barChart.getXAxis().setAxisMinimum(0);
        //barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        barChart.getXAxis().setAxisMaximum(3.0f);
        barChart.groupBars(0, groupSpace, barSpace); // perform the "explicit" grouping
    }

    /*@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.custom_barchart);
            barChart = (BarChart) findViewById(R.id.chart1);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            setContentView(R.layout.custom_pie_chart);
            barChart = (BarChart) findViewById(R.id.chart1);
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }*/

    private int getMonth(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateTimeToEpoch = new Date();
        try {
            dateTimeToEpoch = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTimeToEpoch.getMonth();
    }

    class LoadAllEmployeesVisitors extends AsyncTask<String, String, String> {
        JSONObject response;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CustomBarChart.this);
            pDialog.setMessage("Fetching Employees and visitors. Please wait...");
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
            Log.e("MESSAGE: ", response.toString());
            try {
                success = response.getInt(TAG_SUCCESS);
                if (success == 1) {
                    JSONArray employeeArray = response.getJSONArray(TAG_EMPLOYEES);
                    for (int i = 0; i < employeeArray.length(); i++) {
                        JSONObject employeeObj = employeeArray.getJSONObject(i);
                        Employee employee = new Employee();
                        employee.setCheckinTime(employeeObj.getString(KEY_CHECKIN_TIME));
                        CustomBarChart.this.employeeList.add(employee);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            response = jsonParser.makeHttpRequest(urlAllVisitors,"GET",null);
            Log.e("MESSAGE: ", response.toString());
            try {
                success = response.getInt(TAG_SUCCESS);
                if (success == 1) {
                    JSONArray employeeArray = response.getJSONArray(TAG_VISITORS);
                    for (int i = 0; i < employeeArray.length(); i++) {
                        JSONObject employeeObj = employeeArray.getJSONObject(i);
                        Visitor visitor = new Visitor();
                        visitor.setCheckinTime(employeeObj.getString(KEY_CHECKIN_TIME));
                        CustomBarChart.this.visitorList.add(visitor);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            response = jsonParser.makeHttpRequest(urlAllSuppliers,"GET",null);
            Log.e("script is ", urlAllSuppliers);
            Log.e("MESSAGE: ", response.toString());
            try {
                success = response.getInt(TAG_SUCCESS);
                if (success == 1) {
                    JSONArray employeeArray = response.getJSONArray(TAG_VISITORS);
                    for (int i = 0; i < employeeArray.length(); i++) {
                        JSONObject employeeObj = employeeArray.getJSONObject(i);
                        Supplier supplier = new Supplier();
                        supplier.setCheckinTime(employeeObj.getString(KEY_CHECKIN_TIME));
                        CustomBarChart.this.supplierList.add(supplier);
                    }
                }
                return "true";
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
                    /*try {
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
                    }*/
                }
            });

        }

    }
}
