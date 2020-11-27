package com.ChillChat.ChillChat;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseService {
    private static final String TAG = "DatabaseService";

    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // This is a reference to all of our different collections. This way we don't have to type a
    // lot of code to access the same collection over and over again
    final CollectionReference userCollection = db.collection("users");
    final CollectionReference groupCollection = db.collection("groups");

    /**
     * Helper Function, this is a helper method that should stay private to this class
     * This function uses the .set() function to create user documents for the database.
     * This is where we store extra user data after they created after successful registration.
     * Basic information can be access with FirebaseAuth.getInstance().getUser . . .getEmail(), etc.
     *
     * @param uid       UID of the user
     * @param email     Email address of the user (for testing)
     * @param firstName First name of the user (to add to the user record)
     */
    private void setUserData(String uid, String email, String firstName) {
        // Create a user object for the document and add data to it.
        // This can be expanded in the future
        Map<String, Object> user = new HashMap<>();


        user.put("email", email);
        user.put("firstName", firstName);
        user.put("dateRegistered", FieldValue.serverTimestamp());

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

    public void getUserData() {
        DocumentReference reference = userCollection.document(user.getUid());

        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                Map<String, Object> data = document.getData();

                                //log and add every field data in the user document
                                ProfileFragment.userData.add(data.get("firstName").toString());
                                Log.i(TAG, "firstName: " + ProfileFragment.userData.get(0));

                                ProfileFragment.userData.add(data.get("email").toString());
                                Log.i(TAG, "email: " + ProfileFragment.userData.get(1));

                                //userData.add(data.get("firstName").toString());
                            } else {
                                Log.d(TAG, "No such document");
                            }

                        } else {
                            Log.w(TAG, "get failed with ", task.getException());
                        }
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
        msg.put("msgId", message.messageID);
        msg.put("userID", message.userID);

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
     * Gets all the messages for the corresponding group (see param) and update the list of messages
     * Currently only gets the message, but has the capability to get other data based on the
     * ChatMessage class and its properties.
     *
     * @param groupNumber The group from which we grab messages
     */
    public void getMessages(int groupNumber) {
        groupCollection.document("Rd9DOKVw33lCtfzSnvjV") // TODO Update this so the document path is equal to the group number
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            // Ignore the warning here
                            ArrayList<HashMap<String, String>> incomingMessages = (ArrayList<HashMap<String, String>>) snapshot.getData().get("messages");

                            if (incomingMessages != null) {

                                for (int i = 0; i < incomingMessages.size() - 1; i++) {

                                    ChatMessage incomingMessage = new ChatMessage(
                                            incomingMessages.get(i).get("message"),
                                            incomingMessages.get(i).get("sender"),
                                            1,
                                            incomingMessages.get(i).get("msgId"),
                                            incomingMessages.get(i).get("userID"));

                                    if (!ChatFragment.chatMessages.contains(incomingMessage)) {
                                        Log.d(TAG, "New message detected and being added to message array");
//
                                        ChatFragment.chatMessages.add(incomingMessage);
                                        ChatFragment.externallyCallDatasetChanged();
                                    }
                                }

                                Log.d(TAG, "New Message Query Complete");
                            } else {
                                Log.d(TAG, "Skipped new message query. Existing data is up to date.");
                            }


                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });
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
    public static Uri getImageUrl() {
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
    public static void setImageUrl(Uri imageUrl) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && user.isAnonymous()) {
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

    /**
     * Get the current user's UID. This helps reduce clutter.
     * We no longer have to run the first line in this function.
     *
     * @return - [STRING] Current user's UID
     */
    public static String getUID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user.getUid();
    }
}
