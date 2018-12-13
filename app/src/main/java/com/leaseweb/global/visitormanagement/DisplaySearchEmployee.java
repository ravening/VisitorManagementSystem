package com.leaseweb.global.visitormanagement;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class DisplaySearchEmployee extends Activity {
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.display_search_employee);

        Bundle result = getIntent().getExtras();
        String firstName = result.getString("firstName");
        String lastName = result.getString("lastName");
        String department = result.getString("department");
        int badgeNumber = result.getInt("badgeNumber");
        String phoneNumber = result.getString("phoneNumber");
        String checkinTime = result.getString("checkinTime");
        String checkoutTime = result.getString("checkoutTime");

        TextView tvFirstName = (TextView) findViewById(R.id.firstNameText);
        tvFirstName.setText(firstName);

        TextView tvLastName = (TextView) findViewById(R.id.lastNameText);
        tvLastName.setText(lastName);

        TextView tvDepartment = (TextView) findViewById(R.id.departmentText);
        tvDepartment.setText(department);

        TextView tvPhone = (TextView) findViewById(R.id.phoneNumber);
        tvPhone.setText(phoneNumber);

        TextView tvBadgeNumber = (TextView) findViewById(R.id.badgeNumberText);
        tvBadgeNumber.setText("" + badgeNumber);

        TextView tvCheckinTime = (TextView) findViewById(R.id.checkinTimeText);
        tvCheckinTime.setText(checkinTime);

        TextView tvCheckoutTime = (TextView) findViewById(R.id.checkoutTimeText);
        if (checkoutTime != null && checkoutTime.equalsIgnoreCase("null")) {
            tvCheckoutTime.setText("---");
        } else {
            tvCheckoutTime.setText(checkoutTime);
        }

    }

    public void finish(View view) {
        super.finish();
    }
}
