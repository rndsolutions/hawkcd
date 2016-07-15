package net.hawkengine.http;

import net.hawkengine.model.User;
import net.hawkengine.model.dto.GithubAuthDto;
import net.hawkengine.services.UserService;
import net.hawkengine.services.github.GitHubService;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Consumes("application/json")
@Produces("application/json")
@Path("auth")
public class AuthController {

    private static final String GH_ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token";
    //TODO: move this to the config
    private static final String GH_CLIENT_ID = "2d3dbbf586d2260cbd68";
    //TODO: move this to the config
    private static final String GH_CLIENT_SERCRET = "4f2c569d33f6ad8ed4fb3595677321adc2af32b7";

    private UserService usrService;

    public AuthController(){
        this.usrService =  new UserService();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/github")
    public Response updateUserInfo(GithubAuthDto authDto) throws IOException {

        //TODO: implement schema validation
        //extract authorization code
        String code = authDto.getCode();
        //request access token
        String accessToken = getGitHubAccessToken(code);
        //find out the gh user details
        GitHubService ghService = new GitHubService(accessToken);
        Map ghUserDetails = ghService.getMyself();
        //read user email
        String email = (String) ghUserDetails.get("email");

        //register a new user
        User user =  new User();
        user.setEmail(email);
        user.setProvider("GitHub");
        usrService.add(user);

            return Response.status(Response.Status.CREATED)
                    .entity("User Registered")
                    .build();
    }


    private String getGitHubAccessToken(String authCode) {

        String ghResource = String.format("%s?client_id=%s&client_secret=%s&code=%s",GH_ACCESS_TOKEN_URL,GH_CLIENT_ID,GH_CLIENT_SERCRET,authCode);
        Client client = Client.create();
        WebResource resource = client.resource(ghResource);

        ClientResponse response = resource.get(ClientResponse.class);
        if (response.getStatus() !=200 ){
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        String stringToMatch = response.getEntity(String.class);

        //run through regext to extract the access_token
        Pattern p = Pattern.compile("access_token=.*?&");
        Matcher m = p.matcher(stringToMatch);
        String at = null;
        if (m.find()){
            at = m.group(0);
        }
        if (at!=null){
            return at.replace("access_token=","").replace("&","");
        }else
        {
            return null;
        }
    }
}




