package sknyazev.valeo;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import Data.GalleryContract;
import Data.GalleryDbHelper;
import Data.NoteContract;
import Data.NotesDbHelper;


public class NoteActivity extends AppCompatActivity implements
        TimePickerFragment.OnTimeChangeListenerInterface,
        DatePickerFragment.OnDateChangeListenerInterface {
    private NotesDbHelper mDbHelper;
    private long id;

    //UI
    TextView dateView;

    //date picker
    Date mDate;
    String mDateEdited;
    //gallery
    private LinearLayout mGallery;
    private int[] mImgIds;
    private ArrayList<String> mImgNames = new ArrayList<String>();
    private LayoutInflater mInflater;
    private HorizontalScrollView horizontalScrollView;
    //db helper
    private GalleryDbHelper mDbGalleryHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dateView = (TextView) findViewById(R.id.tvNoteDate);
        Date date = new Date();

        dateView.setText(DateUtils.getInstance().formatDate(date));

        id = getIntent().getLongExtra("id", 0);
        Log.i("get id", Integer.toString((int) id));
        mDbHelper = new NotesDbHelper(this);
        mDbGalleryHelper = new GalleryDbHelper(this);
        loadNote();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.cameraFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    captureCameraImage();
                } catch (ActivityNotFoundException e) {
                    // Выводим сообщение об ошибке
                    String errorMessage = "Ваше устройство не поддерживает съемку";
                    Snackbar.make(getWindow().getDecorView().getRootView(), errorMessage, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //camera


        mInflater = LayoutInflater.from(this);
        initView();


        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        //camera
    }

    private void addNotification() {
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_camera)
                        .setContentTitle("Notifications Example")
                        .setContentText("This is a test notification");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    // method from OnTimeChangeListenerInterface, this method will be called when user select time from time picker

    public void onTimeChangeListener(String time) {
        Log.i("Date: ", mDateEdited);
        Log.i("Date: ", time);
        addNotification();
        //dateView.setText(mDateEdited + " " + time);
    }

    // method from OnDateChangeListenerInterface, this method will be called when user select the date

    public void onDateChangeListener(String date) {
        mDateEdited = date;

        TimePickerFragment timePickerFragment = new TimePickerFragment();
        FragmentManager manager = getSupportFragmentManager();
        timePickerFragment.show(manager, "timePicker");
    }


    private void initView() {
        loadGallery();

        mGallery = (LinearLayout) findViewById(R.id.id_gallery);

        for (int i = 0; i < mImgNames.size(); i++) {

            File folder = getAlbumStorageDir("notes");
            File imgFile = new  File(folder, mImgNames.get(i));

            Bitmap bitmap =getBitmap(imgFile.getAbsolutePath());

            View view = mInflater.inflate(R.layout.activity_gallery_item, mGallery, false);
            ImageView img = (ImageView) view.findViewById(R.id.id_index_gallery_item_image);
            img.setImageBitmap(bitmap);
            TextView txt = (TextView) view.findViewById(R.id.id_index_gallery_item_text);
            txt.setText(mImgNames.get(i));

            mGallery.addView(view);
        }


    }


    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menuSaveNote:
                updateNote();
                Snackbar.make(getWindow().getDecorView().getRootView(), "Сохранено", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return true;
            case R.id.menuDeleteNote:
                removeNote();
                return true;
            case R.id.menuAlarm:
                makeAlarm();
                return true;
            case R.id.menuNoteInfo:
                Snackbar.make(getWindow().getDecorView().getRootView(), "Инфо", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void makeAlarm() {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void loadGallery() {
        SQLiteDatabase db = mDbGalleryHelper.getReadableDatabase();

        String query = "SELECT " + GalleryContract.GalleryEntry.COLUMN_NAME
                + " FROM " + GalleryContract.GalleryEntry.TABLE_NAME
                + " WHERE " + GalleryContract.GalleryEntry.COLUMN_NOTE_ID + " = " + id;

        Cursor cursor2 = db.rawQuery(query, null);
        if (cursor2.getCount() > 0) {
            while (cursor2.moveToNext()) {
                String fileName = cursor2.getString(cursor2.getColumnIndex(GalleryContract.GalleryEntry.COLUMN_NAME));
                mImgNames.add(fileName);
            }
        }
        cursor2.close();
    }


    private void addImage(String name) {

        SQLiteDatabase db = mDbGalleryHelper.getWritableDatabase();
        // Создаем объект ContentValues, где имена столбцов ключи,
        // а информация о госте является значениями ключей
        ContentValues values = new ContentValues();

        long noteId = id;
        values.put(GalleryContract.GalleryEntry.COLUMN_NAME, name);
        values.put(GalleryContract.GalleryEntry.COLUMN_NOTE_ID, noteId);

        db.insert(GalleryContract.GalleryEntry.TABLE_NAME, null, values);

    }

    private void loadNote() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Абстрактный пример
        // Метод 2: Сырой SQL-запрос
        String query = "SELECT " + NoteContract.NotesEntry.COLUMN_NAME + ", "
                + NoteContract.NotesEntry.COLUMN_DATE + ", "
                + NoteContract.NotesEntry.COLUMN_DESCRIPTION
                + " FROM " + NoteContract.NotesEntry.TABLE_NAME
                + " WHERE " + NoteContract.NotesEntry._ID + " = " + id;

        Cursor cursor2 = db.rawQuery(query, null);
        while (cursor2.moveToNext()) {

            String title = cursor2.getString(cursor2.getColumnIndex(NoteContract.NotesEntry.COLUMN_NAME));
            long date = cursor2.getLong(cursor2.getColumnIndex(NoteContract.NotesEntry.COLUMN_DATE));
            String description = cursor2.getString(cursor2.getColumnIndex(NoteContract.NotesEntry.COLUMN_DESCRIPTION));

            DataObject obj = new DataObject(id, date, title, description);

            EditText titleInput = (EditText) findViewById(R.id.etTitle);
            TextView dateTextView = (TextView) findViewById(R.id.tvNoteDate);
            EditText descriptionInput = (EditText) findViewById(R.id.etText);

            titleInput.setText(title);
            dateTextView.setText(obj.getFullDate());
            descriptionInput.setText(description);

        }
        cursor2.close();
    }

    private void updateNote() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        String title = ((EditText) findViewById(R.id.etTitle)).getText().toString();
        String description = ((EditText) findViewById(R.id.etText)).getText().toString();
        values.put(NoteContract.NotesEntry.COLUMN_NAME, title);
        values.put(NoteContract.NotesEntry.COLUMN_DESCRIPTION, description);
        db.update(NoteContract.NotesEntry.TABLE_NAME,
                values,
                NoteContract.NotesEntry._ID + "= ?", new String[]{String.valueOf(id)});
    }

    private void removeNote() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        db.delete(NoteContract.NotesEntry.TABLE_NAME, NoteContract.NotesEntry._ID + "=" + id, null);
        finish();
    }

    String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // do something
            }
            return;
        }
    }


    private Uri imageToUploadUri;
    private String fileName;
    public static File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            //This is also the case if the directory already existed
            Log.i("wall-splash", "Directory not created");
        }
        return file;
    }

    private void captureCameraImage() throws IOException {
        if(checkPermissions()) {
            Intent chooserIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            //create file
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            fileName = timeStamp + ".jpg";

            File folder = getAlbumStorageDir("notes");
            //create the file
            File file = new File(folder, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            String newFile = folder.getAbsolutePath() + "/" + timeStamp + ".jpg";
            File f = new File(newFile);

            chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
            imageToUploadUri = Uri.fromFile(f);
            startActivityForResult(chooserIntent, 0);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if(imageToUploadUri != null){
                Uri selectedImage = imageToUploadUri;
                getContentResolver().notifyChange(selectedImage, null);
                Bitmap reducedSizeBitmap = getBitmap(imageToUploadUri.getPath());
                if(reducedSizeBitmap != null){

                    View view = mInflater.inflate(R.layout.activity_gallery_item,mGallery, false);
                    ImageView img = (ImageView) view.findViewById(R.id.id_index_gallery_item_image);
                    img.setImageBitmap(reducedSizeBitmap);
                    TextView txt = (TextView) view.findViewById(R.id.id_index_gallery_item_text);
                    txt.setText(fileName);
                    mGallery.addView(view);

                    addImage(fileName);
                }else{
                    Toast.makeText(this,"Error while capturing Image with URI",Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this,"Error while capturing Image",Toast.LENGTH_LONG).show();
            }
        }
    }

    private Bitmap getBitmap(String path) {

        Uri uri = Uri.fromFile(new File(path));
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            in = getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();


            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d("", "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: " + o.outHeight);

            Bitmap b = null;
            in = getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();
                Log.d("", "1th scale operation dimenions - width: " + width + ", height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
                        (int) y, true);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d("", "bitmap size - width: " + b.getWidth() + ", height: " +
                    b.getHeight());
            return b;
        } catch (IOException e) {
            Log.e("", e.getMessage(), e);
            return null;
        }
    }




    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Сохраните состояние UI в переменную savedInstanceState.
        // Она будет передана в метод onCreate при закрытии и
        // повторном запуске процесса.
        updateNote();
        super.onSaveInstanceState(savedInstanceState);
    }

    // Вызывается перед выходом из "активного" состояния
    @Override
    public void onPause(){
        // "Замораживает" пользовательский интерфейс, потоки
        // или трудоемкие процессы, которые могут не обновляться,
        // пока Активность не находится на переднем плане.
        updateNote();
        super.onPause();
    }

    // Вызывается перед тем, как Активность перестает быть "видимой".
    @Override
    public void onStop(){
        // "Замораживает" пользовательский интерфейс, потоки
        // или операции, которые могут подождать, пока Активность
        // не отображается на экране. Сохраняйте все введенные
        // данные и изменения в UI так, как будто после вызова
        // этого метода процесс должен быть закрыт.
        updateNote();
        super.onStop();
    }

    // Вызывается перед выходом из "полноценного" состояния.
    @Override
    public void onDestroy(){
        // Очистите все ресурсы. Это касается завершения работы
        // потоков, закрытия соединений с базой данных и т. д
        updateNote();
        mDbHelper.close();
        super.onDestroy();
    }

}
