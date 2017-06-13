package sknyazev.valeo;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import Data.NoteContract;
import Data.NotesDbHelper;
import sknyazev.valeo.UI.adapters.MyRecyclerViewAdapter;

public class MainActivity extends AppCompatActivity {

    protected MyRecyclerViewAdapter mAdapter;
    private static final String LOG_TAG = "Main_activity_i";
    private NotesDbHelper mDbHelper;
    private ArrayList<DataObject> mdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNote();
            }
        });
        initItemList();
    }


    private  void initItemList()
    {
        //get items array
        mDbHelper = new NotesDbHelper(this);
        mdata = getMdata();

        //init adapter
        mAdapter = new MyRecyclerViewAdapter(mdata);

        //Items view
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);

        TextView emptyText = (TextView)findViewById(R.id.empty_text);
        if(mAdapter.getItemCount() == 0)
        {
            emptyText.setVisibility(View.VISIBLE);
        }
        else
        {
            emptyText.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Long click open context menu
        int id = item.getItemId();
        SharedPreferences sp = getSharedPreferences("valeo", MODE_PRIVATE);
        String order = null;
        SharedPreferences.Editor ed  = sp.edit();;

        switch (id) {
            case R.id.action_search:
                Toast.makeText(this, "search", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menuDeleteAllNotes:
                removeAllNotes();
                initItemList();
                Toast.makeText(this, "menuDeleteAllNotes", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menuSortByName:
                ed.putString("orderBy", "name");
                order = sp.getString("orderByName", "ASC");
                if(order == "ASC")
                {
                    ed.putString("orderByName", "DESC");
                }
                else
                {
                    ed.putString("orderByName", "ASC");
                }
                ed.commit();
                initItemList();
                Toast.makeText(this, "menuSortByName", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menuSortByDate:
                ed.putString("orderBy", "date");
                order = sp.getString("orderByDate", "ASC");
                if(order == "ASC")
                {
                    ed.putString("orderByDate", "DESC");
                }
                else
                {
                    ed.putString("orderByDate", "ASC");
                }
                ed.commit();
                initItemList();
                Toast.makeText(this, "menuSortByDate", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.menuExit:
                android.os.Process.killProcess(android.os.Process.myPid());
                //Toast.makeText(this, "Exit", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        //return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {

        super.onResume();
        mAdapter.setOnItemClickListener(new
            MyRecyclerViewAdapter.MyClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    long id = mAdapter.getId(position);
                    openNote(id);
                }
                @Override
                public void onItemLongClick(int position, View v) {
                    long id = mAdapter.getId(position);
                    showNoteContextDialog(id, position);
                }
            });
        mdata = getMdata();
        mAdapter.updateData(mdata);
    }

    private void removeAllNotes(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        for(int i = 0; i < mdata.size(); i++) {
            DataObject dataObject = mdata.get(i);
            db.delete(NoteContract.NotesEntry.TABLE_NAME, NoteContract.NotesEntry._ID  + "=" + String.valueOf(dataObject.getId()) , null);
        }

    }

    private void openNote(long id)
    {
        Intent intent = new Intent(MainActivity.this, NoteActivity.class);

        // указываем первым параметром ключ, а второе значение
        // по ключу мы будем получать значение с Intent
        intent.putExtra("id", id);
        // показываем новое Activity
        startActivity(intent);
    }

    private void showNoteContextDialog(final long id, final int position){
        new MaterialDialog.Builder(this)
                .title("Что делать с заметкой")
                .items(R.array.main_note_context)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which)
                        {
                            case 0:
                                openNote(id);
                                break;
                            case 1:
                                break;
                            case 2:
                                removeNote(id, position);
                                break;
                            default:
                                break;
                        }
                    }
                })
                .show();
    }

    private ArrayList<DataObject> getMdata() {
        ArrayList results = new ArrayList<DataObject>();
        // Создадим и откроем для чтения базу данных
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Зададим условие для выборки - список столбцов
        String[] projection = {
                NoteContract.NotesEntry._ID,
                NoteContract.NotesEntry.COLUMN_DATE,
                NoteContract.NotesEntry.COLUMN_NAME,
                NoteContract.NotesEntry.COLUMN_DESCRIPTION};

        SharedPreferences sp = getSharedPreferences("valeo", MODE_PRIVATE);

        String orderBy = null;
        String myOrder = sp.getString("orderBy", "date");
        if(myOrder == "date")
        {
            String order = sp.getString("orderByDate", "ASC");
            orderBy = NoteContract.NotesEntry.COLUMN_DATE + " " + order;
        }
        else if(myOrder == "name")
        {
            String order = sp.getString("orderByName", "ASC");
            orderBy = NoteContract.NotesEntry.COLUMN_NAME + " " + order;
        }


        // Делаем запрос
        Cursor cursor = db.query(
                NoteContract.NotesEntry.TABLE_NAME,   // таблица
                projection,            // столбцы
                null,                  // столбцы для условия WHERE
                null,                  // значения для условия WHERE
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                orderBy);                   // порядок сортировки


        try {
            // Узнаем индекс каждого столбца
            int idColumnIndex = cursor.getColumnIndex(NoteContract.NotesEntry._ID);
            int dateColumnIndex = cursor.getColumnIndex(NoteContract.NotesEntry.COLUMN_DATE);
            int nameColumnIndex = cursor.getColumnIndex(NoteContract.NotesEntry.COLUMN_NAME);
            int descriptionColumnIndex = cursor.getColumnIndex(NoteContract.NotesEntry.COLUMN_DESCRIPTION);

            // Проходим через все ряды
            int index = 0;
            while (cursor.moveToNext()) {
                // Используем индекс для получения строки или числа
                int currentID = cursor.getInt(idColumnIndex);

                Long currentDateLong = cursor.getLong(dateColumnIndex);

                String currentName = cursor.getString(nameColumnIndex);
                String currentDescription = cursor.getString(descriptionColumnIndex);
                // Выводим значения каждого столбца
                DataObject obj = new DataObject(currentID, currentDateLong, currentName, currentDescription);
                results.add(index++, obj);
            }
        } finally {
            // Всегда закрываем курсор после чтения
            cursor.close();
        }
        return results;
    }

    private void addNote() {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Создаем объект ContentValues, где имена столбцов ключи,
        // а информация о госте является значениями ключей
        ContentValues values = new ContentValues();

        String title = "Без названия";
        String description = "";

        long currentDateandTime = System.currentTimeMillis();

        values.put(NoteContract.NotesEntry.COLUMN_DATE, currentDateandTime);
        values.put(NoteContract.NotesEntry.COLUMN_NAME, title);
        values.put(NoteContract.NotesEntry.COLUMN_DESCRIPTION, description);

        long newNoteId = db.insert(NoteContract.NotesEntry.TABLE_NAME, null, values);
        openNote(newNoteId);
    }

    private void removeNote(final long  id, final int position)
    {
        MaterialDialog mNoteDeleteDialog = new MaterialDialog.Builder(this)
                .title("Удаление заметки")
                .content("Вы действительно хотите удалить заметку")
                .positiveText("Да")
                .negativeText("Нет")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        SQLiteDatabase db = mDbHelper.getWritableDatabase();
                        db.delete(NoteContract.NotesEntry.TABLE_NAME, NoteContract.NotesEntry._ID  + "=" + String.valueOf(id) , null);
                        mdata.remove(position);
                        mAdapter.updateData(mdata);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            // TODO
                        }
                    })
                 .cancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                        }
                    })
                .show();
    }

}