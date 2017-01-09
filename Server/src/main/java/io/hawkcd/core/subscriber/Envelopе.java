/*
 *   Copyright (C) 2016 R&D Solutions Ltd.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *
 */

package io.hawkcd.core.subscriber;

import java.util.List;

/**
 * The class holds a concrete object reference to the object sent by Publishers
 */
public class Envelopе {
    private Object object;
    private String packageName;

    public Envelopе(){
    }

    public Envelopе(Object object) {
        if (object != null) {
            this.setFields(object);
        }
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    /**
     * Sets the fields of the Envelope class
     * Field packageName is set to the Type of the passed Object, if it is not a List.
     * If it is a List, packageName is set to the Type of the members in the List.
     * If the List is empty, packageName is set to Type Object.
     * @param object
     */
    private void setFields(Object object) {
        this.object = object;
        if (object instanceof List) {
            List<?> list = (List<?>) object;
            if (!list.isEmpty()) {
                this.packageName = list.get(0).getClass().getCanonicalName();
            } else {
                this.packageName = Object.class.getCanonicalName();
            }
        } else {
            this.packageName = object.getClass().getCanonicalName();
        }
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
