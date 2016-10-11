package net.hawkengine.http.tests;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.hawkengine.core.ServerConfiguration;
import net.hawkengine.core.utilities.deserializers.TaskDefinitionAdapter;
import net.hawkengine.http.ArtifactController;
import net.hawkengine.model.TaskDefinition;
import net.hawkengine.services.FileManagementService;
import net.hawkengine.services.interfaces.IFileManagementService;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import javax.ws.rs.core.Response;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ArtifactControllerTests {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(2356);

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private IFileManagementService mockedFileManagementService;
    private File mockedFile;
    private List<File> mockedFileList;

    private ArtifactController artifactController;
    private Gson jsonConverter;

    @BeforeClass
    public static void setUpClass() {
        ServerConfiguration.configure();
    }

    @Before
    public void setUp() {
        this.mockedFileManagementService = Mockito.mock(FileManagementService.class);
        this.artifactController = new ArtifactController(this.mockedFileManagementService);
        this.mockedFile = new File("pathToFile");
        this.mockedFileList = new ArrayList<>();

        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .create();
    }

    @Test
    public void artifactController_instantiated_notNull() {
        this.artifactController = new ArtifactController();

        Assert.assertNotNull(this.artifactController);
    }

    @Test
    public void zipFile_validData_statusOk() {
        //Arrange
        Mockito.when(this.mockedFileManagementService.getRootPath(Mockito.anyString())).thenReturn("rootPath");
        Mockito.when(this.mockedFileManagementService.getPattern(Mockito.anyString(), Mockito.anyString())).thenReturn("");
        Mockito.when(this.mockedFileManagementService.getFiles(Mockito.anyString(), Mockito.anyString())).thenReturn(this.mockedFileList);
        Mockito.when(this.mockedFileManagementService.generateUniqueFile(Mockito.anyString(), Mockito.anyString())).thenReturn(this.mockedFile);

        String expectedDirectory = "testFileDirectory";

        Response actualResponse = this.artifactController.zipFile(expectedDirectory);

        Assert.assertEquals(200, actualResponse.getStatus());
    }

    @Test
    public void zipFile_noRootPath_statusNotFound() {
        //Arrange
        Mockito.when(this.mockedFileManagementService.getRootPath(Mockito.anyString())).thenReturn("");

        String expectedDirectory = "testFileDirectory";

        Response actualResponse = this.artifactController.zipFile(expectedDirectory);

        Assert.assertEquals(404, actualResponse.getStatus());
    }

    @Test
    public void zipFile_noFiles_statusNotFound() {
        //Arrange
        Mockito.when(this.mockedFileManagementService.getRootPath(Mockito.anyString())).thenReturn("rootPath");
        Mockito.when(this.mockedFileManagementService.getPattern(Mockito.anyString(), Mockito.anyString())).thenReturn("");
        Mockito.when(this.mockedFileManagementService.getFiles(Mockito.anyString(), Mockito.anyString())).thenReturn(null);

        String expectedDirectory = "testFileDirectory";

        Response actualResponse = this.artifactController.zipFile(expectedDirectory);

        Assert.assertEquals(404, actualResponse.getStatus());
    }

    @Test
    public void zipFile_zipFilesErrorMessage_statusBadRequest() {
        //Arrange
        Mockito.when(this.mockedFileManagementService.getRootPath(Mockito.anyString())).thenReturn("rootPath");
        Mockito.when(this.mockedFileManagementService.getPattern(Mockito.anyString(), Mockito.anyString())).thenReturn("");
        Mockito.when(this.mockedFileManagementService.getFiles(Mockito.anyString(), Mockito.anyString())).thenReturn(this.mockedFileList);
        Mockito.when(this.mockedFileManagementService.generateUniqueFile(Mockito.anyString(), Mockito.anyString())).thenReturn(this.mockedFile);
        Mockito.when(this.mockedFileManagementService.zipFiles("pathToFile", this.mockedFileList, "rootPath", false)).thenReturn("errorMessage");

        String expectedDirectory = "testFileDirectory";

        Response actualResponse = this.artifactController.zipFile(expectedDirectory);

        Assert.assertEquals(400, actualResponse.getStatus());
    }

//    @Test
//    public void unzipFile_validData_statusOk() {
//        //Arrange
//        Mockito.when(this.mockedFileManagementService.unzipFile("pathToFile", "rootPath")).thenReturn(null);
//
//        UploadArtifactInfo uploadArtifactInfo = new UploadArtifactInfo(this.mockedFile, "testDestination");
//        String uploadArtifactInfoAsString = this.jsonConverter.toJson(uploadArtifactInfo);
//
//        Response actualResponse = this.artifactController.unzipFile("testPipeline", "testPipelineExecutionFolder", uploadArtifactInfoAsString);
//
//        Assert.assertEquals(200, actualResponse.getStatus());
//    }
//
//    @Test
//    public void unzipFile_invalidData_statusNotFound() {
//        //Arrange
//        this.mockedFile = new File("");
//        Mockito.when(this.mockedFileManagementService.unzipFile(Mockito.anyString(), Mockito.anyString())).thenReturn("Cannot unzip file");
//
//        UploadArtifactInfo uploadArtifactInfo = new UploadArtifactInfo(this.mockedFile, "testDestination");
//        String uploadArtifactInfoAsString = this.jsonConverter.toJson(uploadArtifactInfo);
//
//        Response actualResponse = this.artifactController.unzipFile("testPipeline", "testPipelineExecutionFolder", uploadArtifactInfoAsString);
//
//        Assert.assertEquals(404, actualResponse.getStatus());
//    }

//    @Test
//    public void uploadArtifact_validUrl_statusOk() {
//        //Arrange
//        stubFor(get(urlEqualTo("/my")).willReturn(aResponse().withHeader("Content-Type", "text/html").withBody("BODY!!!!").withStatus(200)));
//        stubFor(post(urlEqualTo("/Artifacts/testPipeline/testStage/testJob/upload-artifact")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "multipart/form-data")));
//
//        Mockito.when(this.mockedFileManagementService.unzipFile(Mockito.anyString(), Mockito.anyString())).thenReturn("errorMessage");
//
//        String requestSource = "http://localhost:8080/Artifacts/testPipeline/testStage/testJob/upload-artifact";
//        //String requestSource = "http://localhost:8080/my";
//        Client client = Client.create();
//        try {
//            this.mockedFile = folder.newFile("pesho");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        WebResource webResource = client.resource(requestSource);
//        ClientResponse response = webResource.post(ClientResponse.class, this.mockedFile);
//
//        Assert.assertEquals(200, response.getStatus());
//    }

//    @Test
//    public void fetchArtifact_validUrl_statusOk() {
//        //Arrange
//        stubFor(post(urlEqualTo("/Artifacts/testPipeline/testStage/testJob/fetch-artifact")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")));
//
//        String requestSource = "http://localhost:8080/Artifacts/testPipeline/testStage/testJob/fetch-artifact";
//        //String requestSource = "http://localhost:8080/my";
//        Client client = Client.create();
//
//        WebResource webResource = client.resource(requestSource);
//        ClientResponse response = webResource.post(ClientResponse.class, "testDir");
//
//        Assert.assertEquals(200, response.getStatus());
//    }
}
