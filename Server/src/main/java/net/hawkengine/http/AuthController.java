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

package net.hawkengine.http;

import com.google.gson.Gson;
import net.hawkengine.core.utilities.deserializers.TokenAdapter;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.User;
import net.hawkengine.model.dto.LoginDto;
import net.hawkengine.model.dto.RegisterDto;
import net.hawkengine.model.enums.NotificationType;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.UserService;
import net.hawkengine.ws.SessionPool;
import org.apache.commons.codec.digest.DigestUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.ClientResponse;
//import com.sun.jersey.api.client.WebResource;

@Consumes("application/json")
@Produces("application/json")
@Path("auth")
public class AuthController {

    private static final String GH_ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token";
    //TODO: move this to the config
    private static final String GH_CLIENT_ID = "2d3dbbf586d2260cbd68";
    //TODO: move this to the config
    private static final String GH_CLIENT_SERCRET = "";

    private UserService userService;

    public AuthController() {
        this.userService = new UserService();
    }

//    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path("/github")
//    public Response updateUserInfo(GithubAuthDto authDto) throws IOException {
//
//        //TODO: implement schema validation
//        //extract authorization code
//        String code = authDto.getCode();
//        //request access token
//        String accessToken = getGitHubAccessToken(code);
//        //find out the gh user details
//        GitHubService ghService = new GitHubService(accessToken);
//        Map ghUserDetails = ghService.getMyself();
//        //read user email
//        String email = (String) ghUserDetails.get("email");
//
//        ArrayList<User> all = (ArrayList<User>) this.userService.getAll().getObject();
//
//        boolean isUserRegistered = false;
//        //loop through users to check if the user already exists
//
//        User user = null;
//        for(User usr:all){
//            String uEmail = usr.getEmail();
//            if ( uEmail.equals(email)){
//                isUserRegistered = true;
//                user = usr;
//                break;
//            }
//        }
//
//        if (!isUserRegistered){
//            //register a new user
//            user =  new User();
//            user.setEmail(email);
//            user.setProvider("GitHub");
//            userService.add(user);
//        }
//
//
//        return Response.status(Response.PipelineStatus.CREATED)
//                .entity(user)
//                .build();
//    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    @Path("/login")
    public Response login(LoginDto login) throws IOException {
        String hashedPassword = DigestUtils.sha256Hex(login.getPassword());

        ServiceResult serviceResult = this.userService.getByEmailAndPassword(login.getEmail(), hashedPassword);
        if (serviceResult.getNotificationType() == NotificationType.ERROR) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(serviceResult)
                    .build();
        }

        User userFromDb = (User) serviceResult.getObject();

        if (!userFromDb.isEnabled()) {
            serviceResult.setNotificationType(NotificationType.ERROR);
            serviceResult.setMessage("Cannot login");
            serviceResult.setObject(null);
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        String token = TokenAdapter.createJsonWebToken(userFromDb, 30L);

        Gson gson = new Gson();

        String jsonToken = gson.toJson(token);

        return Response.ok(jsonToken).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    @Path("/logout")
    public Response logout(String email) throws IOException {
        SessionPool.getInstance().logoutUserFromAllSessions(email);

        return Response.ok().build();
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/register")
    public Response register(RegisterDto newUser) {

        List<Permission> userPermissions = new ArrayList<>();

        User user = new User();
        user.setEmail(newUser.getEmail());
        user.setPassword(newUser.getPassword());

        //TODO: move this to update user, should not be here, left till adminUsers is ready
        if (newUser.getPermissions() != null) {
            userPermissions.addAll(newUser.getPermissions());
        }
        user.setPermissions(userPermissions);

        ServiceResult serviceResult = this.userService.addUserWithoutProvider(user);

        if (serviceResult.getNotificationType() == NotificationType.ERROR) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(serviceResult)
                    .build();
        } else {
            return Response.status(Response.Status.CREATED)
                    .entity(serviceResult)
                    .build();
        }
    }

//    private String getGitHubAccessToken(String authCode) {
//
//        String ghResource = String.format("%s?client_id=%s&client_secret=%s&code=%s",GH_ACCESS_TOKEN_URL,GH_CLIENT_ID,GH_CLIENT_SERCRET,authCode);
//        Client client = Client.create();
//        WebResource resource = client.resource(ghResource);
//
//        ClientResponse response = resource.get(ClientResponse.class);
//        if (response.getStatus() !=200 ){
//            throw new RuntimeException("Failed : HTTP error code : "
//                    + response.getStatus());
//        }
//
//        String stringToMatch = response.getEntity(String.class);
//
//        //run through regext to extract the access_token
//        Pattern p = Pattern.compile("access_token=.*?&");
//        Matcher m = p.matcher(stringToMatch);
//        String at = null;
//        if (m.find()){
//            at = m.group(0);
//        }
//        if (at!=null){
//            return at.replace("access_token=","").replace("&","");
//        }else
//        {
//            return null;
//        }
//    }

}




