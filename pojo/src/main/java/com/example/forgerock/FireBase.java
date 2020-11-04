/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2018 ForgeRock AS.
 */

package com.example.forgerock;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class FireBase {
    private FirebaseDatabase firebaseDatabase;

    public FireBase(final String firebase_url) {
        try { //std firebase access
            FileInputStream serviceAccount = new FileInputStream("account-services.json");
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(firebase_url)
                    .build();
            FirebaseApp.initializeApp(options);
            firebaseDatabase = FirebaseDatabase.getInstance(firebase_url);

        } catch (IOException ioe) {
            System.out.println("+++ ERROR: do not proceed until your firebase credentials work (" + ioe.getMessage());
            return;
        }
    }

    public void update(final String key, final String value) {
        try { //std firebase update
            if (firebaseDatabase == null) {
                System.out.println("+++ ERROR: check firebase credentials please");
                return;
            }

            DatabaseReference ref = firebaseDatabase.getReference(key);
            final CountDownLatch latch = new CountDownLatch(1);

            ref.setValue(value, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        System.out.println("Data could not be written " + databaseError.getMessage());
                        latch.countDown();
                    } else {
                        System.out.println(" saved " + key + " / " + value);
                        latch.countDown();
                    }
                }
            });
            System.out.println("checking firebase credentials...");
            latch.await();
        } catch (Exception e) {
            System.out.println("+++ ERROR2: check firebase credentials" + e.getMessage());
        }
    }

    public void close() {
        System.out.println("closed...");
        firebaseDatabase.getApp().delete();
    }

}
