package Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Sergey on 31.05.2017.
 */

public class NotesDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = NotesDbHelper.class.getSimpleName();

    /**
     * Имя файла базы данных
     */
    private static final String DATABASE_NAME =  "notes.db";

    /**
     * Версия базы данных. При изменении схемы увеличить на единицу
     */
    private static final int DATABASE_VERSION = 3;

    /**
     * Конструктор {@link NotesDbHelper}.
     *
     * @param context Контекст приложения
     */
    public NotesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Вызывается при создании базы данных
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Строка для создания таблицы
        String SQL_CREATE_GUESTS_TABLE = "CREATE TABLE " + NoteContract.NotesEntry.TABLE_NAME + " ("
                + NoteContract.NotesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NoteContract.NotesEntry.COLUMN_DATE + " LONG NOT NULL, "
                + NoteContract.NotesEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + NoteContract.NotesEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL); ";

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
        db.execSQL("DROP TABLE IF EXISTS " + NoteContract.NotesEntry.TABLE_NAME);
        // Создаём новую таблицу
        onCreate(db);
    }
}
