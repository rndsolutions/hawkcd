package io.hawkcd.materials.materialupdaters;

import com.google.gson.Gson;
import io.hawkcd.model.NugetMaterial;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;


public class NugetMaterialUpdater extends MaterialUpdater<NugetMaterial> {
    private Client restClient;
    private Gson jsonConverter = new Gson();

    public NugetMaterialUpdater() {
        this.restClient = ClientBuilder.newClient();
        this.jsonConverter = new Gson();
    }

    @Override
    public NugetMaterial getLatestMaterialVersion(NugetMaterial materialDefinition) {
        String targetUrl = this.constructUrl(materialDefinition.getRepositoryUrl(), materialDefinition.getPackageId());
        String response = this.restClient.target(targetUrl).request().get(String.class);

//        JsonParser parser = new JsonParser();
//        JsonObject obj = parser.parse(response).getAsJsonObject();
//        JsonArray imgurl = obj.get("versions").getAsJsonArray();
//
//        String packageVersion = imgurl.get(imgurl.size() - 1).getAsString();
//        materialDefinition.setPackageVersion(packageVersion);


        String[] array = this.jsonConverter.fromJson(response, String[].class);
        String packageVersion = array[array.length - 1];
        materialDefinition.setPackageVersion(packageVersion);

        return materialDefinition;
    }

    @Override
    public boolean areMaterialsSameVersion(NugetMaterial latestMaterial, NugetMaterial dbMaterial) {
        String latestVersion = latestMaterial.getRepositoryUrl() + latestMaterial.getPackageVersion() + latestMaterial.getPackageVersion();
        String dbVersion = dbMaterial.getRepositoryUrl() + dbMaterial.getPackageVersion() + dbMaterial.getPackageVersion();

        return latestVersion.equals(dbVersion);
    }

    private String constructUrl(String repositoryUrl, String packageId) {
//        String url = repositoryUrl +
//                "flatcontainer/" +
//                packageId +
//                "/index.json";
        String url = repositoryUrl + "package-versions/" + packageId;

        return url;
    }
}
