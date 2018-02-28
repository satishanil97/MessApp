package com.example.satish.messapp;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private final String messes = "messes";
    private StringBuilder breakfast = new StringBuilder("");
    private StringBuilder lunch = new StringBuilder("");
    private StringBuilder tea = new StringBuilder("");
    private StringBuilder dinner = new StringBuilder("");
    int rate=0,numOfSeats=0,edit = 0;

    private MenuItem menu_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contractor_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        myRef.addValueEventListener(new ValueEventListener() {
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
                fillContent();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                toastMessage("Database Error",0);
                Log.w(TAG, "Database error", error.toException());
            }
        });

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = (EditText) findViewById(R.id.item_breakfast);
                breakfast = new StringBuilder(editText.getText().toString());
                editText = (EditText) findViewById(R.id.item_lunch);
                lunch = new StringBuilder(editText.getText().toString());
                editText = (EditText) findViewById(R.id.item_tea);
                tea = new StringBuilder(editText.getText().toString());
                editText = (EditText) findViewById(R.id.item_dinner);
                dinner = new StringBuilder(editText.getText().toString());

                myRef.child(messes).child(messID).child(Breakfast).setValue(breakfast.toString());
                myRef.child(messes).child(messID).child(Lunch).setValue(lunch.toString());
                myRef.child(messes).child(messID).child(Tea).setValue(tea.toString());
                myRef.child(messes).child(messID).child(Dinner).setValue(dinner.toString());

                menu_item.setChecked(false);
                fillContent();
            }
        });
    }

    public void fillContent(){
        EditText editText = (EditText) findViewById(R.id.item_breakfast);
        editText.setText("");
        editText.setText(breakfast.toString());
        editText.setFocusable(false);

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
        saveButton.setVisibility(View.INVISIBLE);

        edit = 0;
    }

    public void updateMenu(){
        EditText editText = (EditText) findViewById(R.id.item_breakfast);
        editText.setFocusableInTouchMode(true);
        editText = (EditText) findViewById(R.id.item_lunch);
        editText.setFocusableInTouchMode(true);
        editText = (EditText) findViewById(R.id.item_tea);
        editText.setFocusableInTouchMode(true);
        editText = (EditText) findViewById(R.id.item_dinner);
        editText.setFocusableInTouchMode(true);

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setVisibility(View.VISIBLE);
        edit = 1;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(edit == 1) {
                edit = 0;
                fillContent();
                menu_item.setChecked(false);
            }

            else {
                super.onBackPressed();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.contractor_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        menu_item = item;
        menu_item.setChecked(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (id == R.id.nav_menu) {
            updateMenu();
        } else if (id == R.id.nav_rate) {

        } else if (id == R.id.nav_updateSeats) {

        } else if (id == R.id.nav_listOfStudents) {

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
