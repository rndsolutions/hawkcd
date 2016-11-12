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

package io.hawkcd.services.github;

//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.ClientResponse;
//import com.sun.jersey.api.client.WebResource;
public class GitHubService {

//    private String USER_ENDPOINT = "https://api.github.com/user?access_token=";
//    private String token;
//
//    public GitHubService(String token) {
//        this.token = token;
//    }
//
//    public Map getMyself() {
//
//        Client client = Client.create();
//
//        //TODO: Consider using headers rather query params for passing the AT
//        WebResource resource = client.resource(String.format("%s%s",USER_ENDPOINT,this.token));
//        resource.header("Content-Type","application/vnd.github.v3+json");
//
//        ClientResponse response = resource.get(ClientResponse.class);
//        int statusCode = response.getStatus();
//        if (statusCode !=200 ){
//            throw new RuntimeException("Failed : HTTP error code : "
//                    + response.getStatus());
//        }
//        Map entity = response.getEntity(Map.class);
//        return entity;
//    }

}
