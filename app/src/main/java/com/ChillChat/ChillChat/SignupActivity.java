package com.ChillChat.ChillChat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import static android.graphics.Color.parseColor;

public class SignupActivity extends AppCompatActivity {
    //Class variables
    private FirebaseAuth mAuth;
    private static boolean success = false;
    private static final String TAG = "EmailPassword";
    //Screen elements
    private static EditText txtUsername;
    private static EditText txtPassword;
    private static EditText txtConfirmPassword;
    //Variable for SharedPreference
    protected static final String FILE_NAME = "CurrentUser";

    /**
     Runs when onCreate() state is called.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getWindow().setStatusBarColor(parseColor("#0080ff"));
        //Firebase Authorization
        mAuth = FirebaseAuth.getInstance();
        //Get screen elements
        txtUsername = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtConfirmPassword = findViewById(R.id.txtConfirmPassword);
    }

    /**
     Runs when onStart() state is called.
     This function is used to check if the user is already signed in, preventing invalid logout
     */
    @Override
    public void onStart() {
        super.onStart();
        //Open shared preference from file location and retrieve Email
        SharedPreferences prefs = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        String username = prefs.getString("Email", "Void");
        //Compare the stored username to Void to see if a user is currently signed it
        if(username.compareTo("Void") != 0) {
            Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);
        }
    }

    /**
     onClick listener for btnSignup.
     Attempts to register the user with the information provided.
     */
    public void register(View view) {
        //Get text from screen elements
        String email = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();
        String retype = txtConfirmPassword.getText().toString();

        //Checks to see if all fields are filled in and if the password matches the retyped password. Then attempts to create account.
        if (email.isEmpty() || password.isEmpty() || retype.isEmpty()) {
            createAlert("Please fill in all fields before continuing.", "Attention!");
        } else if (isValid(email) == false) {
            createAlert("Please enter a valid email address.", "Email!");
        }else if(password.length() >= 6 && password.equals(retype)) {
            //Attempt to create the account
            createAccount(email, password);
            txtUsername.setText("");
            txtPassword.setText("");
            txtConfirmPassword.setText("");
        } else {
            createAlert("Please enter a 6 digit password in both locations.", "Password!");
        }
    }

    /**
     This function will allow you to create a popup alert on the screen
     * @param email - the user entered password
     * @param password - the user entered password
     */
    protected void createAccount(final String email, String password) {
        // Check authentication with google firebase method
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //If the authentication is successful, Login, create toast(msg), startActivity, getInstance of Singleton for global user variable
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(SignupActivity.this, "Account Created!", Toast.LENGTH_SHORT).show();
                            //Open shared preference from file location and open editor
                            SharedPreferences prefs = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor edit = prefs.edit();
                            //Edit the Email to be text from email and commit changes
                            edit.putString("Email", email);
                            edit.commit();
                            startActivity(new Intent(SignupActivity.this, ChatActivity.class));
                            success = true;
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     This function will allow you to create a popup alert on the screen
     * @param msg - Message that will be displayed
     * @param title - The title that is displayed at the top of the popup
     */
    protected void createAlert(String msg, String title) {
        AlertDialog alertDialog = new AlertDialog.Builder(SignupActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    /**
     This function will check if an email is valid
     * email - the users email (String)
     * returns: boolean based on found result (bool)
     */
    protected static boolean isValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }
}