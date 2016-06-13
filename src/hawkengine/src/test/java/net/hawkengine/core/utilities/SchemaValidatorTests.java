package net.hawkengine.core.utilities;
import net.hawkengine.core.SchemaValidator;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.PipelineGroup;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by boris on 10.06.16.
 */
public class SchemaValidatorTests {
    private String message = "OK";
    private SchemaValidator validator = new SchemaValidator();
    private  PipelineGroup ppgroup;

    @Test
    public void validate_patern_missmatch(){
        PipelineGroup groupToTest = new PipelineGroup();
        groupToTest.setName("Peshogroup");
        Object resultMessage = validator.validate(ppgroup);
        Assert.assertEquals(message,resultMessage);
    }



}
