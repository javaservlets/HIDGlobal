package com.example.forgerock;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

public class KeyboardScanner {
    static String BADGE_ID = "";
    static BadgeTap tap; //scans for a tap and writes the hex value to a Q

    public static void main(String[] args) {
        if (args.length == 0) {
            scanBadge("headless");
        } else if (args[0].contains("--enroll")) { //rj added these two args (both require a username to be passed in as well)
            enroll(args[1]);
        } else if (args[0].contains("--mfa")) {
            scanBadge(args[1]); // name of user is passed in
        } else if (args[0].contains("--headless")) {
            scanBadge("headless");
        }
    }

    // '-headless' will write to a static key called 'headless' for 'touch and go' (ie, no username is provided)
    // this also fires when '-mfa (username)' is passed as an arg, AND 'enroll' = update a user acct when a badge is tapped

    public static void scanBadge(final String usr) {
        tap = new BadgeTap();
        final long timeInterval = 1000;
        Runnable runnable = new Runnable() {
            public void run() {
                while (BADGE_ID.equals("")) { //repeat until getActiveID returns a value
                    System.out.println("to verify please tap your badge to the reader now " + BADGE_ID);

                    Scanner console = new Scanner(System.in);
                    BADGE_ID = console.next();

                    try {
                        Thread.sleep(timeInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("\n >> verifying badge " + BADGE_ID);

                tap.update(usr, (BADGE_ID + " ^ " + getTime()));

            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public static void enroll(final String user) {
        final long timeInterval = 2000;
        Runnable runnable = new Runnable() {
            public void run() {
                while (BADGE_ID.equals("")) { //repeat until getActiveID returns a value
                    System.out.println("To enroll user {" + user + "} please tap the badge to the reader now " + BADGE_ID);

                    //badgeID = getActiveId32(lib); // their OOTB SDK call to their HW
                    try {
                        Thread.sleep(timeInterval);
                    } catch (InterruptedException e) {
                        System.out.println("er: "
                                + e);
                        e.printStackTrace();
                    }
                }
                System.out.println("\n >> Enrolling " + user + " with that badge info");
                updateFR(user, BADGE_ID); //write the attribute
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public static void updateFR(String usr, String badgeID) {
        String toke = UpdateFRuser.getToken();
        UpdateFRuser.updateAttribute(usr, badgeID, toke);
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
        System.out.println("\nUsage: scanner [options]\n");
        System.out.println("--enroll {name} \t\t update {username's} profile with badge ID "); //rj added
        System.out.println("--mfa {name} \t\t authenticate via a badge tap "); //rj added
        System.out.println("--headless \t\t authenticate via 'tap and go' (ie, no username required upfront)"); //rj added
        System.out.println("--help\t\t         print this help");
    }

    public void close() {
        System.out.println("closed...");
        //firebaseDatabase.getApp().delete();
    }
}
