package com.example.jerry.healemgood.view.UserViews;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jerry.healemgood.R;
import com.example.jerry.healemgood.config.AppConfig;
import com.example.jerry.healemgood.controller.ProblemController;
import com.example.jerry.healemgood.controller.SwipeDetector;
import com.example.jerry.healemgood.model.problem.Problem;
import com.example.jerry.healemgood.utils.SharedPreferenceUtil;
import com.example.jerry.healemgood.view.UserViews.adapter.ProblemAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PatientAllProblemActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ArrayList<Problem> problems;
    private ProblemAdapter problemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_all_problem);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Problem");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);
        TextView nav_user = (TextView)hView.findViewById(R.id.drawerUserId);
        TextView nav_email = (TextView)hView.findViewById(R.id.drawerEmail);
        nav_user.setText(SharedPreferenceUtil.get(this,AppConfig.USERID));
        nav_email.setText(SharedPreferenceUtil.get(this,AppConfig.EMAIL));


        ListView mListView;
        Button createProblemButton;

        mListView = findViewById(R.id.patientProblemListView);
        createProblemButton = findViewById(R.id.createProblemButton);

        loadProblems();


        problemAdapter = new ProblemAdapter(this,R.layout.problems_list_view_custom_layout,problems);

        mListView.setAdapter(problemAdapter);
        final SwipeDetector swipeDetector = new SwipeDetector();
        mListView.setOnTouchListener(swipeDetector);


        createProblemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PatientAddProblemActivity.class);
                startActivity(intent);
            }
        });


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(swipeDetector.swipeDetected()) {
                    if(swipeDetector.getAction() == SwipeDetector.Action.LR){
                        deleteProblem(position);
                    }
                }
                else{
                    String pId = problems.get(position).getpId();
                    Intent intent = new Intent(PatientAllProblemActivity.this,PatientAllRecordActivity.class);
                    intent.putExtra(AppConfig.PID,pId);
                    startActivity(intent);
                }

            }
        });

    }


    @Override
    protected void onResume(){

        super.onResume();
        loadProblems();
        problemAdapter.refreshAdapter(problems);

    }

    private void loadProblems(){
        ProblemController.searchByPatientIds(SharedPreferenceUtil.get(this,AppConfig.USERID));
        try{
            problems = new ProblemController.SearchProblemTask().execute().get();


        }
        catch (Exception e){
            Log.d("Error","Fail to get the problems");
            problems = new ArrayList<Problem>();
        }



    }

    private void deleteProblem(int i){

        try {
            new ProblemController.DeleteProblemTask().execute(problems.get(i)).get();
            problems.remove(i);
            // notify changes
            problemAdapter.refreshAdapter(problems);
        }
        catch (Exception e){
            Log.d("ERROR","FAIL to delete problem");
        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.patient_all_problem, menu);
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


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (id == R.id.navigation_history) {
            startActivity(new Intent(getApplicationContext(),PatientHistoryActivity.class));
        } else if (id == R.id.navigation_search) {
            startActivity(new Intent(getApplicationContext(),PatientSearchActivity.class));

        } else if (id == R.id.navigation_user) {
            startActivity(new Intent(getApplicationContext(),PatientUserActivity.class));
        }


        return true;
    }
}
