package com.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.apex.sdk.db.gps.GpsData;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "GPS_DATA".
*/
public class GpsDataDao extends AbstractDao<GpsData, Long> {

    public static final String TABLENAME = "GPS_DATA";

    /**
     * Properties of entity GpsData.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Latitude = new Property(0, double.class, "latitude", false, "LATITUDE");
        public final static Property Longitude = new Property(1, double.class, "longitude", false, "LONGITUDE");
        public final static Property CurrentTime = new Property(2, long.class, "currentTime", true, "_id");
    }


    public GpsDataDao(DaoConfig config) {
        super(config);
    }
    
    public GpsDataDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"GPS_DATA\" (" + //
                "\"LATITUDE\" REAL NOT NULL ," + // 0: latitude
                "\"LONGITUDE\" REAL NOT NULL ," + // 1: longitude
                "\"_id\" INTEGER PRIMARY KEY NOT NULL );"); // 2: currentTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"GPS_DATA\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, GpsData entity) {
        stmt.clearBindings();
        stmt.bindDouble(1, entity.getLatitude());
        stmt.bindDouble(2, entity.getLongitude());
        stmt.bindLong(3, entity.getCurrentTime());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, GpsData entity) {
        stmt.clearBindings();
        stmt.bindDouble(1, entity.getLatitude());
        stmt.bindDouble(2, entity.getLongitude());
        stmt.bindLong(3, entity.getCurrentTime());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 2);
    }    

    @Override
    public GpsData readEntity(Cursor cursor, int offset) {
        GpsData entity = new GpsData( //
            cursor.getDouble(offset + 0), // latitude
            cursor.getDouble(offset + 1), // longitude
            cursor.getLong(offset + 2) // currentTime
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, GpsData entity, int offset) {
        entity.setLatitude(cursor.getDouble(offset + 0));
        entity.setLongitude(cursor.getDouble(offset + 1));
        entity.setCurrentTime(cursor.getLong(offset + 2));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(GpsData entity, long rowId) {
        entity.setCurrentTime(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(GpsData entity) {
        if(entity != null) {
            return entity.getCurrentTime();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(GpsData entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
