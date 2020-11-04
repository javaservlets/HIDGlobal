package com.example.forgerock;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

public class BadgeScanner {
    static String BADGE_ID = "";
    static FireBase firebase; // writes the card PACS ID to a message q
    static String SERVER = "";
    static String USERNAME = "";

    public static void main(String[] args) {
        try {
            if (args[0].contains("--url_firebase")) {
                SERVER = args[1];
                badgeTap(); //
            } else if (args[0].contains("--url_forgerock")) {
                SERVER = args[1];
                USERNAME = (args[2]);
                enrollUser(USERNAME);
            }

        } catch (Exception e) {
            showHelpText();
        }

    }

    // '-headless' will write to a static key called 'headless' for 'touch and go' (ie, no username is provided)

    public static void badgeTap() {
        firebase = new FireBase();
        final long timeInterval = 2000;
        Scanner console = new Scanner(System.in);
        BADGE_ID = console.next();
        try {
            Thread.sleep(timeInterval);
        } catch (InterruptedException e) {
            System.out.println("scanBadge error: " + e);
        }
        System.out.println("\n >> verifying badge " + BADGE_ID);
        firebase.update("headless", ("'" + BADGE_ID + "' ^ " + getTime())); //write to a topic named 'headless' the PACS ID and timestamp

    }

    public static void enrollUser(final String user) {
        final long timeInterval = 4000;
        Runnable runnable = new Runnable() {
            public void run() {
                while (BADGE_ID.equals("")) { //repeat until getActiveID returns a value
                    System.out.println("To enroll user {" + user + "} please tap the badge to the reader now " + BADGE_ID);
                    try {
                        Thread.sleep(timeInterval);
                    } catch (InterruptedException e) {
                        System.out.println("enroll error: " + e);
                    }
                }
                System.out.println("\n >> Enrolling " + user + " with that badge info");

                UpdateForgeRock.updateAttribute(SERVER, user, BADGE_ID);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }


    static private String getTime() { // we'll write a timestamp along with the hex value read
        Date date = null;
        String formattedDate = null;
        try {
            Timestamp stamp = new Timestamp(System.currentTimeMillis());
            date = new Date(stamp.getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            formattedDate = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    public static void showHelpText() {
        System.out.println("\nUsage: [options]\n");
        System.out.println("--url {http://a.b.c:portnumber \t\t ");
        System.out.println("--enroll {name} \t\t update {username's} profile with badge ID "); //rj added
        System.out.println("--headless \t\t authenticate via 'tap and go' (ie, no username required upfront)"); //rj added
        System.out.println("--help\t\t         print this help");
    }

    public void close() {
        System.out.println("closed...");
    }
}
