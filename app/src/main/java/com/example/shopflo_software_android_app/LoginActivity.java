/**
 * Login Activity for Android Mobile Application
 * Connecting to Auth0 for User Authentication
 * @author Bjorn Melin, Luciano Balmaceda , Hernan Zalazar
 * Majority of code is from Auth0 github repos.
 *
 * Link to repo - https://github.com/auth0-samples/auth0-android-sample/blob
 * /master/03-Session-Handling/app/src/main/java/com/auth0/samples/LoginActivity.java
 */

package com.example.shopflo_software_android_app;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.auth0.android.Auth0;
import com.auth0.android.Auth0Exception;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.authentication.storage.CredentialsManagerException;
import com.auth0.android.authentication.storage.SecureCredentialsManager;
import com.auth0.android.authentication.storage.SharedPreferencesStorage;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.VoidCallback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;


public class LoginActivity extends AppCompatActivity {

    /**
     * Declare CredentialsManager Variables
     */
    private Auth0 auth0;
    private SecureCredentialsManager credentialsManager;

    /*
     * Required when setting up Local Authentication in the Credential Manager
     * Refer to SecureCredentialsManager#requireAuthentication method for more information.
     */
    public static final String EXTRA_CLEAR_CREDENTIALS = "com.auth0.CLEAR_CREDENTIALS";
    public static final String EXTRA_ACCESS_TOKEN = "com.auth0.ACCESS_TOKEN";
    public static final String EXTRA_ID_TOKEN = "com.auth0.ID_TOKEN";


    /**
     * Method which contains all functions which occur upon creation of the login activity page
     * @param savedInstanceState Bundle - the state (view) of the activity.  If app was closed before
     *                           fully logging off and closing the app, it will show the saved screen
     *                           as it was left by the user
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setup the UI
        setContentView(R.layout.activity_login);
        Button loginButton = findViewById(R.id.loginButton);

        /**
         * This method makes it so that when you click the login button,
         * it will log the user in and redirect a user to the MainActivity home page
         */
        loginButton.setOnClickListener(new View.OnClickListener() {
            /**
             * When the login button is clicked by a user, the doLogin method is called
             * and the user is logged in
             * @param v View - denotes the view clicked by the user
             */
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });

        //Setup CredentialsManager
        auth0 = new Auth0(this);
        auth0.setOIDCConformant(true);
        credentialsManager = new SecureCredentialsManager(this, new AuthenticationAPIClient(auth0), new SharedPreferencesStorage(this));

        //Optional - Uncomment the next line to use:
        //Require device authentication before obtaining the credentials
        //credentialsManager.requireAuthentication(this, CODE_DEVICE_AUTHENTICATION, getString(R.string.request_credentials_title), null);

        /**
         * Check if the activity was launched to log the user out
         */
        if (getIntent().getBooleanExtra(EXTRA_CLEAR_CREDENTIALS, false)) {
            doLogout();
            return;
        }

        /**
         * If credentials for the user are valid, then call method to
         * show the home activity of the app
         */
        if (credentialsManager.hasValidCredentials()) {
            // Obtain the existing credentials and move to the next activity
            showNextActivity();
        }
    }

    /**
     * Override required when setting up Local Authentication in the Credential Manager
     * Refer to SecureCredentialsManager#requireAuthentication method for more information.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (credentialsManager.checkAuthenticationResult(requestCode, resultCode)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Method used to show the Home activity screen upon a successful login or
     * show an error message if login was not successful
     */
    private void showNextActivity() {
        credentialsManager.getCredentials(new BaseCallback<Credentials, CredentialsManagerException>() {
            /**
             * Method which sends the access token and id token stored in the credentials manager
             * to the home screen upon a successful login.  If credentials are valid, it will show
             * the home screen activity.
             * @param credentials Credentials - Auth0 credentials information (id_token, refresh_token, etc).
             *                    User credentials stored in credentials manager
             */
            @Override
            public void onSuccess(final Credentials credentials) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra(EXTRA_ACCESS_TOKEN, credentials.getAccessToken());
                intent.putExtra(EXTRA_ID_TOKEN, credentials.getIdToken());
                startActivity(intent);
                finish();
            }

            /**
             * Method which exits the app if a user was not authenticated
             * @param error CredentialsManagerException - denotes a user was not authenticated
             */
            @Override
            public void onFailure(CredentialsManagerException error) {
                //Authentication cancelled by the user. Exit the app
                finish();
            }
        });
    }


    /**
     * Method which logs a user in.  Calls the loginCallback method below, refer to that method
     * for explanation of functions within this method
     */
    private void doLogin() {
        WebAuthProvider.login(auth0)
                .withScheme("demo")
                .withAudience(String.format("https://%s/userinfo", getString(R.string.com_auth0_domain)))
                .withScope("openid offline_access")
                .start(this, loginCallback);
    }

    /**
     * Method which logs a user out.  Calls the logoutCallback method below, refer to that method
     * for explanation of functions within this method
     */
    private void doLogout() {
        WebAuthProvider.logout(auth0)
                .withScheme("demo")
                .start(this, logoutCallback);
    }


    /**
     * Callback called on success/failure of an Identity Provider authentication.
     * Only one of the success or failure methods will be called for a single authentication request.
     */
    private final AuthCallback loginCallback = new AuthCallback() {
        /**
         * Called when the failure reason is displayed in a {@link android.app.Dialog}.
         * @param dialog Dialog - error dialog
         */
        @Override
        public void onFailure(@NonNull final Dialog dialog) {
            runOnUiThread(new Runnable() {
                /**
                 * On authentication failure, show error dialog
                 */
                @Override
                public void run() {
                    dialog.show();
                }
            });
        }

        /**
         * Called with an AuthenticationException that describes the error.
         * @param exception AuthenticationException - denotes the cause of the error
         */
        @Override
        public void onFailure(final AuthenticationException exception) {
            runOnUiThread(new Runnable() {
                /**
                 * On authentication error, print out a message denoting an error occurred
                 */
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, "Log In - Error Occurred", Toast.LENGTH_SHORT).show();
                }
            });
        }

        /**
         * Called when the authentication is successful using web authentication against Auth0
         * @param credentials Credentials - Auth0 credentials information (id_token, refresh_token, etc).
         *                    User credentials stored in credentials manager
         */
        @Override
        public void onSuccess(@NonNull Credentials credentials) {
            credentialsManager.saveCredentials(credentials);
            showNextActivity();
        }
    };


    /**
     * Generic callback called on success/failure of a user attempting to logout,
     * receives no payload when succeeds.
     */
    private VoidCallback logoutCallback = new VoidCallback() {
        /**
         * Upon success, clear the users credentials to logout.
         * @param payload Void - receives no payload when succeeds
         */
        @Override
        public void onSuccess(Void payload) {
            credentialsManager.clearCredentials();
        }

        /**
         * If the logout is not successful, is canceled, then show home activity page
         * @param error Auth0Exception - Represents an error captured when
         *              executing an http request to the Auth0 Server.
         */
        @Override
        public void onFailure(Auth0Exception error) {
            //Log out canceled, keep the user logged in
            showNextActivity();
        }
    };
}
