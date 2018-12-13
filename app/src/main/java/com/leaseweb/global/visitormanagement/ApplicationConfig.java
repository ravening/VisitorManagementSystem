package com.leaseweb.global.visitormanagement;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.leaseweb.global.visitormanagement.config.Config.DB_HOST;
import static com.leaseweb.global.visitormanagement.config.Config.LOCATION;
import static com.leaseweb.global.visitormanagement.config.Config.sharedPreferenceName;

public class ApplicationConfig extends Activity implements AdapterView.OnItemSelectedListener {
    SharedPreferences sharedpreferences;
    TextView name;

    private Spinner spinner;
    private EditText dbHost;

    public List<String> locationList = new ArrayList<>();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.application_config);

        // locationMap and locationList should be in sync and in same order
        HashMap<String, Integer> locationMap = new HashMap<>();
        locationMap.put("AMS01", 0);
        locationMap.put("FRA01", 1);
        locationMap.put("PHX01", 2);
        locationMap.put("WDC01", 3);
        locationMap.put("LON01", 4);
        locationMap.put("SGP01", 5);
        sharedpreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(DB_HOST)) {
            Log.e("db host is ", sharedpreferences.getString(DB_HOST, ""));
            dbHost = (EditText) findViewById(R.id.databaseHost);
            dbHost.setText(sharedpreferences.getString(DB_HOST,""));
        }

        addItemsOnSpinner();
        if (sharedpreferences.contains(LOCATION)) {
            String location = sharedpreferences.getString(LOCATION, "");
            spinner = (Spinner) findViewById(R.id.spinner);
            if (location != null && !location.equalsIgnoreCase("")) {
                spinner.setSelection(locationMap.get(location));
            } else {
                spinner.setSelection(0);
            }
        }
        addListenerOnButton();

    }

    public void addItemsOnSpinner() {
        spinner = (Spinner) findViewById(R.id.spinner);
        locationList.add("AMS01");
        locationList.add("FRA01");
        locationList.add("PHX01");
        locationList.add("WDC01");
        locationList.add("LON01");
        locationList.add("SGP01");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, locationList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void saveConfig() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(DB_HOST, dbHost.getText().toString());
        editor.putString(LOCATION, String.valueOf(spinner.getSelectedItem()));
        editor.apply();
        finish();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
        Toast.makeText(parent.getContext(),
                "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public void addListenerOnButton() {

        spinner = (Spinner) findViewById(R.id.spinner);
        dbHost = (EditText) findViewById(R.id.databaseHost);
        Button btnSubmit = (Button) findViewById(R.id.save);
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(ApplicationConfig.this,
                        "Data saved" ,
                        Toast.LENGTH_SHORT).show();
                saveConfig();
            }

        });
    }

    public boolean isConnectedToDatabase() {
        String dbHost = null;
        sharedpreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(DB_HOST) && ! sharedpreferences.getString(DB_HOST, "").equalsIgnoreCase("")) {
            dbHost = sharedpreferences.getString(DB_HOST, "");
        }

        if (dbHost == null) {
            return false;
        }
        return true;
    }
}
