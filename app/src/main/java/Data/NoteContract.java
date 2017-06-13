package Data;

import android.provider.BaseColumns;

/**
 * Created by Sergey on 31.05.2017.
 */

public final class NoteContract {
    private NoteContract() {
    };

    public static final class NotesEntry implements BaseColumns {
        public final static String TABLE_NAME = "Notes";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_NAME = "Title";
        public final static String COLUMN_DESCRIPTION = "Description";
        public final static String COLUMN_DATE = "Date";
    }
}
