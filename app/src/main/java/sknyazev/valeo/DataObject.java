package sknyazev.valeo;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class DataObject {
    private String mTitle;
    private String mDescription;
    private long mDate;
    private long id;

    DataObject (long myId, long date, String title, String description){
        id = myId;
        mDate = date;

        mTitle = title;
        if(mTitle.length() > 16)
        {
            mTitle = mTitle.substring(0, 13) + "...";
        }

        mDescription = description.replaceAll("\n", " ");
        if(mDescription.length() > 50)
        {
            mDescription = mDescription.substring(0, 47) + "...";
        }
    }

    public long getId(){return  id;}

    public String getFullDate() {
        String date = new java.text.SimpleDateFormat("MM.dd.yyyy HH:mm").format(new java.util.Date (mDate));
        return date;
    }

    public String getDate() throws ParseException {
        long currentDateAndTime = System.currentTimeMillis();

        String currentDateDate = new java.text.SimpleDateFormat("MM.dd.yyyy").format(new java.util.Date (currentDateAndTime)) + " ";
        String dateDate = new java.text.SimpleDateFormat("MM.dd.yyyy").format(new java.util.Date (mDate)) + " ";

        String currentHoursAndMinuts = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date (currentDateAndTime));
        String dateHoursAndMinuts = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date (mDate));

        if(currentDateDate.equals(dateDate))
        {
            dateDate = "";
            if(currentHoursAndMinuts.equals(dateHoursAndMinuts))
            {
                String seconds = new java.text.SimpleDateFormat("ss").format(new java.util.Date (mDate));
                dateHoursAndMinuts += " " + seconds;
            }

        }
        else
        {
            dateHoursAndMinuts = "";
        }
        String date = dateDate + dateHoursAndMinuts;

        return date;
    }

    public void setDate(long date) {
        this.mDate = date;
    }

    public String getTitle() {
        return mTitle;
    }
    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }
    public void setDescription(String description) {
        this.mDescription = description;
    }

}