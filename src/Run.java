import cits3001_2016s2.*;
import s21504053.*;
import s21130321.*;

import java.io.*;

public class Run{

 public static void main(String args[]){

  try{
      File f = new File("Results.html");
      FileWriter fw = new FileWriter(f);
      Competitor[] contenders = {
        new Competitor(new cits3001_2016s2.RandomAgent(),"Randy","Tim"),
        new Competitor(new BoneCrusher21504053(),"BoneCrusher","Jack"),
        new Competitor(new LogicMan(),"LogicMan","Ruley"),
        new Competitor(new ErnieGenetics21130321(),"Ernie","Tahmer"),
        new Competitor(new ErnieBae21130321(), "Bae", "Baeblade")
      };
      fw.write(Game.tournament(contenders, 10000));
      fw.close();
    } catch (IOException io) {}
  }

}
