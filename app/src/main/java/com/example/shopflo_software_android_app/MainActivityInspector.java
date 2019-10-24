/**
 * Inspector User View Activity for Android Mobile Application
 * @author Bjorn Melin
 */

package com.example.shopflo_software_android_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;



public class MainActivityInspector extends AppCompatActivity{

    /**
     * Initialize user authentication information which was stored in LoginActivity
     */
    public static final String EXTRA_CLEAR_CREDENTIALS = "com.auth0.CLEAR_CREDENTIALS";
    public static final String EXTRA_ACCESS_TOKEN = "com.auth0.ACCESS_TOKEN";
    public static final String EXTRA_ID_TOKEN = "com.auth0.ID_TOKEN";


    /**
     * Method which contains all functions which occur upon creation of the Inspector activity page
     * @param savedInstanceState Bundle - the state (view) of the activity.  If app was closed before
     *                           fully logging off and closing the app, it will show the saved screen
     *                           as it was left by the user
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the UI
        setContentView(R.layout.activity_main_inspector);


        TextView credentialsView = findViewById(R.id.credentials);
        //Obtain the token from the Intent's extras
        final String accessToken = getIntent().getStringExtra(LoginActivity.EXTRA_ACCESS_TOKEN);
        credentialsView.setText(accessToken);
        final String idToken = getIntent().getStringExtra(LoginActivity.EXTRA_ID_TOKEN);


        /**
         * This method makes it so that when you click the logout button in the app,
         * it will clear all tokens and log a user out,
         * then redirect a user to the LoginActivity page
         */
        Button logoutButton = findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            /**
             * When the logout button is clicked by a user, the logout method is called
             * and the user is logged out
             * @param v View - denotes the view clicked by the user
             */
            @Override
            public void onClick(View v) {
                logout();
            }
        });


        /**
         * This method makes it so that when you click the Review button in the app,
         * it will redirect a user to the Review Activity page.
         */
        final Button reviewButton = findViewById(R.id.review_inspector);
        reviewButton.setOnClickListener(new View.OnClickListener() {
            /**
             * When Review button is clicked, sends the access token and id token
             * for the users session authentication to to the Review activity and launches
             * the Review activity page
             * @param v View - denotes the view clicked by the user
             */
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ReviewActivity.class);
                i.putExtra(EXTRA_ACCESS_TOKEN, accessToken);
                i.putExtra(EXTRA_ID_TOKEN, idToken);
                startActivity(i);
            }
        });
    }


    /**
     * Method which logs a user out, user is routed to the Login screen after successful logout
     * Clears the users credentials and tokens upon logout
     */
    private void logout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_CLEAR_CREDENTIALS, true);
        startActivity(intent);
        finish();
    }

}
