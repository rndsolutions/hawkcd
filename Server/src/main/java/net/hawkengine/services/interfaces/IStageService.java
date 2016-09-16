package net.hawkengine.services.interfaces;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.Stage;
import net.hawkengine.model.dto.StageDto;


public interface IStageService extends ICrudService<Stage> {

    ServiceResult addStageWithSpecificJobs(StageDto stageDto);

}
