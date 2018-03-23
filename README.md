# MessApp
App for students and contractors

The Android App has 2 main sections that a developer has to work on. 

One contains the UI elements and resources that are mainly responsible for how the UI looks like (in app/src/main/res).
activity_login.xml is the UI layout that has been designed for the login page.
content_contractor_main.xml is the layout desinged for the Contractor's main page.

app/src/main/java/com/example/satish/messapp contains 2 java files which represent the 2 activities used till now.
These activities are what really make the app work and handles the real functionality.

LoginActivity is the activity to handle user Logins.
ContractorMain is the activity to handle Contractor Actions.

The first activity called by the app is LoginActivity.
In Login Activity , if a user successfully signs in as a contractor, then it identifies the user, then sets up ContractorActivity and finally intiates Contractor Activity and finiches itself.
ContractorActivity handles all the activities that a contractor is allowed to do.
