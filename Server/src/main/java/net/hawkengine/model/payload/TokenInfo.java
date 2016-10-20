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

package net.hawkengine.model.payload;

import net.hawkengine.model.User;

import java.time.LocalDateTime;

public class TokenInfo {
    private User user;
    private LocalDateTime issued;
    private LocalDateTime expires;


    public TokenInfo() {
        this.issued = LocalDateTime.now();
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getIssued() {
        return this.issued;
    }

    public LocalDateTime getExpires() {
        return this.expires;
    }

    public void setExpires(LocalDateTime expires) {
        this.expires = expires;
    }
}
