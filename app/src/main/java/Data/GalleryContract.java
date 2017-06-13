package Data;

import android.provider.BaseColumns;

/**
 * Created by Sergey on 01.06.2017.
 */

public final class GalleryContract {
    private GalleryContract() {
    };

    public static final class GalleryEntry implements BaseColumns {
        public final static String TABLE_NAME = "Gallery";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_NAME = "name";
        public final static String COLUMN_NOTE_ID = "noteId";
    }
}
