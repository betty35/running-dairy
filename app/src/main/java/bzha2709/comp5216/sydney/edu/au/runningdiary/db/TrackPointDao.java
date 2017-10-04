package bzha2709.comp5216.sydney.edu.au.runningdiary.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import bzha2709.comp5216.sydney.edu.au.runningdiary.POJO.TrackPoint;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TRACK_POINT".
*/
public class TrackPointDao extends AbstractDao<TrackPoint, Void> {

    public static final String TABLENAME = "TRACK_POINT";

    /**
     * Properties of entity TrackPoint.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Time = new Property(0, java.util.Date.class, "time", false, "TIME");
        public final static Property Lat = new Property(1, double.class, "lat", false, "LAT");
        public final static Property Lng = new Property(2, double.class, "lng", false, "LNG");
        public final static Property Alt = new Property(3, double.class, "alt", false, "ALT");
        public final static Property Speed = new Property(4, float.class, "speed", false, "SPEED");
    }


    public TrackPointDao(DaoConfig config) {
        super(config);
    }
    
    public TrackPointDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TRACK_POINT\" (" + //
                "\"TIME\" INTEGER," + // 0: time
                "\"LAT\" REAL NOT NULL ," + // 1: lat
                "\"LNG\" REAL NOT NULL ," + // 2: lng
                "\"ALT\" REAL NOT NULL ," + // 3: alt
                "\"SPEED\" REAL NOT NULL );"); // 4: speed
        // Add Indexes
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_TRACK_POINT_TIME ON \"TRACK_POINT\"" +
                " (\"TIME\" ASC);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TRACK_POINT\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, TrackPoint entity) {
        stmt.clearBindings();
 
        java.util.Date time = entity.getTime();
        if (time != null) {
            stmt.bindLong(1, time.getTime());
        }
        stmt.bindDouble(2, entity.getLat());
        stmt.bindDouble(3, entity.getLng());
        stmt.bindDouble(4, entity.getAlt());
        stmt.bindDouble(5, entity.getSpeed());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, TrackPoint entity) {
        stmt.clearBindings();
 
        java.util.Date time = entity.getTime();
        if (time != null) {
            stmt.bindLong(1, time.getTime());
        }
        stmt.bindDouble(2, entity.getLat());
        stmt.bindDouble(3, entity.getLng());
        stmt.bindDouble(4, entity.getAlt());
        stmt.bindDouble(5, entity.getSpeed());
    }

    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    @Override
    public TrackPoint readEntity(Cursor cursor, int offset) {
        TrackPoint entity = new TrackPoint( //
            cursor.isNull(offset + 0) ? null : new java.util.Date(cursor.getLong(offset + 0)), // time
            cursor.getDouble(offset + 1), // lat
            cursor.getDouble(offset + 2), // lng
            cursor.getDouble(offset + 3), // alt
            cursor.getFloat(offset + 4) // speed
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, TrackPoint entity, int offset) {
        entity.setTime(cursor.isNull(offset + 0) ? null : new java.util.Date(cursor.getLong(offset + 0)));
        entity.setLat(cursor.getDouble(offset + 1));
        entity.setLng(cursor.getDouble(offset + 2));
        entity.setAlt(cursor.getDouble(offset + 3));
        entity.setSpeed(cursor.getFloat(offset + 4));
     }
    
    @Override
    protected final Void updateKeyAfterInsert(TrackPoint entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    @Override
    public Void getKey(TrackPoint entity) {
        return null;
    }

    @Override
    public boolean hasKey(TrackPoint entity) {
        // TODO
        return false;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
