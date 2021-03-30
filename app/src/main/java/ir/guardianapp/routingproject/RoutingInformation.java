package ir.guardianapp.routingproject;

import android.view.animation.RotateAnimation;

import java.util.Calendar;

public class RoutingInformation {

    private static RoutingInformation instance;
    private RoutingInformation() {}

    public static RoutingInformation getInstance() {
        if (instance == null)
            return new RoutingInformation();
        return instance;
    }

    public String calculateDistance(double distance){
        int kiloMeter = (int) (distance/1000);
        int meter = (int) (distance%1000);

        if(kiloMeter<1){
            meter/=100;
            return ((meter*100) + " m");
        } else if(kiloMeter>=20) {
            return (kiloMeter + " km");
        } else {
            meter/=100;
            return (kiloMeter + "." + meter + " km");
        }
    }

    public String calculateDuration(double duration){
        int day = 0;
        if(duration >= 1440) {
            day += (int)(duration/1440);
            duration = (duration%1440);
        }

        int durationInMin = (int)(duration/60);

        int hourPlus = (int) (durationInMin/60);
        int minPlus = (int) (durationInMin%60);

        String hourStr = String.valueOf(hourPlus);
        String minStr = String.valueOf(minPlus);

        if(hourPlus<1){
            return (minStr + " min");
        } else if(day==0){
            return (hourStr + " hr " + minStr + " min");
        } else {
            return (day + "day and " + hourStr + " hr " + minStr + " min");
        }
    }

    public String calculateArrivalTime(double duration){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.getTime().getHours();
        int minute = calendar.getTime().getMinutes();

        int durationInMin = (int)(duration/60);

        int hourPlus = (int) (durationInMin/60);
        int minPlus = (int) (durationInMin%60);
        hour += hourPlus;
        minute += minPlus;
        if(minute>=60){
            hour ++;
            minute -= 60;
        }

        if(hour >= 24) {
            hour -= 24;
        }

        String hourStr = String.valueOf(hour);
        String minStr = String.valueOf(minute);
        if(hour<10){
            hourStr = "0" + String.valueOf(hour);
        }
        if(minute<10){
            minStr = "0" + String.valueOf(minute);
        }

        return ("(" + hourStr + ":" + minStr + ")");
    }
}
