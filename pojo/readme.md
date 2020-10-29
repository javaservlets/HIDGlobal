
This is a simple stand-alone java class for the ForgeRock integration with the HID Global smart badge reader; the first part updates a named user's record with the value of his/her HID Global smart card; the second part uses said value as part of the Authentication Tree described at https://github.com/javaservlets/HIDglobal

Assumptions:

* Java SDK 1.8 or greater and
* an instance of ForgeRock AM 7+
* You have an existing Firebase Real-Time Database pre-configured*, and your real account.json values have replaced the contents in the sample one provided in this project
* If running on Windows, folder delimiters in .sh files below must be changed from ":" to ";"


To run this standalone java class:

1. Use your IDE or text editor of choice and update line 35 of /src/com/example/forgerock/EnrollUser to reflect your ForgeRock instance address

2. Use your IDE or text editor of choice and update line 35 of /src/com/example/forgerock/BadgeTap to reflect your FireBase DB instance

3. In a terminal window run the contents of ./compile.sh

4. In a terminal window run the contents of ./enroll.sh {username}
This command will prompt you to present a badge/tap; it will read the values from it and write it to the {username} account in ForgeRock AM (the 'sunIdentityMSISDNNumber' field for that user will then be updated with the PACs value.)

5. Next configure the HID Global Auth Tree as described at https://github.com/javaservlets/HIDGlobal

6. Lastly in a terminal window run the contents of ./headless.sh
The authentication tree will poll and wait for the output of this last step to be written to the firebase:/headless.json topic and message

Misc:
badgetap.java writes to a Firebase Realtime DB thus you are required to provide your account-services.json file
