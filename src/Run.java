import cits3001_2016s2.*;
import s21504053.*;

import java.util.*;
import java.io.*;

public class Run{

 public static void main(String args[]){

  try{
      File f = new File("Results.html");
      FileWriter fw = new FileWriter(f);
      Competitor[] contenders = {
        new Competitor(new cits3001_2016s2.RandomAgent(),"Randy","Tim"),
        new Competitor(new s21504053.BoneCrusher(),"BoneCrusher","Jack Scott")
        };
      fw.write(Game.tournament(contenders, 500));
      fw.close();
    }
    catch(IOException e){System.out.println("IO fail");}
  }

}  


