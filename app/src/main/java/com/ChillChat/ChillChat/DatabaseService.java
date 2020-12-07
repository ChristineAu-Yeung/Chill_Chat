package com.ChillChat.ChillChat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;


public class DatabaseService {
    private static final String TAG = "DatabaseService";
    //Temp Images for user and anonymous
    private static final String defaultImage = "https://static.thenounproject.com/png/3246632-200.png";
    private static final String userImage = "https://i.pinimg.com/originals/0c/3b/3a/0c3b3adb1a7530892e55ef36d3be6cb8.png";

    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Shared prefs file name
    protected static final String FILE_NAME = "CurrentUser";

    // This is a reference to all of our different collections. This way we don't have to type a
    // lot of code to access the same collection over and over again
    final CollectionReference userCollection = db.collection("users");
    final CollectionReference groupCollection = db.collection("groups");
    // Make a blank age
    // Make a blank bio
    // Make a default profile pic

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
        user.put("age", 0);
        user.put("biography", "");
        user.put("profileImage", "");

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

    public void getProfileData(final String userID, final FragmentActivity result) {
        DatabaseService db = new DatabaseService();

        // Create a reference to the cities collection
        CollectionReference userRef = db.userCollection;
        DocumentReference reference = userRef.document(userID);

        reference.get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                Map profileData = document.getData();
                                Collection data = profileData.values();
                                User user = new User(document.getDate("dateRegistered"), (String) document.get("firstName"),
                                        (long) document.get("age"), (String) document.get("biography"), document.getString("profileImage"));

                                EditText name = result.findViewById(R.id.nameEditText);
                                EditText register = result.findViewById(R.id.registeredEditText);
                                EditText age = result.findViewById(R.id.ageEditText);
                                EditText bio = result.findViewById(R.id.bioEditText);

                                name.setText(user.getFirstName());
                                register.setText(user.getDateRegistered().toString());
                                age.setText(String.valueOf(user.getAge()));
                                bio.setText(user.getBio());

                                //User's Profile Picture
                                ImageView profilePic = result.findViewById(R.id.profilePictureImageButton);
                                if ("Anonymous".equals(user.getFirstName())) {
                                    Picasso.get().load(defaultImage).into(profilePic);
                                } else {
                                    Bitmap bmpImage = user.getProfileImage();
                                    if (bmpImage != null) {
                                        profilePic.setImageBitmap(bmpImage);
                                    } else {
                                        Picasso.get().load(userImage).into(profilePic);
                                    }
                                }

                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.w(TAG, "get failed with ", task.getException());
                        }
                    }
                });

    }

    public void setProfileData(String firstName, long age, String biography, String profileImage) {
        Map<String, Object> user = new HashMap<>();
        user.put("firstName", firstName);
        user.put("dateRegistered", FieldValue.serverTimestamp());
        user.put("age", age);
        user.put("biography", biography);
        user.put("profileImage", profileImage);

        // Add the user to the User Collection
        userCollection.document(getUID()).update(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User's profile has been set!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing to user's profile", e);
                    }
                });
    }

    /**
     * Function to get the current group number from shared preferences
     *
     * @param context The context bro
     * @return The integer representing the current group number
     */
    public static int getGroupNumber(final Context context) {
        SharedPreferences prefs = context.getSharedPreferences(FILE_NAME, MODE_PRIVATE);

        return prefs.getInt("groupNumber", 0);
    }

    //Function to Delete User data from Database
    void deleteUserData(String uid) {
        userCollection.document(uid).delete();
        Log.i(TAG, "User Deleted");
    }


    /**
     * Deletes Anonymous Users data on Logout
     * No parameter required since currentUser is fetched and deleteUserData is called
     */
    static void deleteAnonymousUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseService db = new DatabaseService();

        //Function that gets userID already
        if (user.isAnonymous()) {
            user.delete();
            db.deleteUserData(getUID());
        }
    }

    /**
     * Update the user's document as well as profile data
     *
     * @param firstName User's first name
     * @param email     User's email address
     */
    static void updateUserData(String email, String firstName) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseService db = new DatabaseService();

        if (user != null) {
            db.setUserData(getUID(), email, firstName);

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
            Log.w(TAG, "The user is null");
        }
    }

    /**
     * This function fetches the content of message from ChatMessage and parse it to a HashMap
     * <p>
     * message
     * firstName
     * messageID
     * userID
     *
     * @param message
     */
    public Map<String, Object> getMessageContent(ChatMessage message) {

        Map<String, Object> msg = new HashMap<>();
        msg.put("message", message.message);
        msg.put("sender", message.firstName);
        msg.put("msgId", message.messageID);
        msg.put("userID", message.userID);

        return msg;
    }

    /**
     * This function will perform the actual sending of the Messages
     * Once the callback function has completed and fetched the document
     *
     * @param msg, documentUID
     */
    public static void sendMessage(Map<String, Object> msg, String documentUID) {
        DatabaseService db = new DatabaseService();
        db.groupCollection
                .document(documentUID)
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
     * This helper function will help the sendMessage function by first loading the
     * documents and then calling sendMessage after retrieving the entire array
     * This is done to avoid any aSynchronous issues
     *
     * @param message message The group from which we grab messages
     */
    public void sendMessageHelper(final ChatMessage message) {

        final Map<String, Object> msgMap = getMessageContent(message);
        DatabaseService db = new DatabaseService();
        final ArrayList<String> documentID = new ArrayList<String>();

        db.groupCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        documentID.add(document.getId());
                    }
                    sendMessage(msgMap, documentID.get(message.groupNumber));

                } else {
                    Log.i(TAG, "Unsuccessful");
                }
            }
        });
    }

    /**
     * Gets all the messages for the corresponding group (see param) and update the list of messages
     * Currently only gets the message, but has the capability to get other data based on the
     * ChatMessage class and its properties.
     * <p>
     * **UPDATE** The document is now fetched from the helper function getMessageHelper
     * Due to an aSynchronous problem, the helper will fetch the groupDocument first
     * to avoid a potential thread block
     *
     * @param groupDocumentString The group from which we grab messages
     */
    public void getMessages(String groupDocumentString, final Context context) {
        groupCollection.document(groupDocumentString)
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

                                for (int i = 0; i < incomingMessages.size(); i++) {

                                    ChatMessage incomingMessage = new ChatMessage(
                                            incomingMessages.get(i).get("message"),
                                            incomingMessages.get(i).get("sender"),

                                            getGroupNumber(context), //TODO this is hardcoded groupNumber
                                            incomingMessages.get(i).get("msgId"),
                                            incomingMessages.get(i).get("userID"));

                                    if (!ChatFragment.chatMessages.contains(incomingMessage)) {
                                        Log.d(TAG, "New message detected and being added to message array");
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
     * Instead of the HARDCODED document, this helper function for gettingMessages will FETCH
     * The specific document index of the Array and return
     * The document corresponding to the groupNumber
     *
     * @param groupNumber Specifies which group to fetch
     */
    public void getMessageHelper(final int groupNumber, final Context context) {

        DatabaseService db = new DatabaseService();
        final ArrayList<String> documentID = new ArrayList<>();

        db.groupCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        documentID.add(document.getId());
                    }
                    getMessages(documentID.get(groupNumber), context);

                } else {
                    Log.i(TAG, "Unsuccessful");
                }
            }
        });
    }

    /**
     * This function randomizes the group that the user is in.
     * @param context
     */
    public static void randomizeGroup(final Context context){
        DatabaseService db = new DatabaseService();
        final ArrayList<String> documentID = new ArrayList<>();

        db.groupCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        documentID.add(document.getId());
                    }

                    int storedGroupNumber = getGroupNumber(context);

                    Random rand = new Random();
                    int random_integer = rand.nextInt(documentID.size());

                    while(random_integer == storedGroupNumber){
                        random_integer = rand.nextInt(documentID.size());
                    }

                    SharedPreferences prefs = context.getSharedPreferences(FILE_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor edit = prefs.edit();
                    //Edit the group number to be the new group number
                    edit.putInt("groupNumber", random_integer); // Hardcoded for newcomers
                    edit.apply();

                    Log.i(TAG, "Successfully randomized group number");

                } else {
                    Log.i(TAG, "Unsuccessful");
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
     * This function will set the information for a specific message about the provided user
     *
     * @return - void
     */
    public static void getUserData(final String userID, final View result, final ImageView userPic) {
        DatabaseService db = new DatabaseService();

        // Create a reference to the cities collection
        CollectionReference userRef = db.userCollection;
        DocumentReference docRef = userRef.document(userID);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map userData = document.getData();
                        Collection data = userData.values();
                        User user;
                        user = new User(document.getDate("dateRegistered"), (String) document.get("email"), (String) document.get("firstName"), userID);

                        //Check if the user is Anonymous and send default image
                        if ("Anonymous".equals(user.getFirstName())) {
                            Picasso.get().load(defaultImage).into(userPic);
                        } else {
                            //ToDo - Get the user image from the database once this is possible
                            //Temp - Until userImage is added to UserTable
                            Picasso.get().load(userImage).into(userPic);
                        }
                        //Set the user name under message
                        TextView displayName = result.findViewById(R.id.user_name);
                        displayName.setText(user.getFirstName());
                    } else {
                        Log.d(TAG, "No such document");
                        Picasso.get().load(defaultImage).into(userPic);
                        //Set the user name under message
                        TextView displayName = result.findViewById(R.id.user_name);
                        displayName.setText("Anonymous");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    Picasso.get().load(defaultImage).into(userPic);
                    //Set the user name under message
                    TextView displayName = result.findViewById(R.id.user_name);
                    displayName.setText("Anonymous");
                }
            }
        });
    }

    /**
     * Gets the user's photo url (AKA profile pic).
     *
     * @return URI that directs to the user's stored image URL in firebase.
     */
    public static Uri getImageUrl() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && user.isAnonymous()) {
            return Uri.parse(defaultImage);
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
            Log.w(TAG, "The user is null");
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
