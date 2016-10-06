import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class datcal {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		 SimpleDateFormat sdf;
         Calendar cal;
         Date date = new Date();
         int week;
         String sample = "09/03/2013";
         sdf = new SimpleDateFormat("MM/dd/yyyy");
         try {
        	 date = sdf.parse(sample);
         } catch (ParseException e) {
        	 // TODO Auto-generated catch block
        	 e.printStackTrace();
         }
         cal = Calendar.getInstance();
         cal.setTime(date);
         week = cal.get(Calendar.WEEK_OF_YEAR);
         System.out.println(week);
	}

}
