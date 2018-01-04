package citypark.promagreader.gui;

import java.text.*;
import java.util.Date;
import java.sql.Timestamp;


public class TimeManager {

	public static String getTimeDifference(String date1, String date2) throws ParseException
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm:ss"); 
		
		Date d1 = df.parse(date1);  
		Date d2 = df.parse(date2);  
		
		int diffInMinutes = (int)(Math.abs(d1.getTime() - d2.getTime())/ 1000);  
		Date datetime = formatter.parse(calcHMS(diffInMinutes));
		String time = formatter2.format(datetime);
		return time;
	}
	
	public static String getHourDifference(String date1, String date2) throws ParseException
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat formatter2 = new SimpleDateFormat("HH"); 
		
		Date d1 = df.parse(date1);  
		Date d2 = df.parse(date2);  
		
		int diffInMinutes = (int)(Math.abs(d1.getTime() - d2.getTime())/ 1000);  
		Date datetime = formatter.parse(calcHMS(diffInMinutes));
		String time = formatter2.format(datetime);
		return time;
		
	}
	
	public static String calcHMS(int timeInSeconds) {
	      int hours, minutes, seconds;
	      hours = timeInSeconds / 3600;
	      timeInSeconds = timeInSeconds - (hours * 3600);
	      minutes = timeInSeconds / 60;
	      timeInSeconds = timeInSeconds - (minutes * 60);
	      seconds = timeInSeconds;
	      String timediff = ""+hours + ":" + minutes +":"+seconds+"";
	      return timediff;
	   }
	
	public static void main(String[] args) throws ParseException{
		
		Date today = new java.util.Date();
		Timestamp newTimeStamp = new java.sql.Timestamp(today.getTime());
		String datetimestring = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((newTimeStamp));
		String date1 = datetimestring;
		String date2 = "2014-11-11 00:00:00"; //oldTime
		System.out.println("moi" + String.valueOf(Integer.parseInt(date2)));
		
		System.out.println(date1 + "    " + date2 + "      " + getTimeDifference(date1, date2));
	}
}
