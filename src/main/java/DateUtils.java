import java.text.ParseException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public  static Calendar getDateFromString(String dateString,String dateFormat) throws ParseException, ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        simpleDateFormat.setLenient(false);
        Date date = simpleDateFormat.parse(dateString);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;

    }


    public static String formatDate(String inputDate,String inputDateFormat,String outputDateFormat) throws ParseException {
        return  formatDate(getDateFromString(inputDate,inputDateFormat),outputDateFormat);
    }
    public static String formatDate(Calendar cal,String outputDateFormat){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(outputDateFormat);
        return simpleDateFormat.format(cal.getTime());
    }

    public static String getCurrentDate(String dateFormat){
        Calendar cal = Calendar.getInstance();
        return formatDate(cal,dateFormat);
    }



}
