package net.hawkengine.services.tests;

import net.hawkengine.services.github.GitHubService;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class GitHubServiceTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getMyselfTest() throws Exception {

        //arrange
        GitHubService ghService =  new GitHubService("a4ab23eb8e7d4e29549efd139e0e45b917ed9bbb");


        //act
         ghService.getMyself();

        //assert
        //Assert.assertNotNull(myself);

    }

    @Test
    public void listAllPublicRepositories() throws Exception {

        //arrange

        //act

        //assert
    }

}