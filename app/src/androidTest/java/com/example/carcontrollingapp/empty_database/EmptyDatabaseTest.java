package com.example.carcontrollingapp.empty_database;

import com.example.carcontrollingapp.activities.UITests;
import com.example.carcontrollingapp.room.AppDatabase;

import org.junit.Before;

import java.io.IOException;

public class EmptyDatabaseTest extends UITests {
    @Before
    public void setup() throws IOException {
        AppDatabase.getInstance(context).clearAllTables();
    }
}
