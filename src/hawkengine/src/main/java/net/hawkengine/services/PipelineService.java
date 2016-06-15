package net.hawkengine.services;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.Status;
import net.hawkengine.services.interfaces.IPipelineService;

import java.util.List;
import java.util.stream.Collectors;

public class PipelineService extends CrudService<Pipeline> implements IPipelineService {
    public PipelineService() {
        super.setRepository(new RedisRepository(Pipeline.class));
        super.setObjectType("Pipeline");
    }

    public PipelineService(IDbRepository repository) {
        super.setRepository(repository);
    }

    @Override
    public ServiceResult getById(String pipelineId) {
        return super.getById(pipelineId);
    }

    @Override
    public ServiceResult getAll() {
        return super.getAll();
    }

    @Override
    public ServiceResult add(Pipeline pipeline) {
        return super.add(pipeline);
    }

    @Override
    public ServiceResult update(Pipeline pipeline) {
        return super.update(pipeline);
    }

    @Override
    public ServiceResult delete(String pipelineId) {
        return super.delete(pipelineId);
    }

    @Override
    public ServiceResult getAllUpdatedPipelines() {
        List<Pipeline> pipelines = (List<Pipeline>) this.getAll().getObject();
        pipelines = pipelines
                .stream()
                .filter(p -> (p.getStatus() == Status.IN_PROGRESS) && p.areMaterialsUpdated() && !p.isPrepared())
                .sorted((p1, p2) -> p1.getStartTime().compareTo(p2.getStartTime()))
                .collect(Collectors.toList());

        ServiceResult result = new ServiceResult();
        result.setObject(pipelines);
        result.setMessage("Updated" + this.objectType + "s retrieved successfully.");

        return result;
    }

    @Override
    public ServiceResult getAllPreparedPipelines() {
        List<Pipeline> pipelines = (List<Pipeline>) this.getAll().getObject();
        pipelines = pipelines
                .stream()
                .filter(p -> (p.getStatus() == Status.IN_PROGRESS) && p.areMaterialsUpdated() && p.isPrepared())
                .sorted((p1, p2) -> p1.getStartTime().compareTo(p2.getStartTime()))
                .collect(Collectors.toList());

        ServiceResult result = new ServiceResult();
        result.setObject(pipelines);
        result.setMessage("Prepared" + this.objectType + "s retrieved successfully.");

        return result;
    }
}
