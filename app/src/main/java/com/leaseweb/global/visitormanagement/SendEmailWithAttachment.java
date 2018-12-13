package com.leaseweb.global.visitormanagement;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class SendEmailWithAttachment extends Activity {
    private InputValidation inputValidation;
    private static final int CSV = 0;
    private static final int TXT = 1;
    int fileType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_email_with_attachment);
        Bundle result = getIntent().getExtras();
        fileType = result.getInt("fileType");
    }

    public void sendEmail(View view) {
        EditText emailAddress = (EditText) findViewById(R.id.email);
        String value = emailAddress.getText().toString().trim();
        if (value.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            Toast.makeText(SendEmailWithAttachment.this, "Please enter valid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File visitorFile = new File(exportDir, "Visitors.csv");
        File employeeFile = new File(exportDir, "Employees.csv");
        File supplierFile = new File(exportDir, "Suppliers.csv");
       // File outputFile = new File(Environment.getExternalStorageDirectory(),"logcat.txt");
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        try {
            //Uri path = Uri.fromFile(visitorFile);
            ArrayList<Uri> uris = new ArrayList<Uri>();
            Uri u = Uri.fromFile(visitorFile);
            uris.add(u);
            u = Uri.fromFile(employeeFile);
            uris.add(u);
            u = Uri.fromFile(supplierFile);
            uris.add(u);
            Intent emailIntent = null;

            switch (fileType) {
                case CSV:
                    emailIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
                    emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    emailIntent.setType("plain/text");
                    break;

                case TXT:
                    emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    emailIntent.putExtra(android.content.Intent.EXTRA_STREAM, uris);
                    emailIntent.setType("vnd.android.cursor.dir/email");
                    break;
            }

            // set the type to 'email'
            //emailIntent.setType("application/octet-stream");


            //emailIntent.setType("vnd.android.cursor.dir/email");
            String toAddress[] = {emailAddress.getText().toString()};
            emailIntent.putExtra(Intent.EXTRA_EMAIL, toAddress);

            // the mail subject
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Visitors management system");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));

            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(SendEmailWithAttachment.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}

