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
import android.widget.Button;

public class VisitorController extends Activity {
    final Context context = this;
    private Button button;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visitor_controller);

        final String gdprMessage = "The General Data Protection Regulation (GDPR) is a regulation in EU law on data protection and privacy for all individuals within the European Union and the European Economic Area. Visitors to Leaseweb HQ must answer the questions below, so it is known who is visiting the premises and for what reasons. This way it is possible to keep the building secure. And to guarantee the safety of the persons inside. We will not share your personal data for marketing purposes. Today, the Facility Desk and other visitors may have access to personal information. This visitor form will be stored at the end of today and destroyed after three (3) months. Please note that you are under no obligation to provide the information requested. However, if you decline, Leaseweb had the right to refuse you access to the premises.";
        button = (Button) findViewById(R.id.checkin);
        // add button listener
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // set title
                alertDialogBuilder.setTitle("Read GDPR info");

                // set dialog message
                alertDialogBuilder.
                        setMessage(gdprMessage)
                        .setCancelable(false)
                        .setPositiveButton(Html.fromHtml("<font color='#00FF00'>Agree</font>"), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, close
                                // current activity
                                checkinVisitor(view);
                            }
                        })
                        .setNegativeButton(Html.fromHtml("<font color='#FF0000'>Disagree</font>"), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
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

    public void checkinVisitor(View view) {
            Intent checkin = new Intent(this, checkinVisitor.class);
            startActivity(checkin);
            finish();
    }

    public void checkoutVisitor(View view) {
            Intent checkout = new Intent(this, checkoutVisitor.class);
            Log.e("intent is ", "calling checkout visitor");
            startActivity(checkout);
            finish();

    }

    public void datePicker(View view) {
            Intent date = new Intent(this, DatePicker.class);
            startActivity(date);
    }

    public void finish(View view) {
        super.finish();
    }
}
