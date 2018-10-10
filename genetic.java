import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
       public void findFitness(){
           //For each class in the list, compare it to all the other classes in the list.
           //Rules
           //-300 if room has two classes at once
           //-300 if prof is teaching two classes at the same time
           //
           //-50 if class needs media and the room doesn't have it
           //-70 if room is too small
           //+20 if class needs media and the room has media
           //+20 if room is big enough
           boolean roomTaken = false;
           boolean profBusy = false;
           boolean tooSmall = false;
           boolean mediaReq = false;
            //For each course in the list
            for(course i : courseList){
                //Reset  rule flags
                roomTaken = false;
                profBusy = false;
                tooSmall = false;
                mediaReq = false;
                for(course j : courseList){
                    //Skip over itself
                    if (i.CRN == j.CRN){
                        continue;
                    }
                   //==== teacher is teaching twice =====
                    //Teacher,Day,and Time are the same
                    if ((i.prof == j.prof) & (i.timeFrame.days == j.timeFrame.days) & (i.timeFrame.time==j.timeFrame.time)){
                        profBusy = true;
                    }
                   //====Room is in use ======
                   //==roomName,time and day are the same
                   if ((i.room.roomName == j.room.roomName) & (i.timeFrame.days == j.timeFrame.days) & (i.timeFrame.time==j.timeFrame.time)){
                        roomTaken = true;
                   }
                }
                //===Room too small==== 
                if (i.courseSize < i.room.roomSize){
                    tooSmall = true;
                }
                //===Class needs media, room doesn't have it ===
                if ((i.courseMedia = true)&(i.room.roomMedia == false)){
                    mediaReq = true;
                }
                //Calculate fitness
                if (profBusy){
                    this.fitness = this.fitness - 300;
                }
                if (roomTaken){
                    this.fitness = this.fitness - 300;
                }
                if (tooSmall){
                    this.fitness = this.fitness - 70;
                }else {this.fitness = this.fitness +20;}
                if (mediaReq){
                    this.fitness = this.fitness - 50;
                }else {this.fitness = this.fitness +20;}
            }
       }
    }
    public static void printSchedual(schedual s){
        System.out.println("CRN|Course|Proffessor|Size|Needs media|Room Name|Room Size|Room media?|Period |Days |Time ");
        for (course c :s.courseList){
            System.out.printf(c.CRN + " | " +c.courseName + " | " +c.prof + " | " + c.courseSize + " | " + c.courseMedia + "     | " +c.room.roomName + " | " + c.room.roomSize + " | " +c.room.roomMedia + "     | " +c.timeFrame.period + " | " +c.timeFrame.days + " | " +c.timeFrame.time + "\n");
        }
        System.out.println("Fitness:" + s.fitness);
    }
    public static List<String[]> readFile(String file){
       // String file = "rooms.txt";
        List<String[]> data = new ArrayList<String[]>();
        try{  
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
       
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
           data.add(line.split(" "));
         }
        bufferedReader.close();
       } catch(Exception e){System.out.print("read error");}
       //for(String[] i:data){
       //    for(String j:i){
       //         System.out.print(j + " ");
      //     }
      //     System.out.println("");
      // }
       
       return data;
    }
    public static schedual makeSchedual(){
        String ROOMFILE = "rooms.txt";
        String COURSEFILE = "courses.txt";
        String PERIODFILE = "periods.txt";
        List<String[]> courseMaster = readFile(COURSEFILE);
        List<String[]> roomMaster = readFile(ROOMFILE);
        List<String[]> periodMaster = readFile(PERIODFILE);
        List<room> roomList = new ArrayList<room>();
        List<timeFrame> periodList = new ArrayList<timeFrame>();
        boolean x;
        schedual s = new schedual();
       
        for(String[] i :courseMaster){
              //Load all courses into s.courseList     
            if (Integer.parseInt(i[4])==1){
                x = true;
            }else {x= false;}
            s.courseList.add(new course(Integer.parseInt(i[0]),i[1],i[2],Integer.parseInt(i[3]),x));
        }
        for(String[] i :roomMaster){
            //Load all rooms into roomlist
            if (Integer.parseInt(i[2])==1){
                x = true;
            }else {x= false;}
            roomList.add(new room(i[0],Integer.parseInt(i[1]),x));
        }
        for(String[] i :periodMaster){
            //Load all rooms into roomlist
            periodList.add(new timeFrame(i[1],Integer.parseInt(i[2]),Integer.parseInt(i[0])));
        }
        for(course c:s.courseList){
            //Give each course a random room and period
            Collections.shuffle(periodList);
            Collections.shuffle(roomList);
            c.room = roomList.get(0).deepCopy();
            c.timeFrame = periodList.get(0).deepCopy();
        }
        s.findFitness();
        return s;
    }
    public static void main(String args[]){
        if (args.length < 4){
            System.out.print("Need input params: \"java genetic *size of population* *Max generations* *CrossoverRate* *Mutation Rate*\" " );
            System.exit(0);
        }
        int size = Integer.parseInt(args[0]);
        int max = Integer.parseInt(args[1]);
        double crossRate = Double.parseDouble(args[2]);
        double mutRate = Double.parseDouble(args[3]);

        // population should be a list of scheduals
        List<schedual> population = new ArrayList<schedual>();
        population.add(makeSchedual()); 
        printSchedual(population.get(0));
       // test.courseList.add(new course(0,"Math","Teacher Guy",20,true));
      //  test.courseList.get(0).timeFrame = new timeFrame("MFW", 9, 0);
     //   test.courseList.get(0).room = new room("Room 3", 30, false);
      //  printSchedual(test);
    }
}