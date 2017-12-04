# InstagramLayoutTest

A layout created to test how the main screen of instagram's android app works.
Disclaimer : This project uses logos and other icons found on the internet along with the Instagram logo. No copyright violation is intended.

## Features and limitations

This is only the layout of the Instagram home page. The app has the following features -
* Login and profile - Name, Mobile number and email ID (optional)
* Camera - uses the system camera app to click a picture, saves it and displays it on the screen
* Messaging - Allows user to open any installed text messaging app.

## PhoneAuth Verification

Mobile SMS based verification using Firebase has been added with [this commit](https://github.com/yashovardhan99/InstagramLayoutTest/commit/621a5a7555948ae7811ea2e72835b675696a8a8b). As we are using firebase based authentication, note the following -
* Data and carrier charges may apply.
* Your device must be connected to the internet and the appropriate sim card inserted to receive an otp.

Firebase documentation for mobile verification can be accessed [here](https://firebase.google.com/docs/auth/android/phone-auth).

Also note that your mobile number will be sent to Firebase and they may use it as per their policies. Please visit [their website](firebase.google.com) for more.

