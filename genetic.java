import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
public class genetic{
    public static class room{
        public String roomName;
        public int roomSize;
        public boolean roomMedia;
        public room deepCopy(){
            room x = new room(this.roomName,this.roomSize,this.roomMedia);
            return x;
        }
        public room(String name,int size,boolean media){
            this.roomName = name;
            this.roomSize = size;
            this.roomMedia = media;
        }

    }
    public static class timeFrame{
        public String days;
        public int time;
        public int period;
        public timeFrame deepCopy(){
            timeFrame x = new timeFrame(this.days,this.time,this.period);
            return x;
        }
        public timeFrame(String days,int time,int period){
            this.days = days;
            this.time = time;
            this.period = period;
        }
    }
    public static class course{
        //Each course needs a time and room
        public int CRN;
        public String courseName;
        public String prof;
        public int courseSize;
        public boolean courseMedia;
        public timeFrame timeFrame;
        public room room;
        public course(int CRN,String name,String prof,int size,boolean media){
            this.CRN = CRN;
            this.courseName = name;
            this.prof = prof;
            this.courseSize = size;
            this.courseMedia= media;
            timeFrame = null;
            room = null;
        }
        public course deepCopy(){
            course x = new course(this.CRN,this.courseName,this.prof,this.courseSize,this.courseMedia);
            if (this.timeFrame != null){
                x.timeFrame = this.timeFrame.deepCopy();
            }else{ x.timeFrame = null;}
            if (this.room != null){
                x.room = this.room.deepCopy();
            }else{ x.room = null;}
            return x;
        }
    }public static class schedual{
        //Each schedual is a list of courses
        public List<course> courseList;
        public int fitness;
        public schedual(){
            this.fitness = 0;
            courseList = new ArrayList<course>();
            
        }
        public schedual deepCopy(){
            schedual x = new schedual();
            x.fitness = this.fitness;
            for (course c: courseList ) {
                x.courseList.add(c.deepCopy());
            }
            return x;
        }
    }
    
    public static void main(String args[]){
        System.out.println("hi");
    }
}