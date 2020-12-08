package com.ChillChat.ChillChat;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static com.ChillChat.ChillChat.DatabaseService.deleteAnonymousUser;

import static android.graphics.Color.parseColor;

public class MenuActivity extends AppCompatActivity {
    //Variable for SharedPreference
    protected static final String FILE_NAME = "CurrentUser";
    //Variables for Navigation Drawer
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        getWindow().setStatusBarColor(parseColor("#3f77bc"));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        //Listener for all screen elements in the navigation drawer
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                //Gets triggered on nav_logout
                if (id == R.id.nav_logout) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
                    builder.setMessage("Are you sure you want to log out?")
                            .setTitle("Attention")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //Open shared preference from file location and open editor
                                    SharedPreferences prefs = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
                                    SharedPreferences.Editor edit = prefs.edit();
                                    //Edit the DefaultEmail to be text from email and commit changes
                                    edit.putString("Email", "Void");
                                    edit.putInt("groupNumber", 0);
                                    edit.commit();
                                    //If anonymous user must delete from the database below
                                    deleteAnonymousUser();
                                    //Set success to false then open activity
                                    LoginActivity.success = false;
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            })
                            .show();
                }
                //This is for maintaining the behavior of the Navigation view
                NavigationUI.onNavDestinationSelected(item, navController);
                //This is for closing the drawer after acting on it
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });
        //Open shared preference from file location and retrieve Email
        SharedPreferences prefs = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        String userEmail = prefs.getString("Email", "Void");
        // Sets the user email in nav_header_main
        View headerView = navigationView.getHeaderView(0);
        TextView navUserEmail = (TextView) headerView.findViewById(R.id.txtUserEmail);
        navUserEmail.setText(userEmail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_button, m);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_one) {
            Log.i("Test", "User tapped the rng button");

            // Get the current group number and then the new one
            int oldGroupNumber = DatabaseService.getGroupNumber(getApplicationContext());
            DatabaseService.randomizeGroup(getApplicationContext());
            int newGroupNumber = DatabaseService.getGroupNumber(getApplicationContext());

            // Pass both numbers into the helper
            DatabaseService.sendGroupMemberHelper(newGroupNumber, oldGroupNumber);

            Intent intent = getIntent();
            finish();
            startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}