import cits3001_2016s2.*;
import s21504053.*;
import s21130321.*;

import java.util.*;
import java.io.*;

public class Run{

 public static void main(String args[]){

  try{
      File f = new File("Results.html");
      FileWriter fw = new FileWriter(f);
      Competitor[] contenders = {
        new Competitor(new cits3001_2016s2.RandomAgent(),"Randy","Tim"),
        new Competitor(new s21504053.BoneCrusher(),"BoneCrusher","Jack"),
        new Competitor(new s21504053.LogicMan(),"LogicMan","Ruley"),
        new Competitor(new s21130321.Ernie21130321(),"Ernie","Tahmer"),
        new Competitor(new s21130321.ErnieBae(), "Bae", "Baeblade")

      };
      fw.write(Game.tournament(contenders, 10000));
      fw.close();
    }
}
