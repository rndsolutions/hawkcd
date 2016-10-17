package net.hawkengine.services;

import net.hawkengine.db.DbRepositoryFactory;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.PipelineDefinitionRevision;
import net.hawkengine.model.Revision;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.interfaces.IRevisionService;

import java.util.List;

public class RevisionService extends CrudService<Revision> implements IRevisionService {
    private static final Class CLASS_TYPE = Revision.class;
    private static final Object lock = new Object();

    public RevisionService() {
        IDbRepository repository = DbRepositoryFactory.create(DATABASE_TYPE, CLASS_TYPE);
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
    }

    @Override
    public ServiceResult getAll() {
        return super.getAll();
    }

    @Override
    public ServiceResult add(Revision revision) {
        return super.add(revision);
    }

    @Override
    public ServiceResult update(Revision revision) {
        return super.update(revision);
    }

    public Revision getByPipelineDefinitionId(String pipelineDefinitionId) {
        Revision result = null;
        List<Revision> revisions = (List<Revision>) this.getAll().getObject();
        for (Revision revision : revisions) {
            if (revision.getPipelineDefinitionId().equals(pipelineDefinitionId)) {
                result = revision;
            }
        }

        return result;
    }

    public Revision addRevisionOfPipelineDefinition(PipelineDefinition pipelineDefinition) {
        synchronized (lock) {
            PipelineDefinitionRevision pipelineDefinitionRevision = new PipelineDefinitionRevision(pipelineDefinition);

            Revision revision = this.getByPipelineDefinitionId(pipelineDefinition.getId());
            if (revision != null) {
                revision.getRevisions().add(pipelineDefinitionRevision);
                this.update(revision);
            } else {
                revision = new Revision(pipelineDefinition.getId(), pipelineDefinition.getName());
                revision.getRevisions().add(pipelineDefinitionRevision);
                this.add(revision);
            }

            return revision;
        }
    }
}
