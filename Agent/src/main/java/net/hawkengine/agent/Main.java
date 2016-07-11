package net.hawkengine.agent;

import java.util.Scanner;

public class Main {

    public static void main(String[] arr) {

        Agent agent = new Agent();
        Scanner scanner = new Scanner(System.in);

        agent.start();
        System.out.println();
        System.out.println("====================== AGENT ========================");
        System.out.println();
        System.out.println("*****************Available Commands******************");
        System.out.println("========================stop=========================");
        System.out.println("*****************Available Commands******************");
        System.out.println();

        while (true) {
            String command = scanner.nextLine();

            if (command.equals("stop")) {
                agent.stop();
                System.exit(0);
            }
        }
    }
}