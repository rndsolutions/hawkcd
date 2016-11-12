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

package io.hawkcd;

import org.apache.log4j.Logger;

public class Main {
    private static final String FAILED_TO_LOAD_CONFIG_ERROR = "Failed to load configuration file: ";
    private static final Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            String errorMessage = Config.configure();
            if (errorMessage.isEmpty()) {
                HServer HServer = new HServer();
                HServer.configureJetty();
                HServer.start();
            } else {
                LOGGER.error(FAILED_TO_LOAD_CONFIG_ERROR + errorMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}