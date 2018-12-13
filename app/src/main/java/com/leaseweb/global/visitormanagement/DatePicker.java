package com.leaseweb.global.visitormanagement;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePicker extends Activity {

    DatePickerFragment customFrom;
    DatePickerFragment customTo;
    Button btnEventDateTimeFrom;
    Button btnEventDateTimeTo;
    String epochValueFrom = null;
    String epochValueTo = null;
    int guestType;
    private static final int VISITOR = 0;
    private static final int EMPLOYEE = 1;
    private static final int SUPPLIER = 2;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String dbHost = null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_picker);
        btnEventDateTimeFrom = ((Button) findViewById(R.id.pickDateFrom));
        btnEventDateTimeTo = (Button) findViewById(R.id.pickDateTo);

        Bundle result = getIntent().getExtras();
        guestType = result.getInt("guestType");

        Log.e("guest type is ", "" + guestType);
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
                        //edtEventDateTime.setText("");
                        String fromDateTime = year + "-" + (monthNumber + 1) + "-" + calendarSelected.get(Calendar.DAY_OF_MONTH) + " " + hour24 + ":" + min + ":" + sec;
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date fromDateTimeEpoch = new Date();
                        try {
                            fromDateTimeEpoch = dateFormat.parse(fromDateTime);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        epochValueFrom = fromDateTime;
                        //epochValueFrom = fromDateTimeEpoch.getTime();
                        //showDate.setText(String.valueOf(oldDate.getTime()));
                        //showDate.setText(year
                        //        + "-" + (monthNumber + 1) + "-" + calendarSelected.get(Calendar.DAY_OF_MONTH)
                        //        + " " + hour24 + ":" + min
                        //        + ":" + sec);
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
                        //edtEventDateTime.setText("");
                        String dateTimeTo = year + "-" + (monthNumber + 1) + "-" + calendarSelected.get(Calendar.DAY_OF_MONTH) + " " + hour24 + ":" + min + ":" + sec;
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date dateTimeToEpoch = new Date();
                        try {
                            dateTimeToEpoch = dateFormat.parse(dateTimeTo);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        epochValueTo = dateTimeTo;
                        //epochValueTo = dateTimeToEpoch.getTime();

                        Log.e("current time is ", String.valueOf(dateTimeTo));
                        Log.e("getTime is ", String.valueOf(dateTimeToEpoch.getTime()));
                        //showDate.setText(String.valueOf(oldDate.getTime()));
                        //showDate.setText(year
                        //        + "-" + (monthNumber + 1) + "-" + calendarSelected.get(Calendar.DAY_OF_MONTH)
                        //        + " " + hour24 + ":" + min
                        //        + ":" + sec);
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

    public void done(View view) {
        //Long difference = (epochValueTo - epochValueFrom) / 1000;
        Long difference;
        Date today = new Date();
        int hour = today.getHours();
        int minutes = today.getMinutes();
        int seconds = today.getSeconds();

        int hourTo = 0;
        int minuteTo = 3;
        int secTo = 20;

        int fromHour = 0;
        int fromMin = 0;
        int fromSec=0;
        int year = 1900 + today.getYear();
        int month = today.getMonth() + 1;
        int date = today.getDate();

        String dateTimeTo = year + "-" + (month + 1) + "-" + date + " " + hourTo + ":" + minuteTo + ":" + secTo;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateTimeToEpoch = new Date();
        try {
            dateTimeToEpoch = dateFormat.parse(dateTimeTo);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dateTimeFrom =year + "-" + (month + 1) + "-" + date + " " + fromHour + ":" + fromMin + ":" + fromSec;
        Date dateTimeFromEpoch = new Date();
        try {
            dateTimeFromEpoch = dateFormat.parse(dateTimeFrom);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String time = year + "=" + month + "-" + date + " " + hour + ":" + minutes + ":" + seconds;

        difference = (dateTimeToEpoch.getTime() - dateTimeFromEpoch.getTime()) / 1000;
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

        switch (guestType) {
            case VISITOR:
                Intent visitor = new Intent(this, DisplayVisitors.class);
                visitor.putExtra("dateRange", "range");
                visitor.putExtra("fromDate", String.valueOf(epochValueFrom));
                visitor.putExtra("toDate", String.valueOf(epochValueTo));
                startActivity(visitor);
                break;
            case EMPLOYEE:
                Intent employee = new Intent(this, DisplayEmployees.class);
                employee.putExtra("dateRange", "range");
                employee.putExtra("fromDate", String.valueOf(epochValueFrom));
                employee.putExtra("toDate", String.valueOf(epochValueTo));
                startActivity(employee);
                break;
            case SUPPLIER:
                Intent supplier = new Intent(this, DisplaySupplier.class);
                supplier.putExtra("dateRange", "range");
                supplier.putExtra("fromDate", String.valueOf(epochValueFrom));
                supplier.putExtra("toDate", String.valueOf(epochValueTo));
                startActivity(supplier);
                break;
        }
    }

    public void displayTodayResult(View view) {
        switch (guestType) {
            case VISITOR:
                Intent visitor = new Intent(getApplicationContext(), DisplayVisitors.class);
                visitor.putExtra("dateRange", "today");
                startActivity(visitor);
                break;
            case EMPLOYEE:
                Intent employee = new Intent(this, DisplayEmployees.class);
                employee.putExtra("dateRange", "today");
                startActivity(employee);
                break;
            case SUPPLIER:
                Intent supplier = new Intent(this, DisplaySupplier.class);
                Log.e("display supplier", "intent");
                supplier.putExtra("dateRange", "today");
                startActivity(supplier);
                break;
        }

    }

    public void displayThisWeekResult(View view) {
        switch (guestType) {
            case VISITOR:
                Intent visitor = new Intent(getApplicationContext(), DisplayVisitors.class);
                visitor.putExtra("dateRange", "thisWeek");
                startActivity(visitor);
                break;
            case EMPLOYEE:
                Intent employee = new Intent(this, DisplayEmployees.class);
                employee.putExtra("dateRange", "thisWeek");
                startActivity(employee);
                break;
            case SUPPLIER:
                Intent supplier = new Intent(this, DisplaySupplier.class);
                supplier.putExtra("dateRange", "thisWeek");
                startActivity(supplier);
                break;
        }

    }

    public void displayLastMonthResult(View view) {
        switch (guestType) {
            case VISITOR:
                Intent visitor = new Intent(getApplicationContext(), DisplayVisitors.class);
                visitor.putExtra("dateRange", "lastMonth");
                startActivity(visitor);
                break;
            case EMPLOYEE:
                Intent employee = new Intent(getApplicationContext(), DisplayEmployees.class);
                employee.putExtra("dateRange", "lastMonth");
                startActivity(employee);
                break;
            case SUPPLIER:
                Intent supplier = new Intent(this, DisplaySupplier.class);
                supplier.putExtra("dateRange", "lastMonth");
                startActivity(supplier);
                break;
        }

    }

    public void displayLsatThreeMonthsResult(View view) {
        switch (guestType) {
            case VISITOR:
                Intent visitor = new Intent(getApplicationContext(), DisplayVisitors.class);
                visitor.putExtra("dateRange", "lastThreeMonths");
                startActivity(visitor);
                break;
            case EMPLOYEE:
                Intent employee = new Intent(getApplicationContext(), DisplayEmployees.class);
                employee.putExtra("dateRange", "lastThreeMonths");
                startActivity(employee);
                break;
            case SUPPLIER:
                Intent supplier = new Intent(getApplicationContext(), DisplaySupplier.class);
                supplier.putExtra("dateRange", "lastThreeMonths");
                startActivity(supplier);
                break;
        }

    }

    public void displayAll(View view) {
        switch (guestType) {
            case VISITOR:
                Intent visitor = new Intent(getApplicationContext(), DisplayVisitors.class);
                visitor.putExtra("dateRange", "all");
                startActivity(visitor);
                break;
            case EMPLOYEE:
                Intent employee = new Intent(getApplicationContext(), DisplayEmployees.class);
                employee.putExtra("dateRange", "all");
                startActivity(employee);
                break;
            case SUPPLIER:
                Intent supplier = new Intent(this, DisplaySupplier.class);
                supplier.putExtra("dateRange", "all");
                startActivity(supplier);
                break;
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

    public void finish(View view) {
        super.finish();
    }
}