package com.leaseweb.global.visitormanagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.net.UnknownHostException;

import static com.leaseweb.global.visitormanagement.config.Config.DB_HOST;
import static com.leaseweb.global.visitormanagement.config.Config.sharedPreferenceName;

public class GuestController extends Activity {
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_controller);
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

    public void visitorController(View view) {
        if (isConnectedToDatabase()) {
            Intent visitorAction = new Intent(this, VisitorController.class);
            startActivity(visitorAction);
        }
    }

    public void employeeController(View view) {
        if (isConnectedToDatabase()) {
            Intent employeeAction = new Intent(this, EmployeeController.class);
            startActivity(employeeAction);
        }
    }

    public void supplierController(View view) {
        if (isConnectedToDatabase()) {
            Intent employeeAction = new Intent(this, SupplierController.class);
            startActivity(employeeAction);
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
                GuestController.super.onBackPressed();
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
}
