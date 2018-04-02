package com.example.satish.messapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class StudentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "StudentActivity";
    private final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    private String RollNo;
    private boolean signedOut = false;
    private MenuItem menu_item; //currently selected item from the navigation drawer

    private TextView text1;

    private static final String FORMAT = "%02d:%02d:%02d";

    int seconds , minutes;
    private boolean booked=false;

    private String roll,messGot;
    private final String students = "students";
    private final String Name = "StdName";

    private final String Dues = "Dues";
    private final String Rollno = "RollNo";
    private final String Mess = "BookedMess";
    private final String Rate="Rate";
    private final String Seats="NoOfSeats";
    private final String messes="messes";
    private final String Breakfast = "Breakfast";
    private final String Lunch = "Lunch";
    private final String Tea = "Tea";
    private final String Dinner = "Dinner";
    private StringBuilder name = new StringBuilder("");
    int dues,seats,sSeats,ratenow;
    private StringBuilder duesD=new StringBuilder("");
    private StringBuilder givMess = new StringBuilder("");
    private StringBuilder givRoll = new StringBuilder("");
    private StringBuilder givRate=new StringBuilder("");
    private StringBuilder givSeats=new StringBuilder("");

    private StringBuilder rollnum = new StringBuilder("");
    private StringBuilder mess = new StringBuilder(""), sMess = new StringBuilder("");
    private StringBuilder breakfast = new StringBuilder(""), sBreakfast = new StringBuilder("");
    private StringBuilder lunch = new StringBuilder(""), sLunch = new StringBuilder("");
    private StringBuilder dinner = new StringBuilder(""), sDinner = new StringBuilder("");
    private StringBuilder tea = new StringBuilder(""), sTea = new StringBuilder("");
    private StringBuilder rate = new StringBuilder(""), sRate = new StringBuilder("");
    private StringBuilder seat=new StringBuilder(""), sSeat = new StringBuilder("");
    private DataSnapshot messesDS;
    private boolean firstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
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
        roll = bundle.getString("RollNo");

        myRef.addValueEventListener(new ValueEventListener() {  //Function runs whenver there's a change in the database
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //Toast.makeText(getApplicationContext(), "Successfully Booked", Toast.LENGTH_LONG).show();
                DataSnapshot studentDS = dataSnapshot.child(students).child(roll);
                messesDS = dataSnapshot.child(messes);

                name = new StringBuilder(studentDS.child(Name).getValue().toString());
                mess = new StringBuilder(studentDS.child(Mess).getValue().toString());
                rollnum = new StringBuilder(studentDS.child(Rollno).getValue().toString());

                TextView tv = (TextView) findViewById(R.id.NavStdName);
                tv.setText(name.toString());
                tv = (TextView) findViewById(R.id.NavStdRollno);
                tv.setText(roll);

                duesD =new StringBuilder(studentDS.child(Dues).getValue().toString());

                Long L = (Long) studentDS.child(Dues).getValue();
                dues = L.intValue();

                if(mess.toString().equals("")) {
                    booked = false;
                }
                else {
                    booked = true;
                    DataSnapshot messDS = dataSnapshot.child(messes).child(mess.toString());
                    breakfast = new StringBuilder(messDS.child(Breakfast).getValue().toString());
                    lunch = new StringBuilder(messDS.child(Lunch).getValue().toString());
                    tea = new StringBuilder(messDS.child(Tea).getValue().toString());
                    dinner = new StringBuilder(messDS.child(Dinner).getValue().toString());
                    rate = new StringBuilder(messDS.child(Rate).getValue().toString());
                    seat = new StringBuilder(messDS.child(Seats).getValue().toString());
                    L = (Long) messDS.child(Seats).getValue();
                    seats = L.intValue();
                }

                if(firstTime) {
                    showMenu();
                    firstTime = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) { //auto-generated
                // Failed to read value
            }
        });

        final String[] messOptions = getResources().getStringArray(R.array.mess_arrays);

        Spinner messSelector = (Spinner) findViewById(R.id.mess_selector);
        messSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tv;
                if(messOptions[i].equals("Select Mess")) {
                    tv = findViewById(R.id.menu_breakfast);
                    tv.setText("");

                    tv = findViewById(R.id.menu_lunch);
                    tv.setText("");

                    tv = findViewById(R.id.menu_tea);
                    tv.setText("");

                    tv = findViewById(R.id.menu_dinner);
                    tv.setText("");

                    tv = (TextView) findViewById(R.id.menu_rate);
                    tv.setText("");

                    tv = (TextView) findViewById(R.id.menu_numOfSeats);
                    tv.setText("");
                }

                else {
                    givMess = new StringBuilder(messOptions[i]);
                    DataSnapshot selectMessDS = messesDS.child(givMess.toString());
                    sBreakfast = new StringBuilder(selectMessDS.child(Breakfast).getValue().toString());
                    sLunch = new StringBuilder(selectMessDS.child(Lunch).getValue().toString());
                    sTea = new StringBuilder(selectMessDS.child(Tea).getValue().toString());
                    sDinner = new StringBuilder(selectMessDS.child(Dinner).getValue().toString());
                    sRate = new StringBuilder(selectMessDS.child(Rate).getValue().toString());
                    sSeat = new StringBuilder(selectMessDS.child(Seats).getValue().toString());
                    /*Long L = (Long) selectMessDS.child(Seats).getValue();
                    sSeats = L.intValue();*/

                    tv = findViewById(R.id.menu_breakfast);
                    tv.setText(sBreakfast.toString());

                    tv = findViewById(R.id.menu_lunch);
                    tv.setText(sLunch.toString());

                    tv = findViewById(R.id.menu_tea);
                    tv.setText(sTea.toString());

                    tv = findViewById(R.id.menu_dinner);
                    tv.setText(sDinner.toString());

                    tv = (TextView) findViewById(R.id.menu_rate);
                    tv.setText(sRate.toString());

                    tv = (TextView) findViewById(R.id.menu_numOfSeats);
                    tv.setText(sSeat.toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                TextView tv;
                tv = findViewById(R.id.menu_breakfast);
                tv.setText("");

                tv = findViewById(R.id.menu_lunch);
                tv.setText("");

                tv = findViewById(R.id.menu_tea);
                tv.setText("");

                tv = findViewById(R.id.menu_dinner);
                tv.setText("");

                tv = (TextView) findViewById(R.id.menu_rate);
                tv.setText("");

                tv = (TextView) findViewById(R.id.menu_numOfSeats);
                tv.setText("");
            }
        });

        Button bookMessButton = (Button) findViewById(R.id.book_button);

        bookMessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(givMess.equals("Select Mess"))
                {
                    toastMessage("Please Select a Mess",0);
                }
                else {

                    final DatabaseReference bookingRef = myRef.child(messes).child(givMess.toString()).child(Seats);
                    bookingRef.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            if(mutableData.getValue(int.class) == 0) {
                                return Transaction.abort();
                            }

                            else {
                                int seats = mutableData.getValue(int.class);
                                seats = seats - 1;
                                mutableData.setValue(seats);
                                return Transaction.success(mutableData);
                            }

                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                            if(committed) {
                                DataSnapshot ds = messesDS.child(givMess.toString());
                                Long L = (Long) ds.child(Rate).getValue();
                                ratenow = L.intValue();
                                dues += ratenow;
                                myRef.child(students).child(roll).child(Dues).setValue(dues);
                                myRef.child(students).child(roll).child(Mess).setValue(givMess.toString());
                                mess = new StringBuilder(givMess.toString());
                                myRef.child(messes).child(givMess.toString()).child("listOfStudents").push().setValue(roll);
                                toastMessage("Booking Successful",0);
                                booked = true;
                                menu_item.setChecked(false);
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }

                            else {
                                if(databaseError != null) {
                                    Log.w(TAG,"Database Error",databaseError.toException());
                                    toastMessage("Database Error",0);
                                }

                                else {
                                    toastMessage("No Seats Left",0);
                                }
                            }
                        }
                    });

                }
            }

        });
        //save instance





        //timer

        Calendar cal = Calendar.getInstance();
        int hourofday = cal.get(Calendar.HOUR_OF_DAY);

        /*long timeCheck= System.currentTimeMillis();
        Calendar calendarCheck=Calendar.getInstance();
        calendarCheck.set(Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.DATE),
                16,0,0);
        long startTimeCheck = calendarCheck.getTimeInMillis();
        Calendar calendarCheck2 = Calendar.getInstance();
        calendarCheck2.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DATE),
                22, 20, 0);

        long endTime1 = calendarCheck2.getTimeInMillis();*/
        if(hourofday >= 17 && hourofday <= 20) {
            final TextView tv;
            tv = (TextView) findViewById(R.id.book_prompt);
            long time = Calendar.getInstance().getTimeInMillis();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DATE),
                    21, 0, 0);

            long endTime = calendar.getTimeInMillis();
            new CountDownTimer(endTime - time, 1000) { // adjust the milli seconds here

                public void onTick(long millisUntilFinished) {

                    tv.setText("Booking Ends In : " + String.format(FORMAT,
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))+" Hours");
                }

                public void onFinish() {
                    tv.setText("Booking Period Is Over");
                }
            }.start();
        }

        else {
            TextView tv = (TextView) findViewById(R.id.book_prompt);
            tv.setText("Booking Not Done");
        }

    }

    private void showMenu() {
        if(!booked) {
            TextView tv = (TextView) findViewById(R.id.book_prompt);
            tv.setVisibility(View.VISIBLE);
            Spinner messSelector = (Spinner) findViewById(R.id.mess_selector);
            messSelector.setVisibility(View.GONE);
            tv = (TextView) findViewById(R.id.student_dues);
            tv.setVisibility(View.GONE);
            LinearLayout ll = (LinearLayout) findViewById(R.id.student_mess_menu);
            ll.setVisibility(View.GONE);
            ll = (LinearLayout) findViewById(R.id.chosen_mess_details);
            ll.setVisibility(View.GONE);
        }

        else {
            TextView tv = (TextView) findViewById(R.id.book_prompt);
            tv.setVisibility(View.GONE);
            tv = (TextView) findViewById(R.id.student_dues);
            tv.setVisibility(View.GONE);
            LinearLayout ll = (LinearLayout) findViewById(R.id.student_mess_menu);
            ll.setVisibility(View.VISIBLE);
            ll = (LinearLayout) findViewById(R.id.chosen_mess_details);
            ll.setVisibility(View.GONE);
            Spinner messSelector = (Spinner) findViewById(R.id.mess_selector);
            messSelector.setVisibility(View.GONE);

            tv = findViewById(R.id.menu_breakfast);
            tv.setText(breakfast.toString());

            tv = findViewById(R.id.menu_lunch);
            tv.setText(lunch.toString());

            tv = findViewById(R.id.menu_tea);
            tv.setText(tea.toString());

            tv = findViewById(R.id.menu_dinner);
            tv.setText(dinner.toString());
        }
    }

    private void showDues() {
        TextView tv;
        if(!booked) {
            tv = (TextView) findViewById(R.id.book_prompt);
            tv.setVisibility(View.VISIBLE);
        }

        LinearLayout ll = (LinearLayout) findViewById(R.id.student_mess_menu);
        ll.setVisibility(View.GONE);
        ll = (LinearLayout) findViewById(R.id.chosen_mess_details);
        ll.setVisibility(View.GONE);
        Spinner messSelector = (Spinner) findViewById(R.id.mess_selector);
        messSelector.setVisibility(View.GONE);

        tv = (TextView) findViewById(R.id.student_dues);
        tv.setVisibility(View.VISIBLE);
        tv.setText(duesD.toString() + " Rs");
    }


    private void bookMess() {
        if(booked) {
            toastMessage("Already Booked",0);
            return;
        }
        Calendar cal = Calendar.getInstance();
        int hourofday = cal.get(Calendar.HOUR_OF_DAY);
        if(hourofday >= 21) {
            toastMessage("Booking Period Over",0);
            return;
        }
        if(hourofday < 17) {
            toastMessage("Booking hasn't started",0);
            return;
        }
        TextView tv;
        tv = (TextView) findViewById(R.id.book_prompt);
        tv.setVisibility(View.VISIBLE);
        tv = (TextView) findViewById(R.id.student_dues);
        tv.setVisibility(View.GONE);
        LinearLayout ll = (LinearLayout) findViewById(R.id.student_mess_menu);
        ll.setVisibility(View.VISIBLE);
        ll = (LinearLayout) findViewById(R.id.chosen_mess_details);
        ll.setVisibility(View.VISIBLE);
        Spinner messSelector = (Spinner) findViewById(R.id.mess_selector);
        messSelector.setVisibility(View.VISIBLE);

    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();   //signs out
        signedOut = true;
        Intent intent = new Intent(StudentActivity.this,LoginActivity.class);
        startActivity(intent);  //starts LoginActivity
        finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.student, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_student_signout) {
            FirebaseAuth.getInstance().signOut();   //signs out
            signedOut = true;
            Intent intent = new Intent(StudentActivity.this,LoginActivity.class);
            startActivity(intent);  //starts LoginActivity
            finish();
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

        if (id == R.id.nav_menu) {
            showMenu();
        }
        else if (id == R.id.nav_dues) {
            showDues();
        }
        else if (id == R.id.nav_book) {
            bookMess();
        }
        else if (id == R.id.nav_logout) {
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void toastMessage(String message, int length) {
        if(length == 0)
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show();

        else
            Toast.makeText(this,message,Toast.LENGTH_LONG).show();

    }
}
