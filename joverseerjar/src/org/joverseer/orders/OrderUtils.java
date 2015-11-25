package org.joverseer.orders;

import java.util.Random;

import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.domain.Character;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;


public class OrderUtils {
    static Random random = new Random();
    
    public static Game getGame() {
        Game g = GameHolder.instance().getGame();
        return g;
    }
    
    public static Turn getTurn() {
        return getGame().getTurn();
    }
    
    public static Character getCharacterFromId(String id) {
        return (Character)getTurn().getContainer(TurnElementsEnum.Character).findFirstByProperty("id", id);
    }
    
    public static void appendOrderResult(Character c, String result) {
        c.setOrderResults((c.getOrderResults().equals("") ? "" : c.getOrderResults() + " ") + result);
    }
    
    public static int getRandomNumber(int max) {
        return new Double(Math.random() * (max + 1)).intValue();
    }
    
    public static int getRandomNumber(int min, int max) {
        return getRandomNumber(max - min) + min;
    }
    
    public static int getGaussianRandomNumber(int min, int max) {
        return new Double(random.nextGaussian() / 2 * (max - min) + (max - min) / 2).intValue();
        //return new Double(random.nextGaussian() * 100).intValue();
    }

//    public void testRandomNumber() {
//        int sum = 0;
//        int total = 10000;
//        for (int i=0; i<total; i++) {
//            int v = getRandomNumber(-3, 3);
//            sum += v;
//            System.out.println(v);
//        }
//        System.out.println(new Double(sum) / total);
//    }
    
    public static void main(String[] args) {
        int sum = 0;
        int total = 1000000;
        for (int i=0; i<total; i++) {
            int v = getGaussianRandomNumber(0, 100);
            sum += v;
            System.out.println(v);
        }
        System.out.println(((double)sum) / total);
    }
    
    

}
