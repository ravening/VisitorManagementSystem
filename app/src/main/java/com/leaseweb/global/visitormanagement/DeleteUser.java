package com.leaseweb.global.visitormanagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.leaseweb.global.visitormanagement.helper.DatabaseHandler;
import com.leaseweb.global.visitormanagement.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeleteUser extends Activity {
    private ListView userListView;
    private static final String ADMIN = "admin";
    private static final String ADMIN_EMAIL = "admin@leaseweb.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_visitors);

        userListView = (ListView) findViewById(R.id.List);

        final DatabaseHandler db = new DatabaseHandler(this);
        final ArrayList<HashMap<String, String>> Users = new ArrayList<HashMap<String, String>>();

        final List<User> userList = db.getAllUser();
        if (userList == null || userList.size() == 0) {
            Toast.makeText(getApplicationContext(),
                    "No users to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        for (User user : userList) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("name", user.getName());
            map.put("email", user.getEmail());
            //map.put("id", Integer.toString(user.getId()));

            Users.add(map);
        }

        ListAdapter adapter = new SimpleAdapter(this, Users, R.layout.list_user,
                new String[]{"name", "email"}, new int[]{R.id.name, R.id.email});
        userListView.setAdapter(adapter);

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, final long item_id) {
                //Dialogue box
                final AlertDialog.Builder delete = new AlertDialog.Builder(DeleteUser.this);

                //Item to be deleted
                final User deletedItemName = userList.get(position);

                if (deletedItemName.getName().equalsIgnoreCase(ADMIN) &&
                        deletedItemName.getEmail().equalsIgnoreCase(ADMIN_EMAIL)) {
                    Toast.makeText(getApplicationContext(), ADMIN + " user cannot be deleted", Toast.LENGTH_LONG).show();
                    return;
                }
                //setup the dialogue box
                delete.setTitle("Delete User");
                delete.setMessage("Are you sure you want to delete the user " + deletedItemName.getName() + "?");
                delete.setNegativeButton(Html.fromHtml("<font color='#00FF00'>CANCEL</font>"), null);

                //Callback to delete the item
                delete.setPositiveButton(Html.fromHtml("<font color='#FF0000'>DELETE</font>"),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                db.deleteUser(deletedItemName);
                                Toast.makeText(getApplicationContext(),
                                        deletedItemName.getName() + " Deleted", Toast.LENGTH_SHORT).show();

                                //Remove the item from contactList
                                userList.remove(position);
                                Users.remove(position);
                                final Adapter adapt = parent.getAdapter();
                                ((BaseAdapter)adapt).notifyDataSetChanged();
                                if (userList.isEmpty()) {
                                    finish();
                                }
                            }
                        });
                delete.show();
            }
        });

    }
}
