package com.ChillChat.ChillChat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import static com.ChillChat.ChillChat.DatabaseService.updateUserData;

public class LoginActivity extends AppCompatActivity {
    //Firebase
    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";
    public static boolean success = false;
    //private CallbackManager callbackManager;
    private static final String EMAIL = "email";
    //Variable for SharedPreference
    protected static final String FILE_NAME = "CurrentUser";

    /**
     Runs when onCreate() state is called.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Basic operations
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setStatusBarColor(parseColor("#0080ff"));
        //Firebase
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     Runs when onStart() state is called.
     This function is used to check if the user is already signed in, avoiding the login process.
     */
    @Override
    public void onStart() {
        super.onStart();
        //Open shared preference from file location and retrieve Email
        SharedPreferences prefs = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        String username = prefs.getString("Email", "Void");
        //Compare the stored username to Void to see if a user is currently signed it
        if(username.compareTo("Void") != 0) {
            Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
            startActivity(intent);
        }
    }

    /**
     onClick listener for btnLogin.
     Checks to see if the email and password are formatted, then attempts signIn()
     */
    public void startLogin(View view) {
        //Get screen elements
        final EditText txtEmail = findViewById(R.id.txtEmail);
        final EditText txtPassword = findViewById(R.id.txtPassword);
        String sEmail = txtEmail.getText().toString();
        String sPassword = txtPassword.getText().toString();
        //Check to see if the email and password is not null
        if (sEmail.equalsIgnoreCase("") || sPassword.equalsIgnoreCase("") || isValid(sEmail) == false) {
            Toast.makeText(LoginActivity.this, "Enter a valid Username and/or password.",Toast.LENGTH_SHORT).show();
        } else {
            //Attempt to sign in
            signIn(sEmail, sPassword);
            if(success == true) {
                txtEmail.setText("");
                txtPassword.setText("");
            }
        }
    }

    /**
     This function will attempt to sign into Firebase
     * email - the users email (String)
     * password - the users password (String)
     */
    private void signIn(final String email, String password) {
        Log.d(TAG, "signIn:" + email);

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Open shared preference from file location and open editor
                            SharedPreferences prefs = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor edit = prefs.edit();
                            //Edit the Email to be text from email and commit changes
                            edit.putString("Email", email);
                            edit.commit();
                            // Sign in success, update UI with the signed-in user's information
                            Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
                            startActivity(intent);
                            success = true;
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     onClick listener for btnRegister
     Opens the SignupActivity to allow user to register
     */
    public void register(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    /**
     onClick listener for btnAnonLogin
     Opens the ChatActivity to allow user to anonymously chat
     */

    public void startChat(View view) {
        //Firebase creates Authentication for Anonymous ID
        mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Place Anonymous Data into Database
                    updateUserData("null",  "Anonymous");
                    SharedPreferences prefs = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString("Email", "Anonymous");
                    edit.commit();
                    Toast.makeText(LoginActivity.this, "Anonymous Login Complete.",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LoginActivity.this, ChatActivity.class));
                }
            }
        });

        //Old Code
//        SharedPreferences prefs = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
//        SharedPreferences.Editor edit = prefs.edit();
//        //Edit the Email to be text from email and commit changes
//        edit.putString("Email", "Anonymous");
//        edit.commit();
//        //Start ChatActivity with temporary email.
//        Intent intent = new Intent(this, ChatActivity.class);
//        startActivity(intent);
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
