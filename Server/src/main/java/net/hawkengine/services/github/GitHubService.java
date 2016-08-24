package net.hawkengine.services.github;

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
