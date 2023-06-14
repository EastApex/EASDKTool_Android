package com.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.apex.sdk.db.freq.StepFreqData;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "STEP_FREQ_DATA".
*/
public class StepFreqDataDao extends AbstractDao<StepFreqData, Long> {

    public static final String TABLENAME = "STEP_FREQ_DATA";

    /**
     * Properties of entity StepFreqData.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property CurrentTime = new Property(0, long.class, "currentTime", true, "_id");
        public final static Property StepFreq = new Property(1, int.class, "stepFreq", false, "STEP_FREQ");
    }


    public StepFreqDataDao(DaoConfig config) {
        super(config);
    }
    
    public StepFreqDataDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"STEP_FREQ_DATA\" (" + //
                "\"_id\" INTEGER PRIMARY KEY NOT NULL ," + // 0: currentTime
                "\"STEP_FREQ\" INTEGER NOT NULL );"); // 1: stepFreq
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"STEP_FREQ_DATA\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, StepFreqData entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getCurrentTime());
        stmt.bindLong(2, entity.getStepFreq());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, StepFreqData entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getCurrentTime());
        stmt.bindLong(2, entity.getStepFreq());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    @Override
    public StepFreqData readEntity(Cursor cursor, int offset) {
        StepFreqData entity = new StepFreqData( //
            cursor.getLong(offset + 0), // currentTime
            cursor.getInt(offset + 1) // stepFreq
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, StepFreqData entity, int offset) {
        entity.setCurrentTime(cursor.getLong(offset + 0));
        entity.setStepFreq(cursor.getInt(offset + 1));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(StepFreqData entity, long rowId) {
        entity.setCurrentTime(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(StepFreqData entity) {
        if(entity != null) {
            return entity.getCurrentTime();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(StepFreqData entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}