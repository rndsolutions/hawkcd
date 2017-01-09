package io.hawkcd.scheduler;

import io.hawkcd.model.EnvironmentVariable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class EnvironmentVariableServiceTests {
    private EnvironmentVariableService environmentVariableService;

    @Before
    public void setUp() {
        this.environmentVariableService = new EnvironmentVariableService();
    }

    @Test
    public void getOverriddenVariables_noOverridingVariables_threeObjects() {
        // Arrange
        List<EnvironmentVariable> pipelineVariables = new ArrayList<>();
        EnvironmentVariable expectedPipelineVariable = new EnvironmentVariable("pipelineKey", "pipelineValue");
        pipelineVariables.add(expectedPipelineVariable);

        List<EnvironmentVariable> stageVariables = new ArrayList<>();
        EnvironmentVariable expectedStageVariable = new EnvironmentVariable("stageKey", "stageValue");
        stageVariables.add(expectedStageVariable);

        List<EnvironmentVariable> jobVariables = new ArrayList<>();
        EnvironmentVariable expectedJobVariable = new EnvironmentVariable("jobKey", "jobValue");
        jobVariables.add(expectedJobVariable);

        int expectedResultCollectionSize = 3;

        // Act
        List<EnvironmentVariable> actualResult = this.environmentVariableService.getOverriddenVariables(jobVariables, stageVariables, pipelineVariables);
        EnvironmentVariable actualJobVariable = actualResult.get(0);
        EnvironmentVariable actualStageVariable = actualResult.get(1);
        EnvironmentVariable actualPipelineVariable = actualResult.get(2);
        int actualResultCollectionSize = actualResult.size();

        // Assert
        Assert.assertEquals(expectedJobVariable.getKey(), actualJobVariable.getKey());
        Assert.assertEquals(expectedJobVariable.getValue(), actualJobVariable.getValue());
        Assert.assertEquals(expectedStageVariable.getKey(), actualStageVariable.getKey());
        Assert.assertEquals(expectedStageVariable.getValue(), actualStageVariable.getValue());
        Assert.assertEquals(expectedPipelineVariable.getKey(), actualPipelineVariable.getKey());
        Assert.assertEquals(expectedPipelineVariable.getValue(), actualPipelineVariable.getValue());
        Assert.assertEquals(expectedResultCollectionSize, actualResultCollectionSize);
    }

    @Test
    public void getOverriddenVariables_stageOverridesPipeline_twoObjects() {
        // Arrange
        List<EnvironmentVariable> pipelineVariables = new ArrayList<>();
        EnvironmentVariable expectedPipelineVariable = new EnvironmentVariable("stageKey", "pipelineValue");
        pipelineVariables.add(expectedPipelineVariable);

        List<EnvironmentVariable> stageVariables = new ArrayList<>();
        EnvironmentVariable expectedStageVariable = new EnvironmentVariable("stageKey", "stageValue");
        stageVariables.add(expectedStageVariable);

        List<EnvironmentVariable> jobVariables = new ArrayList<>();
        EnvironmentVariable expectedJobVariable = new EnvironmentVariable("jobKey", "jobValue");
        jobVariables.add(expectedJobVariable);

        int expectedResultCollectionSize = 2;

        // Act
        List<EnvironmentVariable> actualResult = this.environmentVariableService.getOverriddenVariables(jobVariables, stageVariables, pipelineVariables);
        EnvironmentVariable actualJobVariable = actualResult.get(0);
        EnvironmentVariable actualStageVariable = actualResult.get(1);
        int actualResultCollectionSize = actualResult.size();

        // Assert
        Assert.assertEquals(expectedJobVariable.getKey(), actualJobVariable.getKey());
        Assert.assertEquals(expectedJobVariable.getValue(), actualJobVariable.getValue());
        Assert.assertEquals(expectedStageVariable.getKey(), actualStageVariable.getKey());
        Assert.assertEquals(expectedStageVariable.getValue(), actualStageVariable.getValue());
        Assert.assertEquals(expectedResultCollectionSize, actualResultCollectionSize);
    }

    @Test
    public void getOverriddenVariables_jobOverridesPipeline_twoObjects() {
        // Arrange
        List<EnvironmentVariable> pipelineVariables = new ArrayList<>();
        EnvironmentVariable expectedPipelineVariable = new EnvironmentVariable("jobKey", "pipelineValue");
        pipelineVariables.add(expectedPipelineVariable);

        List<EnvironmentVariable> stageVariables = new ArrayList<>();
        EnvironmentVariable expectedStageVariable = new EnvironmentVariable("stageKey", "stageValue");
        stageVariables.add(expectedStageVariable);

        List<EnvironmentVariable> jobVariables = new ArrayList<>();
        EnvironmentVariable expectedJobVariable = new EnvironmentVariable("jobKey", "jobValue");
        jobVariables.add(expectedJobVariable);

        int expectedResultCollectionSize = 2;

        // Act
        List<EnvironmentVariable> actualResult = this.environmentVariableService.getOverriddenVariables(jobVariables, stageVariables, pipelineVariables);
        EnvironmentVariable actualJobVariable = actualResult.get(0);
        EnvironmentVariable actualStageVariable = actualResult.get(1);
        int actualResultCollectionSize = actualResult.size();

        // Assert
        Assert.assertEquals(expectedJobVariable.getKey(), actualJobVariable.getKey());
        Assert.assertEquals(expectedJobVariable.getValue(), actualJobVariable.getValue());
        Assert.assertEquals(expectedStageVariable.getKey(), actualStageVariable.getKey());
        Assert.assertEquals(expectedStageVariable.getValue(), actualStageVariable.getValue());
        Assert.assertEquals(expectedResultCollectionSize, actualResultCollectionSize);
    }

    @Test
    public void getOverriddenVariables_jobOverridesStage_twoObjects() {
        // Arrange
        List<EnvironmentVariable> pipelineVariables = new ArrayList<>();
        EnvironmentVariable expectedPipelineVariable = new EnvironmentVariable("pipelineKey", "pipelineValue");
        pipelineVariables.add(expectedPipelineVariable);

        List<EnvironmentVariable> stageVariables = new ArrayList<>();
        EnvironmentVariable expectedStageVariable = new EnvironmentVariable("jobKey", "stageValue");
        stageVariables.add(expectedStageVariable);

        List<EnvironmentVariable> jobVariables = new ArrayList<>();
        EnvironmentVariable expectedJobVariable = new EnvironmentVariable("jobKey", "jobValue");
        jobVariables.add(expectedJobVariable);

        int expectedResultCollectionSize = 2;

        // Act
        List<EnvironmentVariable> actualResult = this.environmentVariableService.getOverriddenVariables(jobVariables, stageVariables, pipelineVariables);
        EnvironmentVariable actualJobVariable = actualResult.get(0);
        EnvironmentVariable actualPipelineVariable = actualResult.get(1);
        int actualResultCollectionSize = actualResult.size();

        // Assert
        Assert.assertEquals(expectedJobVariable.getKey(), actualJobVariable.getKey());
        Assert.assertEquals(expectedJobVariable.getValue(), actualJobVariable.getValue());
        Assert.assertEquals(expectedPipelineVariable.getKey(), actualPipelineVariable.getKey());
        Assert.assertEquals(expectedPipelineVariable.getValue(), actualPipelineVariable.getValue());
        Assert.assertEquals(expectedResultCollectionSize, actualResultCollectionSize);
    }

    @Test
    public void getOverriddenVariables_jobOverridesStageAndPipeline_oneObject() {
        // Arrange
        List<EnvironmentVariable> pipelineVariables = new ArrayList<>();
        EnvironmentVariable expectedPipelineVariable = new EnvironmentVariable("jobKey", "pipelineValue");
        pipelineVariables.add(expectedPipelineVariable);

        List<EnvironmentVariable> stageVariables = new ArrayList<>();
        EnvironmentVariable expectedStageVariable = new EnvironmentVariable("jobKey", "stageValue");
        stageVariables.add(expectedStageVariable);

        List<EnvironmentVariable> jobVariables = new ArrayList<>();
        EnvironmentVariable expectedJobVariable = new EnvironmentVariable("jobKey", "jobValue");
        jobVariables.add(expectedJobVariable);

        int expectedResultCollectionSize = 1;

        // Act
        List<EnvironmentVariable> actualResult = this.environmentVariableService.getOverriddenVariables(jobVariables, stageVariables, pipelineVariables);
        EnvironmentVariable actualJobVariable = actualResult.get(0);
        int actualResultCollectionSize = actualResult.size();

        // Assert
        Assert.assertEquals(expectedJobVariable.getKey(), actualJobVariable.getKey());
        Assert.assertEquals(expectedJobVariable.getValue(), actualJobVariable.getValue());
        Assert.assertEquals(expectedResultCollectionSize, actualResultCollectionSize);
    }

    @Test
    public void getOverriddenVariables_emptyLists_noObjects() {
        // Arrange
        List<EnvironmentVariable> pipelineVariables = new ArrayList<>();
        List<EnvironmentVariable> stageVariables = new ArrayList<>();
        List<EnvironmentVariable> jobVariables = new ArrayList<>();

        int expectedResultCollectionSize = 0;

        // Act
        List<EnvironmentVariable> actualResult = this.environmentVariableService.getOverriddenVariables(jobVariables, stageVariables, pipelineVariables);
        int actualResultCollectionSize = actualResult.size();

        // Assert
        Assert.assertEquals(expectedResultCollectionSize, actualResultCollectionSize);
    }

    @Test
    public void replaceVariablesInExecTask_oneVariable_variableReplacedCorrectly() {
        // Arrange
        List<EnvironmentVariable> variables = new ArrayList<>();
        EnvironmentVariable variable = new EnvironmentVariable("first", "one");
        variables.add(variable);

        String arguments = String.format("Variable %%%s%%", variable.getKey());

        String expectedResult = String.format("Variable %s", variable.getValue());

        // Act
        String actualResult = this.environmentVariableService.replaceVariablesInArguments(variables, arguments);

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void replaceVariablesInExecTask_severalVariables_allVariablesReplacedCorrectly() {
        // Arrange
        List<EnvironmentVariable> variables = new ArrayList<>();
        EnvironmentVariable firstVariable = new EnvironmentVariable("first", "one");
        EnvironmentVariable secondVariable = new EnvironmentVariable("second", "two");
        EnvironmentVariable thirdVariable = new EnvironmentVariable("third", "three");
        variables.add(firstVariable);
        variables.add(secondVariable);
        variables.add(thirdVariable);

        String arguments = String.format(
                "Variable: %%%s%%, Variable: %%%s%%, Variable: %%%s%%",
                firstVariable.getKey(),
                secondVariable.getKey(),
                thirdVariable.getKey());

        String expectedResult = String.format(
                "Variable: %s, Variable: %s, Variable: %s",
                firstVariable.getValue(),
                secondVariable.getValue(),
                thirdVariable.getValue());

        // Act
        String actualResult = this.environmentVariableService.replaceVariablesInArguments(variables, arguments);

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void replaceVariablesInExecTask_oneVariableSeveralPlaces_allVariablesReplacedCorrectly() {
        // Arrange
        List<EnvironmentVariable> variables = new ArrayList<>();
        EnvironmentVariable firstVariable = new EnvironmentVariable("first", "one");
        variables.add(firstVariable);

        String arguments = String.format(
                "Variable: %%%s%%, Variable: %%%s%%, Variable: %%%s%%",
                firstVariable.getKey(),
                firstVariable.getKey(),
                firstVariable.getKey());

        String expectedResult = String.format(
                "Variable: %s, Variable: %s, Variable: %s",
                firstVariable.getValue(),
                firstVariable.getValue(),
                firstVariable.getValue());

        // Act
        String actualResult = this.environmentVariableService.replaceVariablesInArguments(variables, arguments);

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void replaceVariablesInExecTask_variableNotInList_noVariablesReplaced() {
        // Arrange
        List<EnvironmentVariable> variables = new ArrayList<>();
        EnvironmentVariable variableInArguments = new EnvironmentVariable("first", "one");
        EnvironmentVariable variableInList = new EnvironmentVariable("second", "two");
        variables.add(variableInList);

        String arguments = String.format("Variable %%%s%%", variableInArguments.getKey());

        String expectedResult = String.format("Variable %%%s%%", variableInArguments.getKey());

        // Act
        String actualResult = this.environmentVariableService.replaceVariablesInArguments(variables, arguments);

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void replaceVariablesInExecTask_variableNotMarkedAsSuch_noVariablesReplaced() {
        // Arrange
        List<EnvironmentVariable> variables = new ArrayList<>();
        EnvironmentVariable variable = new EnvironmentVariable("first", "one");
        variables.add(variable);

        String arguments = String.format("Variable %s", variable.getKey());

        String expectedResult = String.format("Variable %s", variable.getKey());

        // Act
        String actualResult = this.environmentVariableService.replaceVariablesInArguments(variables, arguments);

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }
}