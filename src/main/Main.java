package main;

import core.gui.MainFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static void n_random(int targetSum, int numberOfDraws) {
        Random r = new Random();
        List<Integer> list = new ArrayList<>();

        int sum = 0;
        for(int i = 0; i < numberOfDraws; i++) {
            int next = r.nextInt(targetSum) + 1;
            list.add(next);
            sum+=next;
        }

        double scale = 1d * targetSum / sum;
        sum = 0;
        for(int i = 0; i < numberOfDraws; i++) {
            list.set(i, (int)(list.get(i) * scale));
            sum+=list.get(i);
        }

        while(sum++ < targetSum) {
            int i = r.nextInt(numberOfDraws);
            list.set(i, list.get(i) + 1);
        }

        System.out.println("Arraylist: " + list + " Sum: " + (sum - 1));
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}
