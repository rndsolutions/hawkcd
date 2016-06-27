package net.hawkengine.services;

import net.hawkengine.model.ExecTask;
import net.hawkengine.model.FetchArtifactTask;
import net.hawkengine.model.FetchMaterialTask;
import net.hawkengine.model.JobDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.TaskDefinition;
import net.hawkengine.model.UploadArtifactTask;
import net.hawkengine.model.enums.TaskType;
import net.hawkengine.services.interfaces.IJobDefinitionService;
import net.hawkengine.services.interfaces.ITaskDefinitionService;

import java.util.ArrayList;
import java.util.List;

public class TaskDefinitionService extends CrudService<TaskDefinition> implements ITaskDefinitionService {
    private IJobDefinitionService jobDefinitionService;
    private String successMessage = "retrieved successfully";
    private String failureMessage = "not found";

    public TaskDefinitionService() {
        super.setObjectType("TaskDefinition");
        this.jobDefinitionService = new JobDefinitionService();
    }

    public TaskDefinitionService(IJobDefinitionService jobDefinitionService) {
        super.setObjectType("TaskDefinition");
        this.jobDefinitionService = jobDefinitionService;
    }

    @Override
    public ServiceResult getById(String taskDefinitionId) {
        List<JobDefinition> jobDefinitions = (List<JobDefinition>) this.jobDefinitionService.getAll().getObject();
        TaskDefinition result = null;

        for (JobDefinition jobDefinition : jobDefinitions) {
            List<TaskDefinition> taskDefinitions = jobDefinition.getTaskDefinitions();
            for (TaskDefinition taskDefinition : taskDefinitions) {
                if (taskDefinition.getId().equals(taskDefinitionId)) {
                    result = taskDefinition;
                    return super.createServiceResult(result, false, this.successMessage);
                }
            }
        }

        return super.createServiceResult(result, true, this.failureMessage);
    }

    @Override
    public ServiceResult getAll() {
        List<JobDefinition> jobDefinitions = (List<JobDefinition>) this.jobDefinitionService.getAll().getObject();
        List<TaskDefinition> taskDefinitions = new ArrayList<>();

        for (JobDefinition jobDefinition : jobDefinitions) {
            List<TaskDefinition> taskJobDefinitions = jobDefinition.getTaskDefinitions();
            taskDefinitions.addAll(taskJobDefinitions);
        }

        return super.createServiceResultArray(taskDefinitions, false, this.successMessage);
    }

    @Override
    public ServiceResult add(ExecTask taskDefintion) {
        ServiceResult result = this.addTask(taskDefintion);
        return result;
    }

    @Override
    public ServiceResult add(FetchMaterialTask taskDefintion) {
        ServiceResult result = this.addTask(taskDefintion);
        return result;
    }

    @Override
    public ServiceResult add(FetchArtifactTask taskDefinition) {
        ServiceResult result = this.addTask(taskDefinition);
        return result;
    }

    @Override
    public ServiceResult add(UploadArtifactTask taskDefinition) {
        ServiceResult result = this.addTask(taskDefinition);
        return result;
    }

    public ServiceResult addTask(TaskDefinition taskDefinition) {
        JobDefinition jobDefinition = (JobDefinition) this.jobDefinitionService.getById(taskDefinition.getJobDefinitionId()).getObject();
        List<TaskDefinition> taskDefinitions = jobDefinition.getTaskDefinitions();
        boolean hasNameCollision = this.checkForNameCollision(taskDefinitions, taskDefinition);
        if (hasNameCollision) {
            return super.createServiceResult(taskDefinition, true, "with the same name exists");
        }

        taskDefinitions.add(taskDefinition);
        jobDefinition.setTaskDefinitions(taskDefinitions);
        JobDefinition updatedJobDefinition = (JobDefinition) this.jobDefinitionService.update(jobDefinition).getObject();

        TaskDefinition result = this.extractTaskDefinitionFromJobDefinition(updatedJobDefinition, taskDefinition.getId());
        if (result == null) {
            return super.createServiceResult(result, true, "not added successfully");
        }

        return super.createServiceResult(result, false, "added successfully");
    }

    @Override
    public ServiceResult update(ExecTask taskDefintion) {
        ServiceResult result = this.updateTask(taskDefintion);
        return result;
    }

    @Override
    public ServiceResult update(FetchMaterialTask taskDefintion) {
        ServiceResult result = this.updateTask(taskDefintion);
        return result;
    }

    @Override
    public ServiceResult update(FetchArtifactTask taskDefinition) {
        ServiceResult result = this.updateTask(taskDefinition);
        return result;
    }

    @Override
    public ServiceResult update(UploadArtifactTask taskDefinition) {
        ServiceResult result = this.updateTask(taskDefinition);
        return result;
    }

    public ServiceResult updateTask(TaskDefinition taskDefinition) {
        TaskDefinition result = null;

        JobDefinition jobDefinition = (JobDefinition) this.jobDefinitionService.getById(taskDefinition.getJobDefinitionId()).getObject();
        List<TaskDefinition> taskDefinitions = jobDefinition.getTaskDefinitions();
        boolean hasNameCollision = this.checkForNameCollision(taskDefinitions, taskDefinition);
        if (hasNameCollision) {
            return super.createServiceResult(taskDefinition, true, "with the same name exists");
        }

        int lengthOfTaskDefinitions = taskDefinitions.size();
        for (int i = 0; i < lengthOfTaskDefinitions; i++) {
            TaskDefinition definition = taskDefinitions.get(i);
            if (definition.getId().equals(taskDefinition.getId())) {
                Class resultTaskClass = this.getTaskDefinitionType(taskDefinition);
                try {
                    result = (TaskDefinition) resultTaskClass.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }

                taskDefinitions.set(i, taskDefinition);
                jobDefinition.setTaskDefinitions(taskDefinitions);
                JobDefinition updatedJobDefinition = (JobDefinition) this.jobDefinitionService.update(jobDefinition).getObject();
                result = this.extractTaskDefinitionFromJobDefinition(updatedJobDefinition, taskDefinition.getId());
                break;
            }
        }

        if (result == null) {
            return super.createServiceResult(result, true, "not found");
        }

        return super.createServiceResult(result, false, "updated successfully");
    }

    @Override
    public ServiceResult delete(String taskDefinitionId) {
        boolean isRemoved = false;
        TaskDefinition taskDefinitionToDelete = (TaskDefinition) this.getById(taskDefinitionId).getObject();
        if (taskDefinitionToDelete == null) {
            return super.createServiceResult(taskDefinitionToDelete, true, "does not exists");
        }

        JobDefinition jobDefinition = (JobDefinition) this.jobDefinitionService
                .getById(taskDefinitionToDelete.getJobDefinitionId())
                .getObject();
        List<TaskDefinition> taskDefinitions = jobDefinition.getTaskDefinitions();

        int lengthOfTaskDefinitions = taskDefinitions.size();
        if (lengthOfTaskDefinitions > 1) {
            for (int i = 0; i < lengthOfTaskDefinitions; i++) {
                TaskDefinition definition = taskDefinitions.get(i);
                if (definition.getId().equals(taskDefinitionToDelete.getId())) {
                    taskDefinitions.remove(definition);
                    isRemoved = true;
                    break;
                }
            }
        } else {
            return super.createServiceResult(taskDefinitionToDelete, true, "cannot delete the last job definition");
        }

        if (!isRemoved) {
            return super.createServiceResult(taskDefinitionToDelete, true, "not deleted");
        }

        jobDefinition.setTaskDefinitions(taskDefinitions);
        JobDefinition updatedJobDefinition = (JobDefinition) this.jobDefinitionService.update(jobDefinition).getObject();
        TaskDefinition result = this.extractTaskDefinitionFromJobDefinition(updatedJobDefinition, taskDefinitionId);
        if (result != null) {
            return super.createServiceResult(result, true, "not deleted successfully");
        }

        return super.createServiceResult(result, false, "deleted successfully");
    }

    /**
     * Method void for extracting TaskDefinitions from JobDefinition provided. Fills in a provided
     * List.
     */
    private void extractTaskDefinitionsFromJobDefinitions(List<JobDefinition> jobDefinitions, List<TaskDefinition> taskDefinitions) {
        for (JobDefinition jobDefinition : jobDefinitions) {
            List<TaskDefinition> taskDefinitionsList = jobDefinition.getTaskDefinitions();
            taskDefinitions.addAll(taskDefinitionsList);
        }
    }

    /**
     * Method boolean accepts a list of TaskDefinitions, performs name check and decides wheather it
     * has name collision or not.
     */
    private boolean checkForNameCollision(List<TaskDefinition> taskDefinitions, TaskDefinition taskDefinitionToAdd) {
        for (TaskDefinition taskDefinition : taskDefinitions) {
            if (taskDefinition.getId().equals(taskDefinitionToAdd.getId())) {
                continue;
            }
            if (taskDefinition.getName().equals(taskDefinitionToAdd.getName())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Method return TaskDefinition, accepts JobDefinition and TaskDefinition Id, filters all
     * TaskDefinitions in the provided job. Returns null if no TaskDefinition with provided id is
     * found.
     */
    private TaskDefinition extractTaskDefinitionFromJobDefinition(JobDefinition jobDefinition, String taskDefinitionId) {
        TaskDefinition result = jobDefinition
                .getTaskDefinitions()
                .stream()
                .filter(td -> td.getId().equals(taskDefinitionId))
                .findFirst()
                .orElse(null);

        return result;
    }

    private Class<?> getTaskDefinitionType(TaskDefinition taskDefinition) {
        TaskType taskDefinitionType = taskDefinition.getType();
        Class result = null;
        switch (taskDefinitionType) {
            case EXEC:
                result = ExecTask.class;
                break;
            case FETCH_ARTIFACT:
                result = FetchArtifactTask.class;
                break;
            case FETCH_MATERIAL:
                result = FetchMaterialTask.class;
                break;
            case UPLOAD_ARTIFACT:
                result = UploadArtifactTask.class;
                break;
        }

        return result;
    }
}