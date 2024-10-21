package com.crossbowffs.nekosms.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.crossbowffs.nekosms.BuildConfig;
import com.crossbowffs.nekosms.data.SmsFilterAction;
import com.crossbowffs.nekosms.data.SmsFilterField;
import com.crossbowffs.nekosms.utils.Xlog;

import static com.crossbowffs.nekosms.provider.DatabaseContract.BlockedMessages;
import static com.crossbowffs.nekosms.provider.DatabaseContract.FilterRules;

/* package */ class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "nekosms.db";
    private static final int DATABASE_VERSION = BuildConfig.DATABASE_VERSION;

    private static final String CREATE_BLOCKED_MESSAGES_TABLE =
        "CREATE TABLE " + BlockedMessages.TABLE + "(" +
            BlockedMessages._ID                 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            BlockedMessages.SENDER              + " TEXT NOT NULL, " +
            BlockedMessages.BODY                + " TEXT NOT NULL, " +
            BlockedMessages.TIME_SENT           + " INTEGER NOT NULL, " +
            BlockedMessages.TIME_RECEIVED       + " INTEGER NOT NULL, " +
            BlockedMessages.READ                + " INTEGER NOT NULL, " +
            BlockedMessages.SEEN                + " INTEGER NOT NULL, " +
            BlockedMessages.SUB_ID              + " INTEGER NOT NULL" +
        ");";

    private static final String CREATE_FILTER_RULES_TABLE =
        "CREATE TABLE " + FilterRules.TABLE + "(" +
            FilterRules._ID                     + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            '`' + FilterRules.ACTION + '`'      + " TEXT NOT NULL, " +
            FilterRules.SENDER_MODE             + " TEXT, " +
            FilterRules.SENDER_PATTERN          + " TEXT, " +
            FilterRules.SENDER_CASE_SENSITIVE   + " INTEGER, " +
            FilterRules.BODY_MODE               + " TEXT, " +
            FilterRules.BODY_PATTERN            + " TEXT, " +
            FilterRules.BODY_CASE_SENSITIVE     + " INTEGER" +
        ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BLOCKED_MESSAGES_TABLE);
        db.execSQL(CREATE_FILTER_RULES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Xlog.i("Upgrading database from v%d to v%d", oldVersion, newVersion);

        if (oldVersion < 8) {
            upgradePre8(db);
            oldVersion = newVersion;
        }

        if (oldVersion == 8 && newVersion >= 12) {
            upgrade8To12(db);
            oldVersion = 12;
        }

        if (oldVersion == 9 && newVersion >= 10) {
            upgrade9To10(db);
            oldVersion = 10;
        }

        if (oldVersion == 10 && newVersion >= 11) {
            upgrade10To11(db);
            oldVersion = 11;
        }

        if (oldVersion == 11 && newVersion >= 12) {
            upgrade11To12(db);
            oldVersion = 12;
        }
    }

    private void upgradePre8(SQLiteDatabase db) {
        // This version was never released, so it should be fine to just
        // clear all data and start from scratch.
        db.execSQL("DROP TABLE IF EXISTS filters");
        db.execSQL("DROP TABLE IF EXISTS blocked");
        onCreate(db);
    }

    private void upgrade8To12(SQLiteDatabase db) {
        // Get data from old tables
        Cursor filtersCursor = db.query("filters", new String[] {
            "field",
            "mode",
            "pattern",
            "case_sensitive"
        }, null, null, null, null, null);
        Cursor messagesCursor = db.query("blocked", new String[] {
            "sender",
            "body",
            "time_sent",
            "time_received"
        }, null, null, null, null, null);

        // Create new tables. Since the names are different, we can
        // copy the values from the old tables to the new ones, and then
        // delete the old tables, ensuring we don't lose data if the
        // operation fails.
        onCreate(db);

        // Copy filters to new table
        if (filtersCursor != null) {
            ContentValues values = new ContentValues();
            while (filtersCursor.moveToNext()) {
                values.put(FilterRules.ACTION, SmsFilterAction.BLOCK.name());
                if (filtersCursor.getString(0).equals(SmsFilterField.SENDER.name())) {
                    values.put(FilterRules.SENDER_MODE, filtersCursor.getString(1));
                    values.put(FilterRules.SENDER_PATTERN, filtersCursor.getString(2));
                    values.put(FilterRules.SENDER_CASE_SENSITIVE, filtersCursor.getInt(3));
                    values.putNull(FilterRules.BODY_MODE);
                    values.putNull(FilterRules.BODY_PATTERN);
                    values.putNull(FilterRules.BODY_CASE_SENSITIVE);
                } else {
                    values.putNull(FilterRules.SENDER_MODE);
                    values.putNull(FilterRules.SENDER_PATTERN);
                    values.putNull(FilterRules.SENDER_CASE_SENSITIVE);
                    values.put(FilterRules.BODY_MODE, filtersCursor.getString(1));
                    values.put(FilterRules.BODY_PATTERN, filtersCursor.getString(2));
                    values.put(FilterRules.BODY_CASE_SENSITIVE, filtersCursor.getInt(3));
                }
                db.insert(FilterRules.TABLE, null, values);
            }
            filtersCursor.close();
        }

        // Copy messages to new table
        if (messagesCursor != null) {
            ContentValues values = new ContentValues();
            while (messagesCursor.moveToNext()) {
                values.put(BlockedMessages.SENDER, messagesCursor.getString(0));
                values.put(BlockedMessages.BODY, messagesCursor.getString(1));
                values.put(BlockedMessages.TIME_SENT, messagesCursor.getLong(2));
                values.put(BlockedMessages.TIME_RECEIVED, messagesCursor.getLong(3));
                values.put(BlockedMessages.READ, 1);
                values.put(BlockedMessages.SEEN, 1);
                values.put(BlockedMessages.SUB_ID, -1);
                db.insert(BlockedMessages.TABLE, null, values);
            }
            messagesCursor.close();
        }

        // Now delete the old tables
        db.execSQL("DROP TABLE IF EXISTS filters");
        db.execSQL("DROP TABLE IF EXISTS blocked");
    }

    private void upgrade9To10(SQLiteDatabase db) {
        // Add action column
        db.execSQL(
            "ALTER TABLE " + FilterRules.TABLE +
            " ADD COLUMN `" + FilterRules.ACTION + "` TEXT NOT NULL" +
            " DEFAULT '" + SmsFilterAction.BLOCK.name() + "'");
    }

    private void upgrade10To11(SQLiteDatabase db) {
        // Add sub_id column
        db.execSQL(
            "ALTER TABLE " + BlockedMessages.TABLE +
            " ADD COLUMN " + BlockedMessages.SUB_ID + " INTEGER NOT NULL" +
            " DEFAULT 0");
    }

    private void upgrade11To12(SQLiteDatabase db) {
        // Change unknown sub_id value to -1 (unfortunately, we can't tell the
        // difference between 0 and unknown, so we have to clobber some data)
        // See https://github.com/apsun/NekoSMS/pull/100 for context
        db.execSQL(
            "UPDATE " + BlockedMessages.TABLE +
            " SET " + BlockedMessages.SUB_ID + " = -1" +
            " WHERE " + BlockedMessages.SUB_ID + " = 0");
    }
}
