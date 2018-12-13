package com.leaseweb.global.visitormanagement;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static com.leaseweb.global.visitormanagement.config.Config.DATABASE_PATH;
import static com.leaseweb.global.visitormanagement.config.Config.DB_HOST;
import static com.leaseweb.global.visitormanagement.config.Config.DISPLAY_EMPLOYEES;
import static com.leaseweb.global.visitormanagement.config.Config.DISPLAY_SUPPLIERS;
import static com.leaseweb.global.visitormanagement.config.Config.DISPLAY_VISITORS;
import static com.leaseweb.global.visitormanagement.config.Config.sharedPreferenceName;

public class AdminController extends AppCompatActivity implements View.OnClickListener {
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private int requestCode;
    private int grantResults[];
    final Context context = this;
    private Button button;

    private ProgressDialog pDialog;

    private static String urlAllEmployees;
    private static String urlAllVisitors;
    private static String urlAllSuppliers;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_EMPLOYEES = "employees";
    private static final String TAG_VISITORS = "visitors";

    private static final String KEY_FIRST_NAME = "firstName";
    private static final String KEY_LAST_NAME = "lastName";
    private static final String KEY_DEPARTMENT = "department";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_BADGE = "badgeNumber";
    private static final String KEY_CHECKIN_TIME = "checkinTime";
    private static final String KEY_CHECKOUT_TIME = "checkoutTime";
    private static final String KEY_RASON_FOR_VISIT = "reasonForVisit";
    private static final String KEY_CONTACT_PERSON = "contactPerson";
    private static final String KEY_LICENSE_PLATE = "licensePlate";
    private static final String KEY_COMPANY = "company";
    private int success;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_controller);

        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ){
            //if you dont have required permissions ask for it (only required for API 23+)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},requestCode);


            onRequestPermissionsResult(requestCode,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},grantResults);
        }

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String checkinTime = df1.format(c.getTime());
        Log.e("current time", checkinTime);
        //String currentTime = df1.format(c);
        //Log.e("current time 2", currentTime);


       /* SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        // formattedDate have current date/time
        Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();

        finish();*/

       // final DatabaseHandler db = new DatabaseHandler(this);
        //final SQLiteDatabase sqldb = db.getReadableDatabase();
        button = (Button) findViewById(R.id.export);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // set title
                alertDialogBuilder.setTitle("Email files");

                // set dialog message
                alertDialogBuilder.
                        setMessage("Do you want to email the CSV files?")
                        .setCancelable(false)
                        .setPositiveButton(Html.fromHtml("<font color='#00FF00'>YES</font>"), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, close
                                // current activity
                                exportDatabase();
                                sendEmail();
                            }
                        })
                        .setNegativeButton(Html.fromHtml("<font color='#FF0000'>NO</font>"), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                exportDatabase();
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });

    }

    public boolean isConnectedToDatabase() {
        String dbHost = null;
        sharedPreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(DB_HOST) && !sharedPreferences.getString(DB_HOST, "").equalsIgnoreCase("")) {
            dbHost = sharedPreferences.getString(DB_HOST, "");
        }
        if (dbHost == null || dbHost.equalsIgnoreCase("")) {
            Toast.makeText(getApplicationContext(),
                    "Database not configured", Toast.LENGTH_LONG).show();
            return false;
        }

        Runtime runtime = Runtime.getRuntime();
        try {
            Process  mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 " + dbHost);
            int mExitValue = mIpAddrProcess.waitFor();

            if(mExitValue!=0){
                Toast.makeText(getApplicationContext(),
                        "Unable to connect to database", Toast.LENGTH_LONG).show();
                return false;
            }
        } catch (UnknownHostException e) {
            Log.e("exception", e.getMessage());
        } catch (IOException e) {
            Log.e("exception ", e.toString());
            Toast.makeText(getApplicationContext(),
                    "Unable to ping to database", Toast.LENGTH_LONG).show();

            return false;
        } catch (InterruptedException ignore)
        {
            ignore.printStackTrace();
            System.out.println(" Exception:"+ignore);
        }

        return true;
    }

    public void sendEmail() {
        if (isConnectedToDatabase()) {
            Intent sendEmail = new Intent(this, SendEmailWithAttachment.class);
            sendEmail.putExtra("fileType", 0);
            startActivity(sendEmail);
        }
    }

    @Override // android recommended class to handle permissions
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d("permission", "granted");
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.uujm
                    Toast.makeText(AdminController.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();

                    //app cannot function without this permission for now so close it...
                    onDestroy();
                }
                return;
            }
        }
    }

    public void export(View view) {
        String filename = "myfile";
        String outputString = "Hello world!" ;
        String second = "word";
        try {
            FileOutputStream outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(outputString.getBytes());
            outputStream.write("\r".getBytes());
            outputStream.write(second.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.e("stack trace", String.valueOf(e.getMessage()) + "");
        }

        try {
            FileInputStream inputStream = openFileInput(filename);
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
                //total.append("\n");
            }
            r.close();
            inputStream.close();
            Log.d("File", "File contents: " + total);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void viewAllVisitors(View view) {
        if (isConnectedToDatabase()) {
            Intent viewVisitorIntent = new Intent(getApplicationContext(), DatePicker.class);
            viewVisitorIntent.putExtra("guestType", 0);
            startActivity(viewVisitorIntent);
        }
    }

    public void viewAllEmployees(View view) {
        if (isConnectedToDatabase()) {
            Intent viewEmployeeIntent = new Intent(getApplicationContext(), DatePicker.class);
            viewEmployeeIntent.putExtra("guestType", 1);
            startActivity(viewEmployeeIntent);
        }
    }

    public void viewAllSuppliers(View view) {
        if (isConnectedToDatabase()) {
            Intent viewSupplierIntent = new Intent(getApplicationContext(), DatePicker.class);
            viewSupplierIntent.putExtra("guestType", 2);
            Log.e("supplier", "calling datepicker class");
            startActivity(viewSupplierIntent);
        }
    }

    public void viewAllUsers(View view) {
        Intent viewUsers = new Intent(this, UsersListActivity.class);
        startActivity(viewUsers);
    }

    public void addAccount(View view) {
        Intent intentRegister = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intentRegister);
    }

    public void deleteUser(View view) {
        Intent deleteUser = new Intent(getApplicationContext(), DeleteUser.class);
        startActivity(deleteUser);
    }

    public void deleteEmployee(View view) {
        if (isConnectedToDatabase()) {
            Intent deleteEmployee = new Intent(getApplicationContext(), DeleteEmployee.class);
            startActivity(deleteEmployee);
        }
    }

    public void deleteVisitor(View view) {
        if (isConnectedToDatabase()) {
            Intent deleteVisitor = new Intent(getApplicationContext(), DeleteVisitor.class);
            startActivity(deleteVisitor);
        }
    }

    public void deleteSupplier(View view) {
        if (isConnectedToDatabase()) {
            Intent deleteSupplier = new Intent(getApplicationContext(), DeleteSupplier.class);
            startActivity(deleteSupplier);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Logout");
        builder.setMessage("Do you want to Log out? ");
        builder.setPositiveButton(Html.fromHtml("<font color='#FF0000'>YES</font>"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //saveResult();
                AdminController.super.onBackPressed();
            }
        });
        builder.setNegativeButton(Html.fromHtml("<font color='#00FF00'>NO</font>"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //GuestController.super.onBackPressed();
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void datePicker(View view) {
        if (isConnectedToDatabase()) {
            Intent date = new Intent(this, DatePicker.class);
            startActivity(date);
        }
    }

    public void configure(View view) {
        Intent configure = new Intent(this, ApplicationConfig.class);
        startActivity(configure);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewVisitors:
                Intent viewVisitorIntent = new Intent(getApplicationContext(), DatePicker.class);
                viewVisitorIntent.putExtra("guestType", 0);
                startActivity(viewVisitorIntent);
                break;
            case R.id.viewEmployees:
                // Navigate to RegisterActivity
                Intent viewEmployeeIntent = new Intent(getApplicationContext(), DatePicker.class);
                viewEmployeeIntent.putExtra("guestType", 1);
                startActivity(viewEmployeeIntent);
                break;
            case R.id.viewUsers:
                viewAllUsers(v);
                break;
        }
    }

    public boolean exportDatabase() {
        String dbHost = null;

        if (!isConnectedToDatabase()) {
            return false;
        }
        sharedPreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(DB_HOST) && ! sharedPreferences.getString(DB_HOST, "").equalsIgnoreCase("")) {
            dbHost = sharedPreferences.getString(DB_HOST, "");
            urlAllEmployees = "http://" + dbHost + "/" + DATABASE_PATH + "/" + DISPLAY_EMPLOYEES;
            urlAllVisitors = "http://" + dbHost + "/" + DATABASE_PATH + "/" + DISPLAY_VISITORS;
            urlAllSuppliers = "http://" + dbHost + "/" + DATABASE_PATH + "/" + DISPLAY_SUPPLIERS;
        }
        /**First of all we check if the external storage of the device is available for writing.
         * Remember that the external storage is not necessarily the sd card. Very often it is
         * the device storage.
         */
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Toast.makeText(this, "Unable to export data. No external storage found",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        else {
            Object result = "";
            try {
                result = new LoadAllEmployeesVisitors().execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            String returnStatus = (String)result;

            if (returnStatus.equalsIgnoreCase("true")) {
                Toast.makeText(AdminController.this, "Data is succesfully stored in Downloads folder", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AdminController.this, "Unable to export the data", Toast.LENGTH_SHORT).show();
            }
            //We use the Download directory for saving our .csv file.
            /*File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!exportDir.exists())
            {
                exportDir.mkdirs();
            }

            Log.d("Directory is ", exportDir + "");
            File visitorFile;
            File employeeFile;
            PrintWriter visitorPrintWriter = null;
            PrintWriter employeePrintWriter = null;
            try
            {
                visitorFile = new File(exportDir, "Visitors.csv");
                employeeFile = new File(exportDir, "Employees.csv");
                Log.d("File is ", visitorFile + "");
                visitorFile.createNewFile();
                employeeFile.createNewFile();
                visitorPrintWriter = new PrintWriter(new FileWriter(visitorFile));
                employeePrintWriter = new PrintWriter(new FileWriter(employeeFile));

                *//**This is our database connector class that reads the data from the database.
                 * The code of this class is omitted for brevity.
                 *//*
                final DatabaseHandler db = new DatabaseHandler(this);
                final SQLiteDatabase sqldb = db.getReadableDatabase();
                Cursor curCSV = null;

                *//**Let's read the first table of the database.
                 * getFirstTable() is a method in our DBCOurDatabaseConnector class which retrieves a Cursor
                 * containing all records of the table (all fields).
                 * The code of this class is omitted for brevity.
                 *//*
                curCSV = sqldb.rawQuery("select * from visitor_test", null);
                //Write the name of the table and the name of the columns (comma separated values) in the .csv file.

                String firstName;
                String lastName;
                String phone;
                String badge;
                String checkinTime;
                String checkoutTime;

                visitorPrintWriter.println("First Name,Last Name,Phone,Reason for visit,Appointment with,License plate,Badge number,checkin time,checkout time ");

                if (curCSV.moveToFirst())
                {
                    do {
                        firstName = curCSV.getString(curCSV.getColumnIndex("first_name"));
                        lastName = curCSV.getString(curCSV.getColumnIndex("last_name"));
                        phone = curCSV.getString(curCSV.getColumnIndex("phone"));
                        String reason = curCSV.getString(curCSV.getColumnIndex("reason_for_visit"));
                        String contact = curCSV.getString(curCSV.getColumnIndex("contact_person"));
                        String license = curCSV.getString(curCSV.getColumnIndex("license_plate"));
                        badge = curCSV.getString(curCSV.getColumnIndex("badge_number"));
                        checkinTime = curCSV.getString(curCSV.getColumnIndex("checkin_time"));
                        checkoutTime = curCSV.getString(curCSV.getColumnIndex("checkout_time"));

                        *//**Create the line to write in the .csv file.
                         * We need a String where values are comma separated.
                         * The field date (Long) is formatted in a readable text. The amount field
                         * is converted into String.
                         *//*
                        String record = firstName + "," + lastName + "," + phone + "," + reason + "," +
                                contact + "," + license + "," + badge + "," + checkinTime + "," + checkoutTime;
                        visitorPrintWriter.println(record); //write the record in the .csv file
                    } while (curCSV.moveToNext());
                }

                curCSV = sqldb.rawQuery("select * from employee_test", null);
                employeePrintWriter.println("First Name,Last Name,Phone,Department,Badge,Checkin time,Checkout time");
                if (curCSV.moveToFirst())
                {
                    do {
                        firstName = curCSV.getString(curCSV.getColumnIndex("first_name"));
                        lastName = curCSV.getString(curCSV.getColumnIndex("last_name"));
                        phone = curCSV.getString(curCSV.getColumnIndex("phone"));
                        String department = curCSV.getString(curCSV.getColumnIndex("department"));
                        badge = curCSV.getString(curCSV.getColumnIndex("badge_number"));
                        checkinTime = curCSV.getString(curCSV.getColumnIndex("checkin_time"));
                        checkoutTime = curCSV.getString(curCSV.getColumnIndex("checkout_time"));

                        *//**Create the line to write in the .csv file.
                         * We need a String where values are comma separated.
                         * The field date (Long) is formatted in a readable text. The amount field
                         * is converted into String.
                         *//*
                        String record = firstName + "," + lastName + "," + phone + "," + department + "," +
                                badge + "," + checkinTime + "," + checkoutTime;
                        employeePrintWriter.println(record); //write the record in the .csv file
                    }while (curCSV.moveToNext());
                }

                curCSV.close();
                //dbcOurDatabaseConnector.close();
            } catch(Exception exc) {
                Log.d("Exception1", exc.getMessage());
                Toast.makeText(this, "Unable to export data.",
                        Toast.LENGTH_LONG).show();
                exc.printStackTrace();
                //if there are any exceptions, return false
                return false;
            }
            finally {
                if(visitorPrintWriter != null) visitorPrintWriter.close();
                if(employeePrintWriter != null) employeePrintWriter.close();
            }
*/


            return true;
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public void pieChart(View view) {
        if (isConnectedToDatabase()) {
            Intent pieChart = new Intent(this, CustomBarChart.class);
            startActivity(pieChart);
        }
    }
    public void finish(View view) {
        super.finish();
    }

    class LoadAllEmployeesVisitors extends AsyncTask<String, String, String> {
        JSONObject response;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AdminController.this);
            pDialog.setMessage("Fetching Employees and visitors. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!exportDir.exists())
            {
                exportDir.mkdirs();
            }

            Log.d("Directory is ", exportDir + "");
            File visitorFile;
            File employeeFile;
            File supplierFile;
            PrintWriter visitorPrintWriter = null;
            PrintWriter employeePrintWriter = null;
            PrintWriter supplierPrintWriter = null;

            visitorFile = new File(exportDir, "Visitors.csv");
            employeeFile = new File(exportDir, "Employees.csv");
            supplierFile = new File(exportDir, "Suppliers.csv");
            Log.d("File is ", visitorFile + "");
            try {
                visitorFile.createNewFile();
                employeeFile.createNewFile();
                supplierFile.createNewFile();
                visitorPrintWriter = new PrintWriter(new FileWriter(visitorFile));
                employeePrintWriter = new PrintWriter(new FileWriter(employeeFile));
                supplierPrintWriter = new PrintWriter(new FileWriter(supplierFile));
            } catch (Exception e) {
                Log.e("ERROR: ", e.getMessage());
                return null;
            }

            visitorPrintWriter.println("First Name,Last Name,Phone,Reason for visit,Appointment with,License plate,Badge number,checkin time,checkout time ");
            employeePrintWriter.println("First Name,Last Name,Phone,Department,Badge,Checkin time,Checkout time");
            supplierPrintWriter.println("First name,Last Name,Phone,Company,Contact Person,License Plate,Badge Number,Checkin time,Checkout time");

            String firstName = "";
            String lastName = "";
            String phone = "";
            int badge;
            String checkinTime = "";
            String checkoutTime = "";
            String reasonForVisit = "";
            String contactPerson = "";
            String licensePlate = "";
            String department = "";
            String company = "";

            // getting JSON string from URL
            HttpJsonParser jsonParser = new HttpJsonParser();

            response = jsonParser.makeHttpRequest(urlAllEmployees,"GET",null);
            //Log.e("MESSAGE: ", response.toString());
            try {
                success = response.getInt(TAG_SUCCESS);
                if (success == 1) {
                    JSONArray employeeArray = response.getJSONArray(TAG_EMPLOYEES);
                    for (int i = 0; i < employeeArray.length(); i++) {
                        JSONObject employeeObj = employeeArray.getJSONObject(i);
                        firstName = (employeeObj.getString(KEY_FIRST_NAME));
                        lastName = (employeeObj.getString(KEY_LAST_NAME));
                        department = (employeeObj.getString(KEY_DEPARTMENT));
                        phone = (employeeObj.getString(KEY_PHONE));
                        badge = (employeeObj.getInt(KEY_BADGE));
                        checkinTime = (employeeObj.getString(KEY_CHECKIN_TIME));
                        checkoutTime = (employeeObj.getString(KEY_CHECKOUT_TIME));

                        String record = firstName + "," + lastName + "," + phone + "," +
                                department + "," + badge + "," + checkinTime + "," + checkoutTime;
                        employeePrintWriter.println(record);
                    }
                }
                if(employeePrintWriter != null) employeePrintWriter.close();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            response = jsonParser.makeHttpRequest(urlAllVisitors,"GET",null);
            //Log.e("MESSAGE: ", response.toString());
            try {
                success = response.getInt(TAG_SUCCESS);
                if (success == 1) {
                    JSONArray employeeArray = response.getJSONArray(TAG_VISITORS);
                    for (int i = 0; i < employeeArray.length(); i++) {
                        JSONObject employeeObj = employeeArray.getJSONObject(i);
                        firstName = (employeeObj.getString(KEY_FIRST_NAME));
                        lastName = (employeeObj.getString(KEY_LAST_NAME));
                        reasonForVisit = employeeObj.getString(KEY_RASON_FOR_VISIT);
                        contactPerson = employeeObj.getString(KEY_CONTACT_PERSON);
                        licensePlate = employeeObj.getString(KEY_LICENSE_PLATE);
                        phone = (employeeObj.getString(KEY_PHONE));
                        badge = (employeeObj.getInt(KEY_BADGE));
                        checkinTime = (employeeObj.getString(KEY_CHECKIN_TIME));
                        checkoutTime = (employeeObj.getString(KEY_CHECKOUT_TIME));

                        String record = firstName + "," + lastName + "," + phone + "," + reasonForVisit + "," +
                                contactPerson + "," + licensePlate + "," + badge + "," + checkinTime + "," + checkoutTime;
                        visitorPrintWriter.println(record);
                    }
                }
                if(visitorPrintWriter != null) visitorPrintWriter.close();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            response = jsonParser.makeHttpRequest(urlAllSuppliers,"GET",null);
            Log.e("MESSAGE: ", response.toString());
            try {
                success = response.getInt(TAG_SUCCESS);
                if (success == 1) {
                    JSONArray employeeArray = response.getJSONArray(TAG_VISITORS);
                    for (int i = 0; i < employeeArray.length(); i++) {
                        JSONObject employeeObj = employeeArray.getJSONObject(i);
                        firstName = (employeeObj.getString(KEY_FIRST_NAME));
                        lastName = (employeeObj.getString(KEY_LAST_NAME));
                        company = employeeObj.getString(KEY_COMPANY);
                        contactPerson = employeeObj.getString(KEY_CONTACT_PERSON);
                        licensePlate = employeeObj.getString(KEY_LICENSE_PLATE);
                        phone = (employeeObj.getString(KEY_PHONE));
                        badge = (employeeObj.getInt(KEY_BADGE));
                        checkinTime = (employeeObj.getString(KEY_CHECKIN_TIME));
                        checkoutTime = (employeeObj.getString(KEY_CHECKOUT_TIME));

                        String record = firstName + "," + lastName + "," + phone + "," + company + "," +
                                contactPerson + "," + licensePlate + "," + badge + "," + checkinTime + "," + checkoutTime;
                        supplierPrintWriter.println(record);
                    }
                }
                if(supplierPrintWriter != null) supplierPrintWriter.close();
                return "true";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return  "true";
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
