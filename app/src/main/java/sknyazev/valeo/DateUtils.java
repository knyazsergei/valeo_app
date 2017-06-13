package sknyazev.valeo;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Created by Sergey on 31.05.2017.
 */


public final class DateUtils {
    private DateUtils() {}

    private static class Holder {
        private static final DateUtils _instance = new DateUtils();
    }

    public static DateUtils getInstance() {
        return Holder._instance;
    }

    public String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return dateFormat.format(date);
    }
}