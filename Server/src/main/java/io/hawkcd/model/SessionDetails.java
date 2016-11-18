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

package io.hawkcd.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;


/**
 * Created by Rado @radoslavMinchev, rminchev@rnd-solutions.net on 15.11.16.
 *
 */
public class SessionDetails extends  DbEntry{

    private String sessionId;
    private String userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private  String userEmail;
    private  boolean isActive;

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public SessionDetails(String sessionIdd){
        this.sessionId =  sessionIdd;
        this.startTime = ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime();
        this.isActive =  true;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getUserEmail() {
        return this.userEmail;
    }

    public boolean isActive() {
        return this.isActive;
    }

    @Override
    public String toString() {
        return "SessionDetails{" +
                "userId='" + this.userId + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
