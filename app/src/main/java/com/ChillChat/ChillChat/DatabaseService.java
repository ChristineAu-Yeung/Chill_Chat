package com.ChillChat.ChillChat;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DatabaseService {
    private static final String TAG = "DatabaseService";

    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // This is a reference to all of our different collections. This way we don't have to type a
    // lot of code to access the same collection over and over again
    final CollectionReference userCollection = db.collection("users");
    final CollectionReference groupCollection = db.collection("groups");
    // make a user collection
    // Make a timestamp on registration
    // Make a blank age
    // Make a blank bio
    // Make a default profile pic

    /**
     * This function uses the .set() function to create user documents for the database.
     * This is where we store extra user data after they created after successful registration.
     * Basic information can be access with FirebaseAuth.getInstance().getUser . . .getEmail(), etc.
     *
     * @param uid       UID of the user
     * @param email     Email address of the user (for testing)
     * @param firstName First name of the user (to add to the user record)
     */
    void setUserData(String uid, String email, String firstName) {
        // Create a user object for the document and add data to it.
        // This can be expanded in the future
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("firstName", firstName);

        // Add the user to the User Collection
        userCollection.document(uid).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    void setGroupData(String id, String name) {
        Map<String, Object> group = new HashMap<>();

        // Add the group to the Group Collection
        groupCollection.add(group)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    /**
     * Update the user's document as well as profile data
     * This function should stay private to this class
     *
     * @param firstName User's first name
     * @param email     User's email address
     */
    static void updateUserData(String email, String firstName) {
        // Create user document
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseService db = new DatabaseService();

        if (user != null) {
            db.setUserData(user.getUid(), email, firstName);

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(firstName)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "User profile updated.");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "User profile was not updated.");
                        }
                    });
        } else {
            Log.w(TAG, "fuck. the user is nullllllllllllllllllll");
        }
    }

    /**
     * Update the current group's message array with the properties of the ChatMessage class.
     *
     * @param message Instance of the ChatMessage class
     */
    public void sendMessage(ChatMessage message) {
        Map<String, Object> msg = new HashMap<>();

        // More data can be added just by writing lines similar to the two below
        msg.put("message", message.message);
        msg.put("sender", message.firstName);

        groupCollection
                .document("Rd9DOKVw33lCtfzSnvjV") // TODO Update this so the document path is equal to the group number
                .update("messages", FieldValue.arrayUnion(msg))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Message sent");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Message not sent");
                    }
                });

    }

    /**
     * TODO: Finish this function with realtime checks
     *
     * @param groupNumber The group from which we grab messages
     * @return messages - The list of ChatMessage objects (might change later)
     */
    public ArrayList<ChatMessage> getMessages(int groupNumber){
        ArrayList<ChatMessage> messages = new ArrayList<>();

        return messages;
    }

    /**
     * @return - Null if the user does not exist
     * - [String] "Anonymous" if they're anon
     * - [String] User's full display name if they do exist. Space delimited if there are
     * middle and last names
     */
    public static String getDisplayName() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && user.isAnonymous()) {
            return "Anonymous";
        } else if (user != null) {
            return user.getDisplayName();
        } else {
            return null;
        }
    }

    /**
     * Gets the user's photo url (AKA profile pic).
     *
     * @return URI that directs to the user's stored image URL in firebase.
     */
    public static Uri getImageUrl(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && user.isAnonymous()) {
            return Uri.parse("https://i.redd.it/95pfytrlsl241.jpg"); // S M O O T H B R A I N
        } else if (user != null) {
            return user.getPhotoUrl();
        } else {
            return null;
        }
    }

    /**
     * TODO Code this so it takes the image's location and stores it in firebase first
     * Sets the user's profile pic to whatever they uploaded.
     *
     * @param imageUrl The URI of the new profile pic once it is updated on the databse
     */
    public static void setImageUrl(Uri imageUrl){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null && user.isAnonymous()){
            Log.w(TAG, "Anonymous users should not be able to change their profile pictures.");
        }
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(imageUrl)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "Profile picture updated.");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Profile picture was not updated.");
                        }
                    });
        } else {
            Log.w(TAG, "fuck. the user is nullllllllllllllllllll");
        }
    }
}
