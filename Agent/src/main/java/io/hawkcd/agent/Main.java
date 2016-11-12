/*
 * Copyright (C) 2016 R&D Solutions Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.hawkcd.agent;

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