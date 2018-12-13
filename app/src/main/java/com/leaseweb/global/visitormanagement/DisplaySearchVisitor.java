package com.leaseweb.global.visitormanagement;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class DisplaySearchVisitor extends Activity {
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.display_search_visitor);

        Bundle result = getIntent().getExtras();
        String firstName = result.getString("firstName");
        String lastName = result.getString("lastName");
        String phone = result.getString("phone");
        String reasonForVisit = result.getString("reasonForVisit");
        String contactPerson = result.getString("contactPerson");
        String comingFrom = result.getString("comingFrom");
        String licensePlate = result.getString("licensePlate");
        int badgeNumber = result.getInt("badgeNumber");
        String checkinTime = result.getString("checkinTime");
        String checkoutTime = result.getString("checkoutTime");

        TextView tvFirstName = (TextView) findViewById(R.id.firstNameText);
        tvFirstName.setText(firstName);

        TextView tvLastName = (TextView) findViewById(R.id.lastNameText);
        tvLastName.setText(lastName);

        TextView tvPhone = (TextView) findViewById(R.id.phoneNumber);
        tvPhone.setText(phone);

        TextView tvReasonForVisit = (TextView) findViewById(R.id.reasonForVisit);
        tvReasonForVisit.setText(reasonForVisit);

        TextView tvContactPerson = (TextView) findViewById(R.id.contactPerson);
        tvContactPerson.setText(contactPerson);

        TextView tvLicensePlate = (TextView) findViewById(R.id.licensePlateText);
        tvLicensePlate.setText(licensePlate);

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
