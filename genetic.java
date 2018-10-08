import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
public class genetic{
    public static class room{
        String roomName;
        int roomSize;
        boolean roomMedia;
    }
    public static class timeFrame{
        String days;
        int time;
        int period;
    }
    public static class course{
        //Each course needs a time and room
        int CRN;
        String courseName;
        String prof;
        int courseSize;
        boolean courseMedia;
        timeFrame timeFrame;
        room room;
    }    public static class schedual{
        //Each schedual is a list of courses
        List<course> courseList;
        int fitness;
    }
    
    public static void main(String args[]){
        System.out.print("hi");
    }
}