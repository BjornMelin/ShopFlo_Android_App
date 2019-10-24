# ShopFlo_Software_Android_App
 Project and resource management software.  Developed in Android Studio as an Android mobile application.


## Auth0 User Authentication
 1.  Register a free Auth0 account at **https://auth0.com/**  
 2.  Navigate to Dashboard and select **Create Application**
 3.  Choose a name for the Auth0 application and select **Native**
 4.  Navigate to settings and locate your **Domain** and **Client ID**
 5.  In Android Studio ShopFlo Application, navigate to **res > values > strings**
 6.  Locate Auth0 related strings and enter info as shown below:   
     `<string name="com_auth0_client_id">your client ID</string>`  
     `<string name="com_auth0_domain">your auth0 domain</string>`
 7.  Go back to Auth0 Application and navigate to **Settings** then find **Allowed Callback URLs** and **Allowed Logout URLs** and enter in:
    demo://your_auth0_domain/android/your_android_app_package_name/callback  
 8.  Navigate to Application build.gradle file and ensure that the following line is filled in correctly under defaultConfig:
    manifestPlaceholders = [auth0Domain: "@string/com_auth0_domain", auth0Scheme: "demo"]
 9.  Test your authenticated login with Auth0, should be working.  Also be sure to test that logout works.  If any issues are encountered, refer to Auth0 help resources available on their website.
