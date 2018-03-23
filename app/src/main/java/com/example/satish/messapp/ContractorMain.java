package com.example.satish.messapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ContractorMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "ContractorActivity";
    final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

    private String Cid,messID;
    private final String Breakfast = "Breakfast";
    private final String Lunch = "Lunch";
    private final String Tea = "Tea";
    private final String Dinner = "Dinner";
    private final String Rate = "Rate";
    private final String NumOfSeats = "NoOfSeats";
    private final String ListOfStudents = "listOfStudents";
    private final String messes = "messes";
    private StringBuilder breakfast = new StringBuilder("");
    private StringBuilder lunch = new StringBuilder("");
    private StringBuilder tea = new StringBuilder("");
    private StringBuilder dinner = new StringBuilder("");
    private List<String> listOfStudents = new ArrayList<String>();
    int rate=0,numOfSeats=0,edit = 0;   //edit = 1 => user is currently editing some field
    private boolean signedOut = false;

    private MenuItem menu_item; //currently selected item from the navigation drawer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //auto-generated
        setContentView(R.layout.activity_contractor_main);  //auto-generated
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); //auto-generated
        setSupportActionBar(toolbar); //auto-generated

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Bundle bundle = getIntent().getExtras();
        Cid = bundle.getString("Cid");
        messID = bundle.getString("messID");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {  //Function runs whenver there's a change in the database
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                DataSnapshot messDS = dataSnapshot.child(messes).child(messID);
                breakfast = new StringBuilder(messDS.child(Breakfast).getValue().toString());
                lunch = new StringBuilder(messDS.child(Lunch).getValue().toString());
                tea = new StringBuilder(messDS.child(Tea).getValue().toString());
                dinner = new StringBuilder(messDS.child(Dinner).getValue().toString());
                Long L = (Long) messDS.child(Rate).getValue();
                rate = L.intValue();
                L = (Long) messDS.child(NumOfSeats).getValue();
                numOfSeats = L.intValue();

                listOfStudents.clear(); //clear all contents of the array

                for(DataSnapshot ds : messDS.child(ListOfStudents).getChildren()) {
                    listOfStudents.add(ds.getValue().toString());
                }

                fillContent();
            }

            @Override
            public void onCancelled(DatabaseError error) { //auto-generated
                // Failed to read value
                if(!signedOut) {
                    toastMessage("Database Error",0);
                    Log.w(TAG, "Database error", error.toException());
                }
            }
        });

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    //when save button is clicked
                Calendar cal = Calendar.getInstance();
                int hourofday = cal.get(Calendar.HOUR_OF_DAY);

                if(hourofday >= 17 && hourofday <= 20) {    //if time is between 17:00 and 21:00 contractor cannot change mess details
                    toastMessage("Update Failed as Booking Period has begun",0);
                }

                else {
                    EditText editText = (EditText) findViewById(R.id.item_breakfast);
                    breakfast = new StringBuilder(editText.getText().toString());   //extract value from the editText field
                    editText = (EditText) findViewById(R.id.item_lunch);
                    lunch = new StringBuilder(editText.getText().toString());
                    editText = (EditText) findViewById(R.id.item_tea);
                    tea = new StringBuilder(editText.getText().toString());
                    editText = (EditText) findViewById(R.id.item_dinner);
                    dinner = new StringBuilder(editText.getText().toString());

                    editText = (EditText) findViewById(R.id.item_rate);
                    rate = Integer.parseInt(editText.getText().toString());
                    editText = (EditText) findViewById(R.id.item_numOfSeats);
                    numOfSeats = Integer.parseInt(editText.getText().toString());

                    myRef.child(messes).child(messID).child(Breakfast).setValue(breakfast.toString());  //sets value at that path to new value
                    myRef.child(messes).child(messID).child(Lunch).setValue(lunch.toString());
                    myRef.child(messes).child(messID).child(Tea).setValue(tea.toString());
                    myRef.child(messes).child(messID).child(Dinner).setValue(dinner.toString());
                    myRef.child(messes).child(messID).child(Rate).setValue(rate);
                    myRef.child(messes).child(messID).child(NumOfSeats).setValue(numOfSeats);

                    menu_item.setChecked(false);
                }
            }
        });
    }

    public void getListOfStudents() {   //to be completed
        LinearLayout ll = (LinearLayout) findViewById(R.id.contractor_main_layout);
        ll.setVisibility(View.GONE);

        ll = (LinearLayout) findViewById(R.id.student_list_layout);
        ll.setVisibility(View.VISIBLE);

        ListView lv = (ListView) findViewById(R.id.list_of_students);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.listrow, listOfStudents);
        lv.setAdapter(arrayAdapter);

        edit = 1; //this is not because we're editing anything but rather to go back to displaying mess details on hitting back button
        //see onBackPressed()
    }

    public void fillContent(){
        LinearLayout ll = (LinearLayout) findViewById(R.id.contractor_main_layout);
        ll.setVisibility(View.VISIBLE); //we want to show only the mess data and not the student list

        ll = (LinearLayout) findViewById(R.id.student_list_layout);
        ll.setVisibility(View.GONE);

        EditText editText = (EditText) findViewById(R.id.item_breakfast);
        editText.setText("");
        editText.setText(breakfast.toString()); //set value to current value of breakfast
        editText.setFocusable(false);   //Set edit-text field to not editable until user clicks on updateMenu option again

        editText = (EditText) findViewById(R.id.item_lunch);
        editText.setText("");
        editText.setText(lunch.toString());
        editText.setFocusable(false);

        editText = (EditText) findViewById(R.id.item_tea);
        editText.setText("");
        editText.setText(tea.toString());
        editText.setFocusable(false);

        editText = (EditText) findViewById(R.id.item_dinner);
        editText.setText("");
        editText.setText(dinner.toString());
        editText.setFocusable(false);

        editText = (EditText) findViewById(R.id.item_rate);
        editText.setText("");
        editText.setText(String.valueOf(rate));
        editText.setFocusable(false);

        editText = (EditText) findViewById(R.id.item_numOfSeats);
        editText.setText("");
        editText.setText(String.valueOf(numOfSeats));
        editText.setFocusable(false);

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setVisibility(View.INVISIBLE);   //save-button is hidden since it's not required when user is not editing anything

        edit = 0;   //user is not editing now
    }

    public void updateMenu(){   //function to enable editing Menu
        LinearLayout ll = (LinearLayout) findViewById(R.id.contractor_main_layout);
        ll.setVisibility(View.VISIBLE);

        ll = (LinearLayout) findViewById(R.id.student_list_layout);
        ll.setVisibility(View.GONE);

        EditText editText = (EditText) findViewById(R.id.item_breakfast);
        editText.setFocusableInTouchMode(true); //now this becomes editable again
        editText = (EditText) findViewById(R.id.item_lunch);
        editText.setFocusableInTouchMode(true);
        editText = (EditText) findViewById(R.id.item_tea);
        editText.setFocusableInTouchMode(true);
        editText = (EditText) findViewById(R.id.item_dinner);
        editText.setFocusableInTouchMode(true);

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setVisibility(View.VISIBLE);
        edit = 1;   //user wants to edit now
    }

    public void updateRate() { //function to enable editing Rate
        LinearLayout ll = (LinearLayout) findViewById(R.id.contractor_main_layout);
        ll.setVisibility(View.VISIBLE);

        ll = (LinearLayout) findViewById(R.id.student_list_layout);
        ll.setVisibility(View.GONE);

        EditText editText = (EditText) findViewById(R.id.item_rate);
        editText.setFocusableInTouchMode(true);

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setVisibility(View.VISIBLE);
        edit = 1;
    }

    public void updateSeats() { //function to enable editing number of Seats
        LinearLayout ll = (LinearLayout) findViewById(R.id.contractor_main_layout);
        ll.setVisibility(View.VISIBLE);

        ll = (LinearLayout) findViewById(R.id.student_list_layout);
        ll.setVisibility(View.GONE);

        EditText editText = (EditText) findViewById(R.id.item_numOfSeats);
        editText.setFocusableInTouchMode(true);

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setVisibility(View.VISIBLE);
        edit = 1;
    }

    @Override
    public void onBackPressed() {   //Inbuilt function that handles backbutton presses
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) { //if drawer is open, close it
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(edit == 1) { //if user was editing something and decided to cancel it by pressing back-button
                edit = 0;
                fillContent();  //set Content back to most recently saved values
                menu_item.setChecked(false);    //uncheck the option (the option in the drawer) chosen by user
            }

            else {
                super.onBackPressed();  //default action on back-button press (which is exiting the app)
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //auto-generated
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.contractor_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //auto-generated
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_signout) {    //to handle sign-out
            FirebaseAuth.getInstance().signOut();   //signs out
            signedOut = true;
            Intent intent = new Intent(ContractorMain.this,LoginActivity.class);
            startActivity(intent);  //starts LoginActivity
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) { //auto-generated
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        fillContent();  //reload last saved content before proceeding
        menu_item = item;
        menu_item.setChecked(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        Calendar cal = Calendar.getInstance();
        int hourofday = cal.get(Calendar.HOUR_OF_DAY);

        if(hourofday >= 17 && hourofday <= 20) {    //if time is between 17:00 and 21:00 contractor cannot change mess details
            toastMessage("This Action Is Blocked in Booking Hours",0);
            menu_item.setChecked(false);
            return true;
        }

        if (id == R.id.nav_menu) {  //if user chose update Menu option
            updateMenu();

        } else if (id == R.id.nav_rate) {
            updateRate();

        } else if (id == R.id.nav_updateSeats) {
            updateSeats();

        } else if (id == R.id.nav_listOfStudents) {
            getListOfStudents();

        }

        return true;
    }

    private void toastMessage(String message, int length) {
        if(length == 0)
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show();

        else
            Toast.makeText(this,message,Toast.LENGTH_LONG).show();

    }
}
