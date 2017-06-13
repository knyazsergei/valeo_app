package Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Sergey on 01.06.2017.
 */

public class GalleryDbHelper  extends SQLiteOpenHelper {
    public static final String LOG_TAG = GalleryDbHelper.class.getSimpleName();

    /**
     * Имя файла базы данных
     */
    private static final String DATABASE_NAME =  "gallery.db";

    /**
     * Версия базы данных. При изменении схемы увеличить на единицу
     */
    private static final int DATABASE_VERSION = 2;

    /**
     * Конструктор {@link GalleryDbHelper}.
     *
     * @param context Контекст приложения
     */
    public GalleryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Вызывается при создании базы данных
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Строка для создания таблицы
        String SQL_CREATE_GUESTS_TABLE = "CREATE TABLE " + GalleryContract.GalleryEntry.TABLE_NAME + " ("
                + GalleryContract.GalleryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + GalleryContract.GalleryEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + GalleryContract.GalleryEntry.COLUMN_NOTE_ID + " INTEGER NOT NULL); ";

        // Запускаем создание таблицы
        db.execSQL(SQL_CREATE_GUESTS_TABLE);
    }

    /**
     * Вызывается при обновлении схемы базы данных
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Запишем в журнал
        Log.w("SQLite", "Обновляемся с версии " + oldVersion + " на версию " + newVersion);

        // Удаляем старую таблицу и создаём новую
        db.execSQL("DROP TABLE IF EXISTS " + GalleryContract.GalleryEntry.TABLE_NAME);
        // Создаём новую таблицу
        onCreate(db);
    }
}
