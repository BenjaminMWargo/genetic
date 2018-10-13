import java.util.List;
import java.util.Random;
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
    }public static class schedule implements Comparable<schedule>{
        //Each schedule is a list of courses
        public List<course> courseList;
        public int fitness;
        public schedule(){
            this.fitness = 0;
            courseList = new ArrayList<course>();
            
        }
        public schedule deepCopy(){
            schedule x = new schedule();
            x.fitness = this.fitness;
            for (course c: this.courseList ) {
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
           this.fitness = 0;
            //For each course in the list
            for(course i : courseList){
                //Reset  rule flags
                roomTaken = false;
                profBusy = false;
                tooSmall = false;
                mediaReq = false;
                //Reset Fitness 
                
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
                if ((i.courseMedia == true)&(i.room.roomMedia == false)){
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
            System.out.println(this.fitness);
            
       }
       public int compareTo(schedule s){
        if(this.fitness < s.fitness){
            return 1;
        }else if (this.fitness > s.fitness){
            return -1;
        }else{
            return 0;
        }

    }
    }
    public static void printSchedule(schedule s){
        System.out.printf("%3s | %-8s | %-12s | %-4s | %-11s | %-10s | %-9s | %-11s | %-6s | %-4s | %4s%n", "CRN", "Course", "Professor", "Size", "Needs media", "Room Name", "Room Size", "Room media?", "Period", "Days", "Time");
        for (course c :s.courseList){
            System.out.printf("%3d | %-8s | %-12s | %-4d | %-11b | %-10s | %-9d | %-11b | %-6d | %-4s | %4d%n", c.CRN, c.courseName, c.prof, c.courseSize, c.courseMedia, c.room.roomName, c.room.roomSize, c.room.roomMedia, c.timeFrame.period, c.timeFrame.days, c.timeFrame.time);
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
         
       return data;
    }
    public static List<room> getRoomList(){
        String ROOMFILE = "rooms.txt";
        List<String[]> roomMaster = readFile(ROOMFILE);
        List<room> roomList = new ArrayList<room>();
        boolean x;
        for(String[] i :roomMaster){
            //Load all rooms into roomlist
            if (Integer.parseInt(i[2])==1){
                x = true;
            }else {x= false;}
            roomList.add(new room(i[0],Integer.parseInt(i[1]),x));
        }
        return roomList;
    }
    public static List<timeFrame> getTimeList(){
        String PERIODFILE = "periods.txt";
        List<String[]> periodMaster = readFile(PERIODFILE);
        List<timeFrame> periodList = new ArrayList<timeFrame>();
        for(String[] i :periodMaster){
            //Load all rooms into roomlist
            periodList.add(new timeFrame(i[1],Integer.parseInt(i[2]),Integer.parseInt(i[0])));
        }
        return periodList;
    }
    public static schedule makeSchedule(){
        
        String COURSEFILE = "courses.txt";
        
        List<String[]> courseMaster = readFile(COURSEFILE);
        
        
        List<room> roomList = new ArrayList<room>();
        List<timeFrame> periodList = new ArrayList<timeFrame>();
        boolean x;
        schedule s = new schedule();
       
        for(String[] i :courseMaster){
              //Load all courses into s.courseList     
            if (Integer.parseInt(i[4])==1){
                x = true;
            }else {x= false;}
            s.courseList.add(new course(Integer.parseInt(i[0]),i[1],i[2],Integer.parseInt(i[3]),x));
        }
        roomList = getRoomList();
        periodList = getTimeList();
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
    public static void printPopulation(List<schedule> s){
        //Prints the entire population
        int x = 1;
        for (schedule i:s){
            System.out.println("Schedule:" + x);
            printSchedule(i);
            x++;
        }
    }
    public static schedule getStatistics(List<schedule> s){
        //Prints statistics for the population. Returns the best schedule of the pop
        double total = 0;
        double average;
        int count = 0;
        double max = -100000000;
        double min = 1000;
        schedule best = null;
        for (schedule i:s){
            count++;
            total = total + i.fitness;
            System.out.println("Chromosome:" + count + "  Fitness:" + i.fitness);
            if (i.fitness > max){
                max = i.fitness;
                best = i.deepCopy();
            }
            if (i.fitness < min){
                min = i.fitness;
            }
        }
        average = total/count;
        System.out.println("Population Average:" + average + "  Min:" + min + " Max:" + max);
        return best;
    }
    public static List<schedule> tournamentSelect(List<schedule> s){
        List<schedule> nextGen = new ArrayList<schedule>();
        Random rand = new Random();
        schedule a, b;
        a = s.get(rand.nextInt(s.size()));
        b = s.get(rand.nextInt(s.size()));
       // c = s.get(rand.nextInt(s.size()));
        for(int x=0; x <= s.size(); x++){
                // starts it off
                if(a.fitness > b.fitness){
                    nextGen.add(a.deepCopy());                    
                }
                // checks if the next 
                else if(a.fitness <= b.fitness){
                    nextGen.add(b.deepCopy());                   
                }
            a = s.get(rand.nextInt(s.size()));
            b = s.get(rand.nextInt(s.size()));
        }

        updateFitness(nextGen);
        return nextGen;
    }

    public static List<schedule> elitistSelection(List<schedule> s){
        //Select from the top 50% randomly
        List<schedule> nextGen = new ArrayList<schedule>();
        int max = s.size();
        double SELECTIONRANGE = .5;
        int range = (int)(s.size()*SELECTIONRANGE);
        Random rand = new Random();
        //Sort by highest fitness
        Collections.sort(s);
        //Pick randomly from the top 50%
        for (int i=0;i<max;i++){
            nextGen.add(s.get(rand.nextInt(range)+1).deepCopy());
        }
        
        updateFitness(nextGen);
        return nextGen;
    }
    public static void updateFitness(List<schedule> s){
        //Updates fitness for entire population
        for(schedule i:s){
            i.findFitness();
        }
    }
    public static List<schedule> crossover(List<schedule> s,double rate){
        List<schedule> nextGen = new ArrayList<schedule>();
        Random rand = new Random();
        List<course> tempCourse = new ArrayList<course>();
        List<course> tempCourse2 = new ArrayList<course>();
        List<course> tempCourse3 = new ArrayList<course>();
        schedule a,b = new schedule();
        int x;
        while (s.size()>0) {
            if (s.size() == 1){
                //edge case of odd population
                nextGen.add(s.get(0).deepCopy());
                s.remove(0);
                break;
            }   
            tempCourse.clear();
            tempCourse2.clear();
            tempCourse3.clear(); 
            a = s.get(0).deepCopy();
            //a.fitness = s.get(0).fitness;
            s.remove(0);
            b = s.get(0).deepCopy();
            //b.fitness = s.get(0).fitness;
            s.remove(0);
            //a.findFitness();
           // b.findFitness();
            //Roll for crossover
            if (rand.nextInt(100)<(rate*100)){            
                x = rand.nextInt(26);
                for (course i:a.courseList){
                    //Take the top half of the course list up to x
                    if (i.CRN <= x){
                        //if CRN is in range, search for it and put it in list1
                        y:for (int j = i.CRN;j<27;j++){
                            if (a.courseList.get(j).CRN == i.CRN){
                                tempCourse.add(a.courseList.get(j).deepCopy());
                               // a2.courseList.remove(j);
                                break y;
                            }
                        }
                    }
                    else {
                        //Put the rest in list2
                        y:for (int j = i.CRN;j<27;j++){
                            if (a.courseList.get(j).CRN == i.CRN){
                                tempCourse3.add(a.courseList.get(j).deepCopy());
                               // a2.courseList.remove(j);
                                break y;
                            }
                        }
                    }
                }
                for (course i:b.courseList){
                    //Take the top half of the course list up to x
                    if (i.CRN <= x){
                        //if CRN is in range, search for it and put it in list1
                        y:for (int j = i.CRN;j<27;j++){
                            if (b.courseList.get(j).CRN == i.CRN){
                                tempCourse2.add(b.courseList.get(j).deepCopy());
                               // b2.courseList.remove(j);
                                break y;
                            }
                        }
                    }
                    else{
                        //if CRN is in range, search for it and remove it
                        y: for (int j = i.CRN;j<27;j++){
                            if (b.courseList.get(j).CRN == i.CRN){
                                tempCourse.add(b.courseList.get(j).deepCopy());
                              //  b2.courseList.remove(j);
                                break y;
                            }
                        }
                    }

                   
                }
                for(course c:tempCourse3){
                    tempCourse2.add(c);
                }
                a.courseList.clear();
                b.courseList.clear();
                a.courseList = tempCourse;
                b.courseList = tempCourse2;
                nextGen.add(a);
                nextGen.add(b);
            }else{
                //Add if no crossover
                for (course i :a.courseList){
                    tempCourse.add(a.courseList.deepCopy());
                }
                for (course i :b.courseList){
                    tempCourse2.add(b.courseList.deepCopy());
                }
                
                a.courseList.clear();
                b.courseList.clear();
                a.courseList = tempCourse;
                b.courseList = tempCourse2;
                nextGen.add(a);
                nextGen.add(b);
                
            }
            
            

        }
        updateFitness(nextGen);
        return nextGen;
    }
    public static List<schedule> mutation(List<schedule> s,double rate){
        List<schedule> nextGen = new ArrayList<schedule>();
        Random rand = new Random();
        List<timeFrame> timeList = new ArrayList<timeFrame>();
        List<room> roomList = new ArrayList<room>();
        int x;
        for(schedule i:s){
            for(course c:i.courseList){
                 //For each class Roll for mutation
                 if (rand.nextInt(100)<(rate*100)){
                    //Find type of mutation
                    x = rand.nextInt(((3-1)+1)-1);
                    if ((x==1)||(x==3)){
                        //Mutate room
                        roomList = getRoomList();
                        Collections.shuffle(roomList);
                        c.room = roomList.get(0);

                    }
                    if ((x==2)||(x==3)){
                        //Mutate time
                        timeList = getTimeList();
                        Collections.shuffle(timeList);
                        c.timeFrame = timeList.get(0);

                    }

                 }
            }
           
        }
        return s;
    }
    public static void main(String args[]){
        
        if (args.length < 4){
            System.out.print("Need input params: \"java genetic *size of population* *Max generations* *CrossoverRate* *Mutation Rate*\" " );
            System.exit(0);
        }
        //Input Params
        int size = Integer.parseInt(args[0]);
        int max = Integer.parseInt(args[1]);
        double crossRate = Double.parseDouble(args[2]);
        double mutRate = Double.parseDouble(args[3]);
        //Other Var
        schedule globalBest;
        schedule generationBest;
        // population should be a list of schedules
        List<schedule> population = new ArrayList<schedule>();
        List<schedule> nextGen = new ArrayList<schedule>();

        for (int i =0;i<size;i++){
            //Added size schedules to the population
            population.add(makeSchedule()); 
        }
        System.out.println("===============================Generation 0 ===============================");
        globalBest = getStatistics(population).deepCopy();
       // printPopulation(population);
       //Loop max times
        for (schedule s:population){
            nextGen.add(s.deepCopy());
        }

       
        for (int i=1;i<=max;i++){
            //Selections - 
           // nextGen = elitistSelection(nextGen);
            //nextGen = tournamentSelect(nextGen);
            System.out.println("====after select=====");
            getStatistics(nextGen);
            //Crossover
            nextGen = crossover(nextGen,crossRate);
             System.out.println("====after cross=====");
             getStatistics(nextGen);
            //Mutation
            nextGen = mutation(nextGen, mutRate);
            // System.out.println("====after mut=====");
            // getStatistics(nextGen);
            //Evaluate
            Collections.sort(nextGen);
            System.out.println("===============================Generation " + i + "===============================");
           generationBest = getStatistics(nextGen);
           if (i%10==0){
               printPopulation(nextGen);
           }
            if (generationBest.fitness>globalBest.fitness){
               globalBest = generationBest.deepCopy();
            }
           // population.clear();
           
        }
        System.out.println("=============================================Best Schedule=======================================================");
        printSchedule(globalBest);
        
      
    }
}