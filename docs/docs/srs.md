HawkCD - SRS
=============
Requirements Specification

***
Prepared by:
***

**Margarita Ivancheva**

**Vladislav Nikolov**

**Simeon Petrov**

**Mariyan Stefanov**

**Radoslav Minchev**

***
**Version 1.0 approved**

**April 15, 2016**

***

## Table of Contents

> [*Introduction*](#introduction)
>
> [**Use Cases**](#manage-pipeline-group-001)
>
> > [*Manage Pipeline Group (\#001)*](#manage-pipeline-group-001)
>
> >[*Manage Pipeline (\#002)*](#manage-pipeline-002)
>
> >[*Manage Stage (\#003)*](#manage-stage-003)
>
> >[*Manage Job (\#004)*](#manage-job-004)
>
> >[*Manage Task (\#005)*](#manage-task-005)
>
> >[*Manage Material (\#006)*](#manage-material-006)
>
> >[*Manage Agent (\#007)*](#manage-agent-007)
>
> >[*Manage Environment Variable (\#008)*](#manage-environment-variable-008)
>
> >[*Manage Environment (\#009)*](#manage-environment-009)
>
> >[*Manage Authentication and Authorization (\#010)*](#manage-authentication-and-authorization-010)
>
> >[*Use Case \#001 - Manage Pipeline Group*](#h.l65b7x4o2d22)
>
>[**Functional requirements**](#h.l65b7x4o2d22)


Introduction
--

### Purpose

The current SRS document tries to define the MVP (Minimum Viable Product) scope for the Hawkengine System targeted to be released as version 1 and elaborate in details the product requirements.

### Intended Audience and Reading Suggestions

The intended audience of this document is all members directly or indirectly involved in the project: Developers, Quality Assurance Engineers, Product and Project Managers and Marketing staff. The document should be read from top to bottom to understand the overall concept the Hawkengine System implements. Each use case is self-contained. It details one or many features of the system related to a logical area of the Application, e.g. “Pipeline Management” and “Authentication & Authorization”.


### Product Perspective

The Hawkengine System intends to be a one stop solution for managing Continuous Delivery processes for the Enterprise. It will enable and encourage collaboration between delivery team members including but not limited to Developers, QA, Ops and Release Managers.

### User Classes and Characteristics

-   Developer

-   Quality Assurance Engineer

-   DevOps Engineer

-   Operations Engineer

-   Release Manager

### Operating Environment

The System runs on all Operating Systems that support the JVM.

The main testing will be performed under the following Operating Systems:

-   Windows Server 2012 and above

-   Ubuntu 14.04 LTS


##
***

### Brief Description

This use case allows an Administrator to create a new Pipeline Group. He/she can also modify or delete a certain Pipeline Group, depending on the intention.

### Actors

*Primary Actor - Administrator*

### Flow of Events

#### Basic Flow
***

#####  __Log On__

> This use case starts when an Administrator accesses the Hawkengine System. The Administrator enters an email and password and the system authenticates them.

##### __Create Pipeline Group__

> The system provides the operations available to the Administrator. These operations are: Create, Modify and Delete Pipeline Group. The Administrator chooses “Create Pipeline Group” option.

##### __Submit the Pipeline Group__

> The system provides an option for the Administrator to enter a name for the new Pipeline Group. The Administrator can modify the name as desired until choosing to submit the Pipeline Group. At this point the system validates the entered value and saves the Pipeline Group. The use case ends.

#### Alternative Flows
***

##### __Modify Pipeline Group__

> At basic flow step [***Create Pipeline Group***](#create-pipeline-group), the Administrator already has a Pipeline Group that has been created. The system retrieves and displays the Pipeline Group on focus and allows him/her to use it as a starting point. The use case resumes at basic flow step [***Submit the Pipeline Group***](#submit-the-pipeline-group).

##### __Delete Pipeline Group__

> At basic flow step [***Create Pipeline Group***](#create-pipeline-group), the Administrator already has a Pipeline Group created and chooses to delete it. This option is available to the Administrator only when the Pipeline Group does not contain any Pipelines. The system deletes the Pipeline Group. The use case ends.

##### __Unauthorized User__

> At basic flow step [***Log On***](#log-on), the system establishes whether the User is authorized for such operations. There are three basic cases when the user is denied access:

##### __Operator__

> In cases, when the user is with Operator permission status, he/she is not able to create, modify or delete Pipeline Groups. He/she has the same accessibility level as Viewer.

##### __Viewer__

> In cases, when the user is with Viewer permission status, he/she is not able to create, modify or delete Pipeline Groups. He/she can only observe the specific Pipeline Group, if he/she is within one of the following groups:

##### __Server Viewer__

> The user has global view access.

##### __Pipeline Group Viewer__

> The user can observe the specific Pipeline Group and its successors.

##### __Unauthenticated User__

> In cases, when the user’s credentials are not valid, a proper error message displays and the use case ends.
>
>  For further explanation regarding authentication and authorization, please refer to [***Authentication and Authorization Section***](#manage-authentication-and-authorization-010).*

##### __Quit__

> The system allows the Administrator to quit at any time during the use case. The Administrator chooses not to save any of the entered information or decides to switch to another view, without submitting the changes, all the entered information will be discarded. The use case ends.

## Manage Pipeline (\#002)
***

### Overview

A Pipeline consists of multiple Stages, each of which will be run in order. If a Stage fails, then the Pipeline is considered failed and the rest of the stages will not be started.

*Pipeline status: *

-   Passed

-   Failed

-   In progress

-   Paused

### Brief Description

This use case allows an Administrator to create a new Pipeline. He/she can also modify or delete a certain Pipeline, depending on the intention. Last, but not least, the Administrator is able to run the Pipeline with latest Materials or with specific Materials. After that he/she can pause or stop the Pipeline.

### Actors

*Primary Actor - Administrator*

### Flow of Events

1.  #### Basic Flow

    1.  <span id="id.z8katc9sp0ac" class="anchor"></span>**Log On**

> This use case starts when an Administrator accesses the Hawkengine System. The Administrator enters an email and password and the system authenticates them.

1.  <span id="id.yx4inewmvuj" class="anchor"></span>**Create Pipeline**

> The system provides the operations available to the Administrator. These operations are: Create, Modify or Delete Pipeline. The Administrator chooses “Create Pipeline” option.

1.  <span id="id.9ppjrs3j6tg7" class="anchor"></span>**Fill the Pipeline Options**

> The system provides the Administrator with a set of fields to fill. The Administrator can add and modify the name of the Pipeline, as well as the Stage and Materials.

1.  <span id="id.egihmj11mfkr" class="anchor"></span>**Submit the Pipeline**

> The Administrator indicates that the operation based on creating a new Pipeline is completed. The system validates all of the entered values and saves the Pipeline. The use case ends.

1.  #### Alternative Flows

    1.  **Modify Pipeline**

> At basic flow step [***Create Pipeline***](#id.yx4inewmvuj), the Administrator already has a Pipeline that has been created. The system retrieves and displays the Pipeline on focus. The Administrator has a set of options to choose from:

1.  **General Options**

> The Administrator can modify the general options of the selected pipeline.

1.  **Material Options**

> The Administrator can add, modify or delete an existing Material.

1.  **Stage Options**

> The Administrator can add, modify or delete an existing Stage.

1.  **Environment Variable Options**

> The Administrator can add, modify or delete an existing Environment Variable.

1.  **Lock Option**

> The Administrator has the option to lock a specific Pipeline. He/she might consider this оption if he/she wants to make sure that a single instance of a pipeline can run at a time, especially when the Stages of the Pipeline are related to one another. The system registers the changes and initializes the process of locking the Pipeline. The use case resumes at **[*Submit the Pipeline*](#id.egihmj11mfkr)** basic flow.
>
> The Administrator can modify all of the options as desired until choosing to submit the changes. At this point the system saves the updated Pipeline and the use case ends. The use case resumes at basic flow step [***Submit the Pipeline***](#id.egihmj11mfkr).

1.  **Delete Pipeline**

> At basic flow step [***Create Pipeline***](#id.yx4inewmvuj), the Administrator already has a Pipeline created and chooses to delete it. The system warns the user that he/she is about to delete a Pipeline. The Administrator confirms the deletion. The system deletes the Pipeline, as well as the Pipeline’s runs. However, the Pipeline’s artifacts are not removed, since there can be interrelation with other pipelines. The use case ends.

1.  <span id="id.yql120sdkl07" class="anchor"></span>**Run Pipeline**

> At basic flow step [***Create Pipeline***](#id.yx4inewmvuj), the Administrator already has a Pipeline and chooses to run it. The system provides a set of options for the Administrator to choose from for triggering the run of the pipeline:

1.  **Automatic Trigger**

> By default, the server is set to automatically trigger pipelines. Whenever a Material from this Pipeline is updated, the pipeline run is triggered.

1.  **Manual Trigger**

> When the Pipeline is manually triggered, the server obligatorily checks for the latest Materials and schedules the Pipeline. The Administrator is able to:

1.  **Run Pipeline**

> The Administrator sets the Pipeline to begin build activity.

1.  **Run Pipeline-specific**

> The Administrator decides which specific Materials will be used to run the Pipeline, and triggers it.
>
> During each of the triggered processes the Administrator can:

1.  **Pause Pipeline**

> The Administrator pauses the scheduled Pipeline. At this point the system prevents any new pipeline activity, while letting the Pipeline proceed until it completes the job currently “running”.

1.  **Stop Pipeline**

> The Administrator stops the scheduled Pipeline.

1.  **Unauthorized User**

> At basic flow step [***Log On***](#id.z8katc9sp0ac), the system establishes whether the User is authorized for such operations. There are three basic occasions when the user is denied access:

1.  **Operator**

> In cases, when the user is with Operator permission status, he/she is not able to create, modify or delete Pipelines. He/she has the same accessibility level as Viewer and can perform alternative flow **[*Run Pipeline*](#id.yql120sdkl07).**

1.  **Viewer**

> In cases, when the user is with Viewer permission status, he/she is not able to create, modify, delete or run Pipelines. He/she can only observe the specific Pipeline, if he/she is within one of the following groups:

1.  **Server Viewer**

> The user has global view access.

1.  **Pipeline Group Viewer**

> The user can observe the specific Pipeline Group and its successors.

1.  **Pipeline Viewer**

> The user can observe the specific Pipeline and its successors.

1.  **Environment Viewer**

> The user can observe the specific Pipeline and its successors, if the current Pipeline is within the Environment.

1.  **Unauthenticated User**

> In cases, when the user’s credentials are not valid, a proper error message displays and the use case ends.
>
> *For further explanation regarding authentication and authorization, please refer to [***Authentication and Authorization Section***](#manage-authentication-and-authorization-010).*

1.  **Quit**

> The system allows the Administrator to quit at any time during the use case. The Administrator chooses not to save any of the entered information or decides to switch to another view, without submitting the changes, all the entered information will be discarded. The use case ends.

1.  **Invalid Name**

> At basic flow step [***Create Pipeline***](#id.yx4inewmvuj) the system determines that the name of the Pipeline is not valid and displays an error message. The use case ends.

Manage Stage (\#003)
--------------------

### Overview

A Stage consists of multiple jobs, each of which can run independently of the others. This means that Hawkengine can and does parallelize the runs of Jobs in a Stage. If a Job fails, then the Stage is considered failed. However, since Jobs are independent of each other, all other Jobs in the Stage will also be run.

*Stage Status:*

-   Passed

-   Failed

-   In progress

-   Paused

### Brief Description

This use case allows an Administrator to create a new Stage. He/she can also modify or delete a certain Stage, depending on the intention. Last, but not least, the Administrator is able to run the Stage with all Jobs or specific Jobs.

### Actors

*Primary Actor - Administrator*

### Flow of Events

1.  #### Basic Flow

    1.  <span id="id.ru09shgxqwr6" class="anchor"></span>**Log On**

> This use case starts when an Administrator accesses the Hawkengine System. The Administrator enters an email and password and the system validates them.

1.  <span id="id.4z8t2f4b0yp0" class="anchor"></span>**Choose Pipeline**

> The Administrator chooses in which Pipeline to create a Stage.

1.  <span id="id.k0eytt69m4ce" class="anchor"></span>**Create Stage**

> The system provides the operations available to the Administrator to create a new Stage within the chosen Pipeline. These operations are: Create, Modify or Delete Stage. The Administrator chooses “Create Stage” option.

1.  <span id="id.f883kc1j2tsi" class="anchor"></span>**Fill the Stage Options**

> The system provides the Administrator with a set of fields to fill. The Administrator can add and modify the name of the Stage, the type of the Task, as well as to add Job to the Stage.

1.  <span id="id.mqffxqqq50xl" class="anchor"></span>**Submit the Stage**

> The Administrator indicates that the operation based on creating a new Stage is completed. The system validates all of the entered values and saves the Stage. The use case ends.

1.  #### Alternative Flows

    1.  **Modify a Stage**

> At basic flow step **[*Create Stage*](#id.k0eytt69m4ce),** the Administrator already has a Stage that has been created. The system retrieves and displays the Stage on focus. The Administrator has a set of options to choose from:

1.  **Job Options**

> The Administrator can modify the Job name.

1.  **Task Options**

> The Administrator can add, modify or delete an existing Task.

1.  **Environment Variable Options**

> The Administrator can add, modify or delete an existing Environment Variable.
>
> The Administrator can modify all of the options as desired until choosing to submit the changes. At this point the system saves the updated Stage and the use case ends. The use case resumes at basic flow step [***Submit the Stage***](#id.mqffxqqq50xl).

1.  **Delete a Stage.**

> At basic flow step [***Create Stage***](#id.k0eytt69m4ce), the Administrator already has a Stage and chooses to delete it. The system warns the user that he/she is about to delete a Stage. The Administrator confirms the deletion. The system deletes the Stage, without removing the stage runs information, since it might be used as reference to previous runs. The use case ends.

1.  **Run**<span id="id.otzjesmzknhz" class="anchor"></span> **Stage.**

> At basic flow step [***Create Stage***](#id.k0eytt69m4ce), the Administrator already has a Stage and chooses to run it. The system provides a set of options for the Administrator to choose from for running the Stage:

1.  **Run Stage**

> The Administrator sets the Stage to begin running an activity

1.  **Run Stage-specific**

> The Administrator decides which specific Jobs will be used to run the Stage, and triggers it.

1.  **Unauthorized User**

> At basic flow step [***Log On***](#id.ru09shgxqwr6), the system establishes whether the User is authorized for such kind of operations. There are three basic occasions when the user is denied access:

1.  **Operator**

> In cases, when the user is with Operator permission status, he/she is not able to create, modify or delete Stages. He/she has the same accessibility level as Viewer and can perform alternative flow **[*Run Stage*](#id.otzjesmzknhz).**

1.  **Viewer**

> In cases, when the user is with Viewer permission status, he/she is not able to create, modify, delete or run Stages. He/she can only observe the specific Stage, if he/she is within one of the following groups:

1.  **Server Viewer**

> The user has global view access.

1.  **Pipeline Group Viewer**

> The user can observe the specific Pipeline Group and its successors.

1.  **Pipeline Viewer**

> The user can observe the specific Pipeline and its successors.

1.  **Environment Viewer**

> The user can observe the specific Pipeline and its successors, if the current Pipeline is within the Environment.

1.  **Unauthenticated User**

> At basic flow step [***Log On***](#id.ru09shgxqwr6), the system establishes whether the Administrator is valid. In case, he/she is not, a proper error message displays and the use case ends.
>
> *For further explanation regarding authentication and authorization, please refer to [***Authentication and Authorization Section***](#manage-authentication-and-authorization-010).*

1.  **Quit**

> The system allows the Administrator to quit at any time during the use case. The Administrator chooses not to save any of the entered information or decides to switch to another view, without submitting the changes, all the entered information will be discarded. The use case ends.

1.  **Invalid Name**

> At basic flow step [***Create Stage***](#id.k0eytt69m4ce) the system determines that the name of the Stage is not valid and displays an error message. The use case ends.

Manage Job (\#004)
------------------

### Overview

A job consists of multiple Tasks, each of which will be run in order. If a Task in a Job fails, then the Job is considered failed, and unless specified otherwise, the rest of the Tasks in the Job will not be run.

Job Status:

-   Passed

-   Failed

-   Awaiting - awaiting agent assignment

-   Scheduled - job assigned for execution

-   Running - executing

### Brief Description

This use case allows an Administrator to create a new Job. He/she can also modify or delete a certain Job, depending on the intention.

### Actors

*Primary Actor - Administrator*

### Flow of Events

1.  #### Basic Flow

    1.  <span id="id.xbls692jm7el" class="anchor"></span>**Log On**

> This use case starts when an Administrator accesses the Hawkengine System. The Administrator enters an email and password and the system validates them.

1.  <span id="id.pf2nzkndqis" class="anchor"></span>**Choose Pipeline**

> The Administrator chooses in which Pipeline to operate.

1.  <span id="id.bjnm51w7fj8i" class="anchor"></span>**Choose Stage**

> The Administrator chooses in which Stage to create a Job.

1.  <span id="id.14zi23shcca1" class="anchor"></span>**Create Job**

> The system provides the operations available to the Administrator to create a new Job within the chosen Stage. These operations are: Create, Modify or Delete Job. The Administrator chooses “Create Job” option.

1.  <span id="id.zdm1ick1c4j6" class="anchor"></span>**Fill the Job Options**

> The system provides the Administrator with set of fields to fill. The Administrator can add and modify the name of the Job, as well as the type of the Task.

1.  <span id="id.6qdfjfz4l806" class="anchor"></span>**Submit the Job**

> The Administrator indicates that the operation based on creating a new Job is completed. The system validates all of the entered values and saves the Job. The use case ends.

1.  #### Alternative Flows

    1.  **Modify Job**

> At basic flow step **[*Create Job*](#id.14zi23shcca1),** the Administrator already has a Job that has been created. The system retrieves and displays the Job on focus. The Administrator has a set of options to choose from:

1.  **Job Options**

> The Administrator can modify the Job name.

1.  **Task Options**

> The Administrator can add, modify or delete an existing Task.

1.  **Environment Variable Options**

> The Administrator can add, modify or delete an existing Environment Variable.
>
> The Administrator can modify all of the options as desired until choosing to submit the changes. At this point the system saves the updated Job and the use case ends. The use case resumes at basic flow step [***Submit the Job***](#id.6qdfjfz4l806).

1.  **Delete Job.**

> At basic flow step [***Create Job***](#id.14zi23shcca1), the Administrator already has a Job and chooses to delete it. The system warns the user that he/she is about to delete a Job. The Administrator confirms the deletion. The system deletes the Job. The use case ends.

1.  **Unauthorized User**

> At basic flow step [***Log On***](#id.xbls692jm7el), the system establishes whether the User is authorized for such operations. There are three basic occasions when the user is denied access:

1.  **Operator**

> In cases, when the user is with Operator permission status, he/she is not able to create, modify or delete Jobs. He/she has the same accessibility level as Viewer.

1.  **Viewer**

> In cases, when the user is with Viewer permission status, he/she is not able to create, modify or delete Jobs. He/she can only observe the specific Job, if he/she is within one of the following groups:

1.  **Server Viewer**

> The user has global view access.

1.  **Pipeline Group Viewer**

> The user can observe the specific Pipeline Group and its successors.

1.  **Pipeline Viewer**

> The user can observe the specific Pipeline and its successors.

1.  **Environment Viewer**

> The user can observe the specific Pipeline and its successors, if the current Pipeline is within the Environment.

1.  **Unauthenticated User**

> At basic flow step [***Log On***](#id.xbls692jm7el), the system establishes whether the Administrator is valid. In case, he/she is not, a proper error message displays and the use case ends.
>
> *For further explanation regarding authentication and authorization, please refer to [***Authentication and Authorization Section***](#manage-authentication-and-authorization-010).*

1.  **Quit**

> The system allows the Administrator to quit at any time during the use case. The Administrator chooses not to save any of the entered information or decides to switch to another view, without submitting the changes, all the entered information will be discarded. The use case ends.

1.  **Invalid Name**

> At basic flow step Create Job the system determines that the name of the Job is not valid and displays an error message. The use case ends.

Manage Task (\#005)
-------------------

### Overview

A task is an action that needs to be performed. Usually, it is a single command.

### Brief Description

This use case allows an Administrator to create a new Task. He/she can also modify or delete a certain Task, depending on the intention.

### Actors

*Primary Actor - Administrator*

### Flow of Events

1.  #### Basic Flow

    1.  <span id="id.ufvvkpz8pcyg" class="anchor"></span>**Log On**

> This use case starts when an Administrator accesses the Hawkengine System. The Administrator enters an email and password and the system authenticates them.

1.  <span id="id.jiczgoa1m8m3" class="anchor"></span>**Choose Pipeline**

> The Administrator chooses in which Pipeline to operate.

1.  <span id="id.9etqitc27as9" class="anchor"></span>**Choose Stage**

> The Administrator chooses in which Stage to operate.

1.  <span id="id.pf8oxxyn8f7j" class="anchor"></span>**Choose Job**

> The Administrator chooses in which Job to create a new Task.

1.  <span id="id.wi8010v1g3ki" class="anchor"></span>**Create Task**

> The system provides the operations available to the Administrator to create a new Task within the chosen Job. These operations are: Create, Modify or Delete Task. The Administrator chooses “Create Task” option.

1.  <span id="id.23p6og4lftt" class="anchor"></span>**Fill the Task Options**

> The system provides the Administrator with a set of fields to fill. The Administrator can modify the type of the Task. The system provides a list with different Task types. The Administrator chooses Exec Task. The system provides a set of fields that should be filled.

1.  <span id="id.vgocw45k2y93" class="anchor"></span>**Submit the Task**

> The Administrator indicates that the operation based on creating a new Task is completed. The system validates all of the entered values and saves the Task. The use case ends.

1.  #### Alternative Flows

    1.  <span id="id.krvt6rcxbxn7" class="anchor"></span>**Modify a Task**

> At basic flow step **[*Create Task*](#id.wi8010v1g3ki),** the Administrator already has a Task that has been created. The system retrieves and displays the Task on focus. The Administrator has a set of options to choose from:

1.  **Task Command**

> The Administrator can modify the Task command.

1.  **Arguments**

> The Administrator can modify the arguments.

1.  **Working Directory**

> The Administrator can modify the working directory.

1.  **Run Condition**

> The Administrator can modify the condition under which the Task will run.

1.  **Ignore Errors**

> The Administrator can check the option to ignore errors.
>
> The Administrator can modify all of the options as desired until choosing to submit the changes. At this point the system saves the updated Task and the use case ends. The use case resumes at basic flow step [***Submit the Task***](#id.vgocw45k2y93).

1.  **Delete a Task**

> At basic flow step [***Create Task***](#id.wi8010v1g3ki), the Administrator already has a Task and chooses to delete it. The system warns the user that he/she is about to delete a Task. The Administrator confirms the deletion. The system deletes the Task. The use case ends.

1.  <span id="id.aagdha2662bh" class="anchor"></span>**Task Type**

> At basic flow step [***Create Task***](#id.wi8010v1g3ki), the Administrator has the option to choose the Task type. There are four different options:

1.  **Exec Task**

> Creates an Execution Task with command, arguments, working directory, run condition and ignore errors options.

1.  **Fetch Artifact Task**

> Creates a Fetch Artifact Task with specific Pipeline, Stage, Job, source, destination and run condition.

1.  **Fetch Material Task**

> Creates a Fetch Material Task with specific Material and run condition.

1.  **Upload Artifact Task**

> Creates a Upload Artifact Task with specific source, destination and run condition.

1.  **Modify Task Change**

> At alternative flow step [***Modify Task***](#id.krvt6rcxbxn7), the system provides different options depending on which Task type the Administrator has created. The options are based on alternative flow step [***Task Type***](#id.aagdha2662bh).

1.  **Unauthorized User**

> At basic flow step [***Log On***](#id.ufvvkpz8pcyg), the system establishes whether the User is authorized for such operations. There are three basic occasions when the user is denied access:

1.  **Operator**

> In cases, when the user is with Operator permission status, he/she is not able to create, modify or delete Tasks. He/she has the same accessibility level as Viewer**.**

1.  **Viewer**

> In cases, when the user is with Viewer permission status, he/she is not able to create, modify or delete Tasks. He/she can only observe the specific Task, if he/she is within one of the following groups:

1.  **Server Viewer**

> The user has global view access.

1.  **Pipeline Group Viewer**

> The user can observe the specific Pipeline Group and its successors.

1.  **Pipeline Viewer**

> The user can observe the specific Pipeline and its successors.

1.  **Environment Viewer**

> The user can observe the specific Pipeline and its successors, if the current Pipeline is within the Environment.

1.  **Unauthenticated User**

> At basic flow step [***Log On***](#id.ufvvkpz8pcyg), the system establishes whether the Administrator is valid. In case, he/she is not, a proper error message displays and the use case ends.
>
> *For further explanation regarding authentication and authorization, please refer to [***Authentication and Authorization Section***](#manage-authentication-and-authorization-010).*

1.  **Quit**

> The system allows the Administrator to quit at any time during the use case. The Administrator chooses not to save any of the entered information or decides to switch to another view, without submitting the changes, all the entered information will be discarded. The use case ends.

1.  **Invalid Name**

> At basic flow step [***Create Task***](#id.wi8010v1g3ki) the system determines that the name of the Task is not valid and displays an error message. The use case ends.

Manage Material (\#006)
-----------------------

### Overview

A Material is a cause for a pipeline to run. Often, it is a source code material repository (Git, TFS, etc). The Hawkengine System continuously polls configured Materials and when a new change or commit is found, the corresponding Pipelines are run or "triggered".

There are different kinds of Materials. An example can be a Git Material. When a commit is made to the repository configured in the Git Material, the pipeline gets triggered.

A Pipeline can even be configured with multiple Materials. When either repository has a new commit, the Pipeline is triggered.

### Brief Description

This use case allows an Administrator to create a new Material. He/she can also modify or delete a certain Material, depending on the intention.

### Actors

*Primary Actor - Administrator*

### Flow of Events

1.  #### Basic Flow

    1.  <span id="id.ilzxouncss6x" class="anchor"></span>**Log On**

> This use case starts when an Administrator accesses the Hawkengine System. The Administrator enters an email and password and the system authenticates them.

1.  <span id="id.p1ylkyd97fci" class="anchor"></span>**Choose Pipeline**

> The Administrator chooses in which Pipeline to create a Material.

1.  <span id="id.fk7yhs69rwaz" class="anchor"></span>**Create Material**

> The system provides the operations available to the Administrator to create a new Material within the chosen Pipeline. These operations are: Create, Modify or Delete Material. The Administrator chooses “Create Material” option.

1.  <span id="id.lj6vxskswk8m" class="anchor"></span>**Fill the Material Options**

> The system provides the Administrator with a set of fields to fill. The Administrator can modify the type of the Material. The system provides two options. The Administrator chooses Git Material. The system provides a set of fields that should be filled. The Administrator enters name for the Material, Git url and credentials.

1.  <span id="id.onve5d8lpwoy" class="anchor"></span>**Submit the Material**

> The Administrator indicates that the operation based on creating a new Material is completed. The system validates all of the entered values and saves the Material. The use case ends.

1.  #### Alternative Flows

    1.  **Create NuGet Material**

> At basic flow step **[*Create Material*](#id.fk7yhs69rwaz),** the Administrator chooses NuGet Material. The system provides a set of fields that should be filled. The Administrator enters Material name, NuGet url, NuGet package and credentials. He/she is also able to choose whether the Material to include can be a pre-release. The use case resumes at basic flow step [***Submit the Material***](#id.onve5d8lpwoy).

1.  **Modify Material**

> At basic flow step **[*Create Material*](#id.fk7yhs69rwaz),** the Administrator already has a Material that has been created. The system retrieves and displays the Material on focus. The Administrator has a set of options to modify:

1.  **Material Name**

> The Administrator can modify the Material name.

1.  **Git Url**

> The Administrator can modify the Git Url of the Material.

1.  **Git Branch**

> The Administrator can modify the Git Branch of the Material.

1.  **Poll for Changes**

> The Administrator can choose to poll for changes.

1.  **Credentials**

> The Administrator should enter his/her Git credentials, if the Git repository is private.

1.  **Delete a Material.**

> At basic flow step [***Create Material**,*](#id.fk7yhs69rwaz) the Administrator already has a Material and chooses to delete it. The system warns the user that he/she is about to delete a Material. The Administrator confirms the deletion. The system deletes the Material. The use case ends.

1.  **Unauthenticated User**

> At basic flow step [***Log On**,*](#id.ilzxouncss6x) the system establishes whether the Administrator is valid. In case, he/she is not, a proper error message displays and the use case ends.

1.  **Quit**

> The system allows the Administrator to quit at any time during the use case. The Administrator chooses not to save any of the entered information or decides to switch to another view, without submitting the changes, all the entered information will be discarded. The use case ends.

1.  **Invalid Name**

> At basic flow step [***Create Material***](#id.fk7yhs69rwaz) the system determines that the Material name is not valid and displays an error message. The use case ends.

1.  **Invalid Url**

> At basic flow step [***Create Material***](#id.fk7yhs69rwaz) the system determines that the Material Url is not valid and displays an error message. The use case ends.

1.  **Invalid Credentials**

> At basic flow step [***Create Material***](#id.fk7yhs69rwaz) the system determines that the Material credentials are not valid and displays an error message. The use case ends.

Manage Agent (\#007)
--------------------

### Overview

> Hawkengine Agents are the workers that execute the Jobs/Tasks. All Tasks configured in the system run on Hawkengine Agents. The Hawkengine Server polls for changes in Materials and then, when a change is detected and a Pipeline needs to be triggered, the corresponding Jobs are assigned to the Agents, for them to execute the Tasks.
>
> Agents pick up Jobs which are assigned to them, execute the Tasks in the Job and report the status of the Job to the Hawkengine Server. Then, the Server collects all the information from the different Jobs and then decides on the status of the Stage.
>
> Agents and Jobs can be enhanced with "Resources". Resources are free-form tags, that help Hawkengine decides which Agents are capable of picking up specific Jobs. The resources can be thought of as the Agent broadcasting its capabilities. Resources are defined by administrators and can mean anything the administrators want them to mean.

### Brief Description

> This use case allows an Administrator to modify or delete a certain Agent, depending on the intention.

### Actors

> *Primary Actor - Administrator*

### Flow of Events

1.  #### Basic Flow

    1.  <span id="id.6sjkl63j0xs8" class="anchor"></span>**Log On**

> This use case starts when an Administrator accesses the Hawkengine System. The Administrator enters an email and password and the system validates them.

1.  <span id="id.x8x5kw29o8ui" class="anchor"></span>**Choose Agent**

> The Administrator chooses with which Agent to operate.

1.  <span id="id.m3aneis68u6i" class="anchor"></span>**Choose Agent Option**

> The system provides the operations available to the Administrator to elaborate. The operations are: Enable/Disable Agent, Edit Resources and Delete Agent. The Administrator chooses “Enable/Disable Agent” option.

1.  <span id="id.5s3oztfx9u7m" class="anchor"></span>**Enable Agent**

> When an Agent first connects to the Server it is “Disabled”. The Administrator chooses to enable Agent in order to schedule work on it.

1.  <span id="id.m66qsyy223j" class="anchor"></span>**Submit the Agent**

> The Administrator indicates that the operation regarding the state of the Agent is completed. The system saves the changes. The use case ends.

1.  #### Alternative Flow

    1.  **Add Resources**

> At basic flow step [***Choose Agent Option***](#id.m3aneis68u6i), the Administrator chooses to Edit Resources. The system provides a set of options. The Administrator chooses to Add new Resource. He/she enters the Resource name and submits the result.

1.  **Delete Resources**

> At basic flow step [***Choose Agent Option***](#id.m3aneis68u6i), the Administrator chooses to Edit Resources. The system provides a set of options. The Administrator chooses to Delete Resource. The system warns the user that he/she is about to delete a Resource. The Administrator confirms the deletion. The system deletes the Resource. The use case ends.

1.  **Delete Agent**

> At basic flow step [***Choose Agent Option***](#id.m3aneis68u6i), the Administrator chooses to Delete Agent. The system warns the user that he/she is about to delete an Agent. The Administrator confirms the deletion. The system deletes the Agent. The use case ends.

1.  **Unauthenticated User**

> At basic flow step [***Log On***](#id.6sjkl63j0xs8), the system establishes whether the Administrator is valid. In case, he/she is not, a proper error message displays and the use case ends.

Manage Environment Variable (\#008)
-----------------------------------

### Overview

> In Hawkengine Server, Environment Variables are user-defined variables that are defined in the configuration. These environment variables are made available to tasks just like other environment variables available to processes when they run in an operation system.
>
> Environment Variables can be defined at multiple levels: Within Environments, within Pipelines, within Stages and within Jobs. They follow a cascading system where Environment Variables defined at the Environment level get overridden by Environment Variables defined at the Pipeline level and so on.

### Brief Description

> This use case allows an Administrator to create a new Environment Variable. He/she can also modify or delete a certain Environment Variable, depending on the intention.

### Actors

> *Primary Actor - Administrator*

### Flow of Events

1.  #### Basic Flow

    1.  <span id="id.3s7v1tp4v8s8" class="anchor"></span>**Log On**

> This use case starts when an Administrator accesses the Hawkengine System. The Administrator enters an email and password and the system validates them.

1.  <span id="id.nlka2af77kdk" class="anchor"></span>**Choose Pipeline**

> The Administrator chooses in which Pipeline to create an Environment Variable.

1.  <span id="id.poplcugmln68" class="anchor"></span>**Create Environment Variable**

> The system provides the operations available to the Administrator to create a new Environment Variable within the chosen Pipeline. These operations are: Create, Modify or Delete an Environment Variable. The Administrator chooses “Create Environment Variable” option.

1.  <span id="id.3ypupwxplppr" class="anchor"></span>**Fill the Environment Variable Options**

> The system provides the Administrator with a set of fields to fill. The Administrator can add or modify the name of the Environment Variable, the value of the Environment Variable, as well as to choose whether the Environment Variable will be secured.

1.  <span id="id.32odvtk0c3d7" class="anchor"></span>**Submit the Environment Variable**

> The Administrator indicates that the operation based on creating a new Environment Variable is completed. The system validates all of the entered values and saves the Environment Variable. The use case ends.

1.  #### Alternative Flow

    1.  **Modify Environment Variable**

> At basic flow step **[*Create Environment Variable*](#id.poplcugmln68),** the Administrator already has an Environment Variable that has been created. The system retrieves and displays the Environment Variable on focus. The Administrator has set of options to choose from:

1.  **Environment Variable Name**

> The Administrator can modify the Environment Variable name.

1.  **Environment Variable Value**

> The Administrator can modify the Environment Variable value.

1.  **Environment Variable Security**

> The Administrator can modify the Environment Variable security.
>
> The Administrator can modify all of the options as desired until choosing to submit the changes. At this point the system saves the updated Environment Variable and the use case ends. The use case resumes at basic flow step [***Submit the Environment Variable***](#id.32odvtk0c3d7).

1.  **Delete Environment Variable**

> At basic flow step [***Create Environment Variable***](#id.poplcugmln68), the Administrator already has an Environment Variable created and chooses to delete it. The system warns the user that he/she is about to delete an Environment Variable. The Administrator confirms the deletion. The system deletes the Environment Variable. The use case ends.

1.  **Add Stage Environment Variable**

> At basic flow step [***Create Environment Variable***](#id.poplcugmln68), the Administrator is able to choose in which Stage to add the Environment Variable. Once, he/she chooses the desired Stage, the use case resumes at **[*Fill the Environment Variable Options*](#id.3ypupwxplppr)** basic flow step.

1.  **Add Job Environment Variable**

> At basic flow step [***Create Environment Variable***](#id.poplcugmln68), the Administrator is able to choose in which Job to add the Environment Variable. Once, he/she chooses the desired Job, the use case resumes at **[*Fill the Environment Variable Options*](#id.3ypupwxplppr)** basic flow step.

1.  **Unauthenticated User**

> At basic flow step [***Log On***](#id.3s7v1tp4v8s8), the system establishes whether the Administrator is valid. In case, he/she is not, a proper error message displays and the use case ends.

1.  **Quit.**

> The system allows the Administrator to quit at any time during the use case. The Administrator chooses not to save any of the entered information or decides to switch to another view, without submitting the changes, all the entered information will be discarded. The use case ends.

Manage Environment (\#009)
--------------------------

### Overview

> An Environment is a group of Agents that allows to distribute Jobs. It is a logical boundary for Job execution. Common examples of environments are **Test**, **Acceptance**, **Staging** or **Production**. An Environment can be assigned to multiple Agents and Pipelines. One Pipeline can belong to more than one Environment, however, one Agent can only belong to one Environment*. *
>
> Access to an Environment should be controlled by the system RBAC model. There should be a way to limit access to Environments for specific users and groups.

### Brief Description

> This use case allows an Administrator to create, modify or delete Environments

### Actors

> *Primary Actor - Administrator*

### Flow of Events

1.  #### Basic Flow

    1.  <span id="id.xrz3wftjxts1" class="anchor"></span>**Log On**

> This use case starts when an Administrator accesses the Hawkengine System. The Administrator enters an email and password and the system validates them.

1.  <span id="id.e06dtta82bor" class="anchor"></span>**Create Environment**

> The system provides the operations available to the Administrator to manage Environments. These operations are: Create, Modify or Delete Environment. The Administrator chooses “Create Environment” option.

1.  <span id="id.ddmb0a12p494" class="anchor"></span>**Fill the Environment Options**

> The system provides the Administrator with a set of fields to fill. The Administrator can modify:

1.  **Environment Name**

> The Administrator should provide a name for the Environment.

1.  **Environment Variables**

> The Administrator may add one or more Environment Variables that need to be passed.

1.  **Pipelines**

> The Administrator may add one or more Pipelines that need to run on the Environment.

1.  **Agents**

> The Administrator may add one or more Agents associated with the Environment.

1.  <span id="id.egnmnc5l5qzp" class="anchor"></span>**Submit the Environment**

> The Administrator indicates that the operation based on creating a new Environment is completed. The system validates all of the entered values and saves the Environment. The use case ends.

1.  #### Alternative Flow

    1.  **Modify Environment**

> At basic flow step **[*Create Environment*](#id.e06dtta82bor),** the Administrator already has an Environment that has been created. The system retrieves and displays the Environment on focus. The Administrator has a set of options to modify:

1.  **Environment Name**

> The Administrator can modify the Environment name.

1.  **Environment Variables**

> The Administrator can add or remove Environment Variables.

1.  **Pipelines**

> The Administrator can add Pipelines that need to run on the particular Environment, or remove the ones that are no longer needed.

1.  **Agents**

> The Administrator can add unassigned Agents to the Environment, or remove some, making them free for further association.
>
> The Administrator can modify all of the options as desired until choosing to submit the changes. At this point the system saves the updated Environment. The use case resumes at basic flow step [***Submit the Environment***](#id.egnmnc5l5qzp).

1.  **Delete Environment**

> At basic flow step [***Create Environment***](#id.e06dtta82bor), the Administrator already has an Environment created and chooses to delete it. The system warns the user that he/she is about to delete an Environment. The Administrator confirms the deletion. The system deletes the Environment. The use case ends.

1.  **Unauthenticated User**

> At basic flow step [***Log On***](#id.xrz3wftjxts1), the system establishes whether credentials of the Administrator are valid. In case, he/she is not, a proper error message displays and the use case ends.

Manage Authentication and Authorization (\#010)
-----------------------------------------------

### Overview

> The server has the notion for *scope* and *permission types*. A scope represents a certain level from the server where specific rights can be applied. On the other hand, *permission types* define the rights - what a user can do from a specific scope. Combining both concepts (scope & permission types) provides a flexible authorization model.
>
> *Permission Scopes *

-   Server - global server scope

-   Pipeline group - pipeline group level

-   Pipeline - pipeline level scope

-   Environment - environment scope

> *Permission Types*

-   Viewer - a user can only view a given resource and its child resources

-   Operator - a user can view and operate (run, re-run, pause, stop, etc.) a given resource (e.g. Pipeline & Stage) and its child resources

-   Admin

*Groups*

> A group is a set of claims (scope + permissions) that are grouped together. A group would ease the authorization management across groups of people. E.g. if we have 3 teams dev, qa & ops, rather than assigning permissions individually to each team member, we would create a group and add scope and permissions to it, then add the members to the group, so that they inherit all of the group’s permissions.

*Permission Inheritance*

> If a user is assigned a *pipeline group* scope and an *admin* permission type that would mean that all resources that are children of the current pipeline group (scope) e.g. one or more pipelines, will obey the permission assigned to their parent - pipeline group.

*Overriding Permissions*

> This is the case when we want to give a user permissions at a given scope e.g. "pipeline group", however we need to either restrict or broaden the rights to one or more child resources, e.g. Pipelines.
>
> Given is a Pipeline group named "Dev pipelines" and we want to have one of our teams to have view rights for the group. Combining the *Pipeline Group* scope and the *view* permission type would allow anyone of the team to see all pipelines. However, if we want the Development Lead of the team to be able to administer one or more pipelines from the group, but not all of them, we would assign in addition to its view rights inherited from the pipeline group scope, a *pipeline* scope with *admin* permission for a concrete pipelines that he needs administration rights for. In fact we'll override the inherited rights he received as part of the *Pipeline group* scope.

### Brief Description

> This use case allows an Administrator to modify team permissions, as well as individual permissions.

### Actors

> Primary Actor - Server Administrator

### Flow of Events

1.  #### Basic Flow

    1.  <span id="id.5fqd8t6gqthm" class="anchor"></span>**Log On**

> This use case starts when an Administrator accesses the Hawkengine System. The Administrator enters an email and password and the system validates them.

1.  <span id="id.lky29z91kqd5" class="anchor"></span>**Choose team of people**

> The Administrator chooses which team to assign to the desired project.

1.  <span id="id.z1symqwuu8zo" class="anchor"></span>**Set permission for the team**

> The system provides a set of options for the Administrator to choose from:

1.  **Server Admin**

> The Administrator gives global Admin rights to the team. The team members will be able to fully operate on a global level.

1.  **Pipeline Group Admin**

> The Administrator gives Pipeline Group Admin level rights to the team. The team members will be able to fully operate on a Pipeline Group level.

1.  **Pipeline Admin**

> The Administrator gives Pipeline Admin level rights to the team. The team members will be able to fully operate on a Pipeline level.

1.  **Environment Admin**

> The Administrator gives Environment Admin level rights to the team. The team members will be able to fully operate on an Environment level.

1.  **Server Operator**

> The Administrator gives Server Operator level rights to the team. The team members will be able to operate (run, re-run, pause, stop, etc.) on a Server level.

1.  **Pipeline Group Operator**

> The Administrator gives Pipeline Group Operator level rights to the team. The team members will be able to operate (run, re-run, pause, stop, etc.) on a Pipeline Group level.

1.  **Pipeline Operator**

> The Administrator gives Pipeline Operator level rights to the team. The team members will be able to operate (run, re-run, pause, stop, etc.) on a Pipeline level.

1.  **Environment Operator**

> The Administrator gives Environment Operator level rights to the team. The team members will be able to operate (run, re-run, pause, stop, etc.) on an Environment level.

1.  **Server Viewer**

> The Administrator gives Server Viewer level rights to the team. The team members will be able to observe on a Server level.

1.  **Pipeline Group Viewer**

> The Administrator gives Pipeline Group Viewer level rights to the team. The team members will be able to observe on a Pipeline Group level.

1.  **Pipeline Viewer**

> The Administrator gives Pipeline Viewer level rights to the team. The team members will be able to observe on a Pipeline level.

1.  **Environment Viewer**

> The Administrator gives Environment Viewer level rights to the team. The team members will be able to observe on an Environment level.
>
> The Administrator chooses the Pipeline Group Viewer as an option for team permissions.

1.  <span id="id.u5fqj9x0ed03" class="anchor"></span>**Submit Permission Changes**

> The Administrator indicates that the operation based on the team permissions is completed. The system validates all of the information provided. The use case ends.

1.  #### Alternative Flow

    1.  **Override individual permissions for person.**

> At basic flow step [***Set permission for the team***](#id.z1symqwuu8zo), the system provides options for individual permissions. The Administrator can:

1.  **Restrict team member rights**

    1.  **Choose team member**

> The Administrator chooses the team member whose permissions he/she will restrict.

1.  **Set individual permissions**

> The Administrator chooses to set Pipeline Viewer level rights to the team member, not the Pipeline Group Viewer level rights as was previously initialized. The team member will no longer be able to observe the Pipeline Group operations, but only the ones associated with the chosen Pipeline.

1.  **Broaden team member rights**

    1.  **Choose team member**

> The Administrator chooses the team member whose permissions he/she will broaden.

1.  **Set individual permissions**

> The Administrator chooses to set Pipeline Group Operator level rights to the team member, not the Pipeline Group Viewer level rights as was previously initialized. The team member will be able to operate within the Pipeline Group, not only to observe.
>
> The Administrator can modify the permissions as desired until choosing to submit the changes. At this point the system saves the updated permissions for the team member. The use case resumes at basic flow step [***Submit Permission Changes.***](#id.u5fqj9x0ed03)

1.  **Update team permission to**

    1.  **Pipeline Group Admin**

> At basic flow step **[*Set permission for the team*](#id.z1symqwuu8zo),** the Administrator chooses to set the team permissions to Pipeline Group Admin. In this way, the team members will be eligible to fully operate within the Pipeline Group.

1.  **Pipeline Group Operator**

> At basic flow step **[*Set permission for the team*](#id.z1symqwuu8zo),** the Administrator chooses to set the team permissions to Pipeline Group Operator. In this way, the team members will be eligible to operate (run, re-run, pause, stop, etc.) within the Pipeline Group.

Functional Requirements
=======================

> This section includes the requirements that specify all the fundamental actions of the software system.

1.  *Use Case \#001 - Manage Pipeline Group*
    ----------------------------------------

    1.  ### Add Pipeline Group

|                       |                                                                                                                                                                                |
|-----------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Use Case Name**     | Manage Pipeline Group                                                                                                                                                          |
| **Trigger**           | The User chooses to add a new Pipeline Group to the database.                                                                                                                  |
| **Precondition**      | 1.  The User has logged in.                                                                                                                                                    

                         2.  The User has chosen to add a new Pipeline Group.                                                                                                                            |
| **Basic Flow**        | 1.  The system validates the data entered by the User.                                                                                                                         

                         2.  The system accesses the Hawkengine database.                                                                                                                                

                         3.  The system saves the Pipeline Group to the database.                                                                                                                        

                         4.  The system returns the newly created Pipeline Group.                                                                                                                        |
| **Alternative Flows** | 1.  At Basic Flow step 1, if the entered data is invalid, the system returns an error with an appropriate message.                                                             

                         2.  At Basic Flow step 3, the system checks whether a Pipeline Group with the same name already exists. If this is the case, an error with an appropriate message is returned.  |
| **Postcondition**     | The Pipeline Group has been added to the database.                                                                                                                             |
| **Exception Paths**   | The User may abandon the operation at any time, without saving the information.                                                                                                |

<span id="h.3fwokq0" class="anchor"></span>

### Update Pipeline Group

|                       |                                                                                                                                                  |
|-----------------------|--------------------------------------------------------------------------------------------------------------------------------------------------|
| **Use Case Name**     | Manage Pipeline Group                                                                                                                            |
| **Trigger**           | The User chooses to update a Pipeline Group in the database.                                                                                     |
| **Precondition**      | 1.  The User has logged in.                                                                                                                      

                         2.  The User has chosen to update Pipeline Group.                                                                                                 |
| **Basic Flow**        | 1.  The system validates the data entered by the User.                                                                                           

                         2.  The system accesses the Hawkengine database.                                                                                                  

                         3.  The system updates the Pipeline Group in the database.                                                                                        

                         4.  The system returns the updated the Pipeline Group.                                                                                            |
| **Alternative Flows** | 1.  At Basic Flow step 1, if the entered data is invalid, the system returns an error with an appropriate message.                               

                         2.  At Basic Flow step 3, the system checks whether the Pipeline Group exists. If it does not, an error with an appropriate message is returned.  |
| **Postcondition**     | The Pipeline Group has been updated in the database.                                                                                             |
| **Exception Paths**   | The User may abandon the operation at any time, without saving the information.                                                                  |

### Delete Pipeline Group

|                       |                                                                                                                                                                                        |
|-----------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Use Case Name**     | Manage Pipeline Group                                                                                                                                                                  |
| **Trigger**           | The User chooses to delete a Pipeline Group from the database.                                                                                                                         |
| **Precondition**      | 1.  The User has logged in.                                                                                                                                                            

                         2.  The User has chosen to delete the Pipeline Group.                                                                                                                                   |
| **Basic Flow**        | 1.  The system accesses the Hawkengine database.                                                                                                                                       

                         2.  The system deletes the Pipeline Group from the database.                                                                                                                            

                         3.  The system notifies the User that the Pipeline Group has been deleted.                                                                                                              |
| **Alternative Flows** | 1.  At Basic Flow step 2, the system checks whether the Pipeline Group exists or contains any Pipelines. Either way, if it does not, an error with an appropriate message is returned. |
| **Postcondition**     | The Pipeline Group has been deleted from the database.                                                                                                                                 |
| **Exception Paths**   | The User may abandon the operation at any time, without saving the information.                                                                                                        |

1.  *Use Case \#002 - Manage Pipeline*
    ----------------------------------

    1.  ### Add Pipeline

|                       |                                                                                                                                                                                                                      |
|-----------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Use Case Name**     | Manage Pipeline                                                                                                                                                                                                      |
| **Trigger**           | The User chooses to add a new Pipeline to the database.                                                                                                                                                              |
| **Precondition**      | 1.  The User has logged in.                                                                                                                                                                                          

                         2.  The User has chosen to add new Pipeline.                                                                                                                                                                          |
| **Basic Flow**        | 1.  The system validates the data entered by the User.                                                                                                                                                               

                         2.  The system accesses the Hawkengine database.                                                                                                                                                                      

                         3.  The system saves the Pipeline to the database.                                                                                                                                                                    

                         4.  The system returns the newly created Pipeline.                                                                                                                                                                    |
| **Alternative Flows** | 1.  At Basic Flow step 1, if the entered data is invalid, the system returns an error with an appropriate message.                                                                                                   

                         2.  At Basic Flow step 3, the system checks whether a Pipeline with the same name within the same Pipeline Group already exists. If it does, an error message “Pipeline {pipelineName} already exists.” is returned.  |
| **Postcondition**     | The Pipeline has been added to the database.                                                                                                                                                                         |

<span id="h.dtfm14lmzp2l" class="anchor"></span>

### Update Pipeline

|                       |                                                                                                                                                                  |
|-----------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Use Case Name**     | Manage Pipeline                                                                                                                                                  |
| **Trigger**           | The User chooses to update a Pipeline in the database.                                                                                                           |
| **Precondition**      | 1.  The User has logged in.                                                                                                                                      

                         2.  The User has chosen to update the Pipeline.                                                                                                                   |
| **Basic Flow**        | 1.  The system validates the data entered by the User.                                                                                                           

                         2.  The system accesses the Hawkengine database.                                                                                                                  

                         3.  The system saves the Pipeline to the database.                                                                                                                

                         4.  The system returns the updated Pipeline.                                                                                                                      |
| **Alternative Flows** | 1.  At Basic Flow step 1, if the entered data is invalid, the system returns an error with an appropriate message.                                               

                         2.  At Basic Flow step 3, the system checks whether the Pipeline exists. If it does not, an error message “Pipeline {pipelineName} does not exist.” is returned.  |
| **Postcondition**     | The Pipeline has been updated in the database.                                                                                                                   |

###

### Delete Pipeline

|                       |                                                                                                                                                                  |
|-----------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Use Case Name**     | Manage Pipeline                                                                                                                                                  |
| **Trigger**           | The User chooses to delete a Pipeline from the database.                                                                                                         |
| **Precondition**      | 1.  The User has logged in.                                                                                                                                      

                         2.  The User has chosen to delete the Pipeline.                                                                                                                   |
| **Basic Flow**        | 1.  The system accesses the Hawkengine database.                                                                                                                 

                         2.  The system deletes the Pipeline from the database.                                                                                                            

                         3.  The system notifies the User that the Pipeline has been deleted.                                                                                              |
| **Alternative Flows** | 1.  At Basic Flow step 2, the system checks whether the Pipeline exists. If it does not, an error message “Pipeline {pipelineName} does not exist.” is returned. |
| **Postcondition**     | The Pipeline has been deleted from the database.                                                                                                                 |
| **Exception Paths**   | The User may abandon the operation at any time, without saving the information.                                                                                  |

###

### Run Pipeline

|                       |                                                                                                                                                                  |
|-----------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Use Case Name**     | Manage Pipeline                                                                                                                                                  |
| **Trigger**           | The User chooses to run a Pipeline.                                                                                                                              |
| **Precondition**      | 1.  The User has logged in.                                                                                                                                      

                         2.  The User has chosen to run a Pipeline with a single Job or it was triggered automatically.                                                                    

                         3.  An eligible Agent within the current Environment.                                                                                                             |
| **Basic Flow**        | 1.  The system accesses the Hawkengine database.                                                                                                                 

                         2.  The system updates all Materials within the Pipeline.                                                                                                         

                         3.  The system sets the status of the Pipeline to “*in progress*”.                                                                                                

                         4.  The system sets the status of the Job to “*awaiting*”.                                                                                                        

                         5.  The system assigns the Job to the Agent.                                                                                                                      

                         6.  The system sets the status of the Job to “*scheduled*”.                                                                                                       

                         7.  The Agent processes the assigned Job, meanwhile the system sets the status of the processing Job to “*running*”.                                              

                         8.  The Agent sets the status of the processed Job to “*passed*”, if all its Tasks’ statuses are “*passed*” as well.                                              

                         9.  The system sets the status of the Pipeline to “*passed*”.                                                                                                     |
| **Alternative Flows** | 1.  At Basic Flow step 1, the system checks whether the Pipeline exists. If it does not, an error message “Pipeline {pipelineName} does not exist.” is returned.

                         2.  If Basic Flow step 2 fails, an error message “Material {materialName} could not be updated. {errorReason}” is returned.                                       

                         3.  At Basic Flow step 3, if the Pipeline is locked and there is a Pipeline scheduled beforehand, it will not be scheduled.                                       

                         4.  Basic Flow step 5 can fail under the following circumstances:                                                                                                 

                             1.  There is no Agent in the current Environment.                                                                                                             

                             2.  There is no eligible Agent in the current Environment:                                                                                                    

                                 1.  The Agent is disabled.                                                                                                                                

                                 2.  The Agent is disconnected.                                                                                                                            

                                 3.  The Agent does not meet the required resources to process the Job.                                                                                    

                         > If this step fails, the Job status will remain “*awaiting*”.                                                                                                    

                         1.  At Basic Flow step 8, if any Task’s status is “*failed*” and the option “Ignore Errors” is not selected, the Agent sets the Job’s status to “*failed*”.       

                         2.  At Basic Flow step 9, if the Job’s status is “*failed*”, the system sets the Pipeline’s status to “*failed*”.                                                 |
| **Postcondition**     | The Pipeline has been scheduled.                                                                                                                                 |

1.  *Use Case \#003 - Manage Stage*
    -------------------------------

    1.  ### Add Stage

|                       |                                                                                                                                                                                                       |
|-----------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Use Case Name**     | Manage Stage                                                                                                                                                                                          |
| **Trigger**           | The User chooses to add a new Stage to the database.                                                                                                                                                  |
| **Precondition**      | 1.  The User has logged in.                                                                                                                                                                           

                         2.  The User has chosen to add a new Stage.                                                                                                                                                            |
| **Basic Flow**        | 1.  The system validates the data entered by the User.                                                                                                                                                

                         2.  The system accesses the Hawkengine database.                                                                                                                                                       

                         3.  The system saves the Stage to the database.                                                                                                                                                        

                         4.  The system returns the newly created Stage.                                                                                                                                                        |
| **Alternative Flows** | 1.  At Basic Flow step 1, if the entered data is invalid, the system returns an error with an appropriate message.                                                                                    

                         2.  At Basic Flow step 3, the system checks whether a Stage with the same name within the same Pipeline already exists. If it does, an error message “Stage {stageName} already exists.” is returned.  |
| **Postcondition**     | The Stage has been added to the database.                                                                                                                                                             |

<span id="h.4c4bqzyeiojz" class="anchor"></span>

### Update Stage

|                       |                                                                                                                                                                                                           |
|-----------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Use Case Name**     | Manage Stage                                                                                                                                                                                              |
| **Trigger**           | The User chooses to update a Stage in the database.                                                                                                                                                       |
| **Precondition**      | 1.  The User has logged in.                                                                                                                                                                               

                         2.  The User has chosen to update the Stage.                                                                                                                                                               |
| **Basic Flow**        | 1.  The system validates the data entered by the User.                                                                                                                                                    

                         2.  The system accesses the Hawkengine database.                                                                                                                                                           

                         3.  The system saves the Stage to the database.                                                                                                                                                            

                         4.  The system returns the updated Stage.                                                                                                                                                                  |
| **Alternative Flows** | 1.  At Basic Flow step 1, if the entered data is invalid, an error with an “Invalid data” message is returned.                                                                                            

                         2.  At Basic Flow step 3, the system checks whether a Stage with the same name within the same Pipeline already exists. If it does not, an error message “Stage {stageName} does not exist.” is returned.  |
| **Postcondition**     | The Stage has been updated in the database.                                                                                                                                                               |

###

### Delete Stage

|                       |                                                                                                                                                         |
|-----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Use Case Name**     | Manage Stage                                                                                                                                            |
| **Trigger**           | The User chooses to delete a Stage from the database.                                                                                                   |
| **Precondition**      | 1.  The User has logged in.                                                                                                                             

                         2.  The User has chosen to delete the Stage.                                                                                                             |
| **Basic Flow**        | 1.  The system accesses the Hawkengine database.                                                                                                        

                         2.  The system deletes the Stage from the database.                                                                                                      

                         3.  The system notifies the User that the Stage has been deleted.                                                                                        |
| **Alternative Flows** | 1.  At Basic Flow step 2, the system checks whether the Stage exists. If it does not, an error message “Stage {stageName} does not exist.” is returned. |
| **Postcondition**     | The Stage has been deleted from the database.                                                                                                           |

###

### Run Stage

|                       |                                                                                                                                                                           |
|-----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Use Case Name**     | Manage Stage                                                                                                                                                              |
| **Trigger**           | The User chooses to run a Stage.                                                                                                                                          |
| **Precondition**      | 1.  The User has logged in.                                                                                                                                               

                         2.  The User has chosen to run the Stage or it was triggered automatically.                                                                                                |
| **Basic Flow**        | 1.  The system accesses the Hawkengine database.                                                                                                                          

                         2.  The system schedules a new Pipeline, starting from the current Stage.                                                                                                  |
| **Alternative Flows** | 1.  At Basic Flow step 1, the system checks whether the Pipeline exists. If it does not, an error with an appropriate message is returned.                                

                         2.  At Basic Flow step 2, the system checks whether the Stage exists. If it does not, an error with an appropriate message is returned.                                    

                         3.  At Basic Flow step 2, the system schedules a new Pipeline. However, if the Pipeline is locked and there is a Pipeline scheduled beforehand, it will not be scheduled.  |
| **Postcondition**     | The Pipeline has been scheduled starting from the current Stage.                                                                                                          |
| **Exception Paths**   | None.                                                                                                                                                                     |

1.  *Use Case \#004 - Manage Job*
    -----------------------------

    1.  ### Add Job

|                       |                                                                                                                                                                                              |
|-----------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Use Case Name**     | Manage Job                                                                                                                                                                                   |
| **Trigger**           | The User chooses to add a new Job to the database.                                                                                                                                           |
| **Precondition**      | 1.  The User has logged in.                                                                                                                                                                  

                         2.  The User has chosen to add a new Job.                                                                                                                                                     |
| **Basic Flow**        | 1.  The system validates the data entered by the User.                                                                                                                                       

                         2.  The system accesses the Hawkengine database.                                                                                                                                              

                         3.  The system saves the Job to the database.                                                                                                                                                 

                         4.  The system returns the newly created Job.                                                                                                                                                 |
| **Alternative Flows** | 1.  At Basic Flow step 1, if the entered data is invalid, the system returns an error with an appropriate message.                                                                           

                         2.  At Basic Flow step 3, the system checks whether a Job with the same name within the same Stage already exists. If it does, an error message “Job {jobName} already exists.” is returned.  |
| **Postcondition**     | The Job has been added to the database.                                                                                                                                                      |

###

### Update Job

|                       |                                                                                                                                                   |
|-----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| **Use Case Name**     | Manage Job                                                                                                                                        |
| **Trigger**           | The User chooses to update a Job in the database.                                                                                                 |
| **Precondition**      | 1.  The User has logged in.                                                                                                                       

                         2.  The User has chosen to update the Stage.                                                                                                       |
| **Basic Flow**        | 1.  The system validates the data entered by the User.                                                                                            

                         2.  The system accesses the Hawkengine database.                                                                                                   

                         3.  The system saves the Job to the database.                                                                                                      

                         4.  The system returns the updated Job.                                                                                                            |
| **Alternative Flows** | 1.  At Basic Flow step 1, if the entered data is invalid, an error message “Invalid data” is returned.                                            

                         2.  At Basic Flow step 3, the system checks whether the Job exists. If it does not, an error message “Job {jobName} does not exist.” is returned.  |
| **Postcondition**     | The Job has been updated in the database.                                                                                                         |

###

### Delete Job

|                       |                                                                                                                                                   |
|-----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| **Use Case Name**     | Manage Job                                                                                                                                        |
| **Trigger**           | The User chooses to delete a Job from the database.                                                                                               |
| **Precondition**      | 1.  The User has logged in.                                                                                                                       

                         2.  The User has chosen to delete the Job.                                                                                                         |
| **Basic Flow**        | 1.  The system accesses the Hawkengine database.                                                                                                  

                         2.  The system deletes the Job from the database.                                                                                                  

                         3.  The system notifies the User that the Job has been deleted.                                                                                    |
| **Alternative Flows** | 1.  At Basic Flow step 2, the system checks whether the Job exists. If it does not, an error message “Job {jobName} does not exist.” is returned. |
| **Postcondition**     | The Job has been deleted from the database.                                                                                                       |

1.  *Use Case \#005 - Manage Task*
    ------------------------------

    1.  ### Add Task

|                       |                                                                                                        |
|-----------------------|--------------------------------------------------------------------------------------------------------|
| **Use Case Name**     | Manage Task                                                                                            |
| **Trigger**           | The User chooses to add a new Task to the database.                                                    |
| **Precondition**      | 1.  The User has logged in.                                                                            

                         2.  The User has chosen to add a new Task.                                                              |
| **Basic Flow**        | 1.  The system validates the data entered by the User.                                                 

                         2.  The system accesses the Hawkengine database.                                                        

                         3.  The system saves the Task to the database.                                                          

                         4.  The system returns the newly created Task.                                                          |
| **Alternative Flows** | 1.  At Basic Flow step 1, if the entered data is invalid, an error message “Invalid data” is returned. |
| **Postcondition**     | The Task has been added to the database.                                                               |

###

### Update Task

|                       |                                                                                                                                                   |
|-----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| **Use Case Name**     | Manage Task                                                                                                                                       |
| **Trigger**           | The User chooses to update a Task in the database.                                                                                                |
| **Precondition**      | 1.  The User has logged in.                                                                                                                       

                         2.  The User has chosen to update the Task.                                                                                                        |
| **Basic Flow**        | 1.  The system validates the data entered by the User.                                                                                            

                         2.  The system accesses the Hawkengine database.                                                                                                   

                         3.  The system saves the Task to the database.                                                                                                     

                         4.  The system returns the updated Task.                                                                                                           |
| **Alternative Flows** | 1.  At Basic Flow step 1, if the entered data is invalid, an error message “Invalid data” is returned.                                            

                         2.  At Basic Flow step 3, the system checks whether the Task already exists. If it does not, an error message “Task does not exist.” is returned.  |
| **Postcondition**     | The Task has been updated in the database.                                                                                                        |

###

### Delete Task

|                       |                                                                                                                                           |
|-----------------------|-------------------------------------------------------------------------------------------------------------------------------------------|
| **Use Case Name**     | Manage Task                                                                                                                               |
| **Trigger**           | The User chooses to delete a Task from the database.                                                                                      |
| **Precondition**      | 1.  The User has logged in.                                                                                                               

                         2.  The User has chosen to delete the Task.                                                                                                |
| **Basic Flow**        | 1.  The system accesses the Hawkengine database.                                                                                          

                         2.  The system deletes the Task from the database.                                                                                         

                         3.  The system notifies the User that the Task has been deleted.                                                                           |
| **Alternative Flows** | 1.  At Basic Flow step 2, the system checks whether the Task exists. If it does not, an error message “Task does not exist.” is returned. |
| **Postcondition**     | The Task has been deleted from the database.                                                                                              |

1.  *Use Case \#006 - Manage Materials*
    -----------------------------------

    1.  ### Add Material

|                       |                                                                                                                                                                                                           |
|-----------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Use Case Name**     | Manage Material                                                                                                                                                                                           |
| **Trigger**           | The User chooses to add Material.                                                                                                                                                                         |
| **Precondition**      | 1.  The User has logged in.                                                                                                                                                                               

                         2.  The User has chosen to add new Material.                                                                                                                                                               |
| **Basic Flow**        | 1.  The system validates the data entered by the User.                                                                                                                                                    

                         2.  The system accesses the Hawkengine database.                                                                                                                                                           

                         3.  The system saves the Material to the database.                                                                                                                                                         

                         4.  The system returns the newly created Material.                                                                                                                                                         |
| **Alternative Flows** | 1.  At Basic Flow step 1, if the entered data is invalid, the system returns an error with an appropriate message.                                                                                        

                         2.  At Basic Flow step 3, the system checks whether a Material with the same name within the database already exists. If it does, an error message “Material {materialName} already exists.” is returned.  |
| **Postcondition**     | The Material has been added to the database.                                                                                                                                                              |

###

### Update Material

|                       |                                                                                                                    |
|-----------------------|--------------------------------------------------------------------------------------------------------------------|
| **Use Case Name**     | Manage Material                                                                                                    |
| **Trigger**           | The User chooses to update the Material in the database.                                                           |
| **Precondition**      | 1.  The User has logged in.                                                                                        

                         2.  The User has chosen to update a Material.                                                                       |
| **Basic Flow**        | 1.  The system validates the data entered by the User.                                                             

                         2.  The system accesses the Hawkengine database.                                                                    

                         3.  The system saves the Material to the database.                                                                  

                         4.  The system returns the updated Material.                                                                        |
| **Alternative Flows** | 1.  At Basic Flow step 1, if the entered data is invalid, the system returns an error with an appropriate message. |
| **Postcondition**     | The Material has been updated.                                                                                     |

###

### Delete Material

|                       |                                                                                                                                                                 |
|-----------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Use Case Name**     | Manage Material                                                                                                                                                 |
| **Trigger**           | The User chooses to delete a Material                                                                                                                           |
| **Precondition**      | 1.  The User has logged in.                                                                                                                                     

                         2.  The User has chosen to delete a Material.                                                                                                                    |
| **Basic Flow**        | 1.  The system accesses the Hawkengine database.                                                                                                                

                         2.  The system deletes the Material from the database.                                                                                                           

                         3.  The system notifies the User that a Material has been deleted.                                                                                               |
| **Alternative Flows** | 1.  At Basic Flow step 2, the system checks whether the Material exists. If it does not, an error message “Material{materialName} does not exist.” is returned. |
| **Postcondition**     | Material has been deleted from the database.                                                                                                                    |

###

1.  *Use Case \#007 -* *Manage* [*Agents*](#manage-agent-007)
    ---------------------------------------------------------

    1.  ### Enable Agent

|                       |                                                                                                                                                                 |
|-----------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Use Case Name**     | Manage Agent                                                                                                                                                    |
| **Trigger**           | The User chooses to enable an Agent in the database.                                                                                                            |
| **Precondition**      | 1.  The User has logged in.                                                                                                                                     

                         2.  The User has chosen to enable the Agent.                                                                                                                     |
| **Basic Flow**        | 1.  The system validates the data entered by the User.                                                                                                          

                         2.  The system accesses the Hawkengine database.                                                                                                                 

                         3.  The system sets the Agent to “Enabled”.                                                                                                                      

                         4.  The system saves the Agent to the database.                                                                                                                  

                         5.  The system returns the Agent.                                                                                                                                |
| **Alternative Flows** | 1.  At Basic Flow step 1, if the entered data is invalid, an error message “Invalid data” is returned.                                                          

                         2.  At Basic Flow step 3, the system checks whether the Agent already exists. If it does not, an error message “Agent {agentName} does not exist.” is returned.  |
| **Postcondition**     | The Agent has been enabled in the database.                                                                                                                     |

###

### Delete Agent

|                       |                                                                                                                                                         |
|-----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Use Case Name**     | Manage Agent                                                                                                                                            |
| **Trigger**           | The User chooses to delete an Agent from the database.                                                                                                  |
| **Precondition**      | 1.  The User has logged in.                                                                                                                             

                         2.  The User has chosen to delete the Agent.                                                                                                             |
| **Basic Flow**        | 1.  The system accesses the Hawkengine database.                                                                                                        

                         2.  The system deletes the Agent from the database.                                                                                                      

                         3.  The system notifies the User that the Agent has been deleted.                                                                                        |
| **Alternative Flows** | 1.  At Basic Flow step 2, the system checks whether the Agent exists. If it does not, an error message “Agent {agentName} does not exist.” is returned. |
| **Postcondition**     | The Agent has been deleted from the database.                                                                                                           |

### Add Resource

|                       |                                                                                                                                                                                                             |
|-----------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Use Case Name**     | Manage Agent                                                                                                                                                                                                |
| **Trigger**           | The User chooses to add a new Resource to the database.                                                                                                                                                     |
| **Precondition**      | 1.  The User has logged in.                                                                                                                                                                                 

                         2.  The User has chosen to add a new Resource.                                                                                                                                                               |
| **Basic Flow**        | 1.  The system validates the data entered by the User.                                                                                                                                                      

                         2.  The system accesses the Hawkengine database.                                                                                                                                                             

                         3.  The system saves the Resource to the database.                                                                                                                                                           

                         4.  The system returns the newly created Resource.                                                                                                                                                           |
| **Alternative Flows** | 1.  At Basic Flow step 1, if the entered data is invalid, the system returns an error with an appropriate message.                                                                                          

                         2.  At Basic Flow step 3, the system checks whether a Resource with the same name within the same Agent already exists. If it does, an error message “Resource {resourceName} already exists.” is returned.  |
| **Postcondition**     | The Resource has been added to the database.                                                                                                                                                                |

###

### Delete Resource

|                       |                                                                                                                                                                  |
|-----------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Use Case Name**     | Manage Agent                                                                                                                                                     |
| **Trigger**           | The User chooses to delete a Resource from the database.                                                                                                         |
| **Precondition**      | 1.  The User has logged in.                                                                                                                                      

                         2.  The User has selected a Resource within the Agent.                                                                                                            

                         3.  The User has chosen to delete a Resource.                                                                                                                     |
| **Basic Flow**        | 1.  The system accesses the Hawkengine database.                                                                                                                 

                         2.  The system deletes the Resource from the database.                                                                                                            

                         3.  The system notifies the User that the Resource has been deleted.                                                                                              |
| **Alternative Flows** | 1.  At Basic Flow step 2, the system checks whether the Resource exists. If it does not, an error message “Resource {resourceName} does not exist.” is returned. |
| **Postcondition**     | The Resource has been deleted from the database.                                                                                                                 |

1.  *Use Case \#008 - Manage Environment Variable*
    ----------------------------------------------

    1.  ***Add Environment Variable***

|                       |                                                                                                        |
|-----------------------|--------------------------------------------------------------------------------------------------------|
| **Use Case Name**     | Manage Environment Variable                                                                            |
| **Trigger**           | The User chooses to add a new Environment Variable to the database.                                    |
| **Precondition**      | 1.  The User has logged in.                                                                            

                         2.  The User has chosen to add a new Environment Variable.                                              |
| **Basic Flow**        | 1.  The system validates the data entered by the User.                                                 

                         2.  The system accesses the Hawkengine database.                                                        

                         3.  The system saves the Environment Variable to the database.                                          

                         4.  The system returns the newly created Environment Variable.                                          |
| **Alternative Flows** | 1.  At Basic Flow step 1, if the entered data is invalid, an error message “Invalid data” is returned. |
| **Postcondition**     | The Environment Variable has been added to the database.                                               |

###

### Update Environment Variable

|                       |                                                                                                                                                                                   |
|-----------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Use Case Name**     | Manage Environment Variable                                                                                                                                                       |
| **Trigger**           | The User chooses to update an Environment Variable in the database.                                                                                                               |
| **Precondition**      | 1.  The User has logged in.                                                                                                                                                       

                         2.  The User has chosen to update the Environment Variable.                                                                                                                        |
| **Basic Flow**        | 1.  The system validates the data entered by the User.                                                                                                                            

                         2.  The system accesses the Hawkengine database.                                                                                                                                   

                         3.  The system saves the Environment Variable to the database.                                                                                                                     

                         4.  The system returns the updated Environment Variable.                                                                                                                           |
| **Alternative Flows** | 1.  At Basic Flow step 1, if the entered data is invalid, an error message “Invalid data” is returned.                                                                            

                         2.  At Basic Flow step 3, the system checks whether the Environment Variable already exists. If it does not, an error message “Environment Variable does not exist.” is returned.  |
| **Postcondition**     | The Environment Variable has been updated in the database.                                                                                                                        |

###

### Delete Environment Variable

|                       |                                                                                                                                                                           |
|-----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Use Case Name**     | Manage Environment Variable                                                                                                                                               |
| **Trigger**           | The User chooses to delete an Environment Variable from the database.                                                                                                     |
| **Precondition**      | 1.  The User has logged in.                                                                                                                                               

                         2.  The User has chosen to delete the Environment Variable.                                                                                                                |
| **Basic Flow**        | 1.  The system accesses the Hawkengine database.                                                                                                                          

                         2.  The system deletes the Environment Variable from the database.                                                                                                         

                         3.  The system notifies the User that the Environment Variable has been deleted.                                                                                           |
| **Alternative Flows** | 1.  At Basic Flow step 2, the system checks whether the Environment Variable exists. If it does not, an error message “Environment Variable does not exist.” is returned. |
| **Postcondition**     | The Environment Variable has been deleted from the database.                                                                                                              |

1.  *Use Case \#009 - Manage Environment*
    -------------------------------------

    1.  ### Add Environment

|                       |                                                                                                        |
|-----------------------|--------------------------------------------------------------------------------------------------------|
| **Use Case Name**     | Manage Environment                                                                                     |
| **Trigger**           | The User chooses to add a new Environment to the database.                                             |
| **Precondition**      | 1.  The User has logged in.                                                                            

                         2.  The User has chosen to add a new Environment.                                                       |
| **Basic Flow**        | 1.  The system validates the data entered by the User.                                                 

                         2.  The system accesses the Hawkengine database.                                                        

                         3.  The system saves the Environment to the database.                                                   

                         4.  The system returns the newly created Environment.                                                   |
| **Alternative Flows** | 1.  At Basic Flow step 1, if the entered data is invalid, an error message “Invalid data” is returned. |
| **Postcondition**     | The Environment has been added to the database.                                                        |

###

### Update Environment

|                       |                                                                                                                                                                          |
|-----------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Use Case Name**     | Manage Environment                                                                                                                                                       |
| **Trigger**           | The User chooses to update an Environment in the database.                                                                                                               |
| **Precondition**      | 1.  The User has logged in.                                                                                                                                              

                         2.  The User has chosen to update the Environment.                                                                                                                        |
| **Basic Flow**        | 1.  The system validates the data entered by the User.                                                                                                                   

                         2.  The system accesses the Hawkengine database.                                                                                                                          

                         3.  The system saves the Environment to the database.                                                                                                                     

                         4.  The system returns the updated Environment.                                                                                                                           |
| **Alternative Flows** | 1.  At Basic Flow step 1, if the entered data is invalid, an error message “Invalid data” is returned.                                                                   

                         2.  At Basic Flow step 3, the system checks whether the Environment Variable already exists. If it does not, an error message “Environment does not exist.” is returned.  |
| **Postcondition**     | The Environment has been updated in the database.                                                                                                                        |

###

### Delete Environment

|                       |                                                                                                                                                         |
|-----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Use Case Name**     | Manage Environment                                                                                                                                      |
| **Trigger**           | The User chooses to delete an Environment from the database.                                                                                            |
| **Precondition**      | 1.  The User has logged in.                                                                                                                             

                         2.  The User has chosen to delete the Environment.                                                                                                       |
| **Basic Flow**        | 1.  The system accesses the Hawkengine database.                                                                                                        

                         2.  The system deletes the Environment from the database.                                                                                                

                         3.  The system notifies the User that the Environment has been deleted.                                                                                  |
| **Alternative Flows** | 1.  At Basic Flow step 2, the system checks whether the Environment exists. If it does not, an error message “Environment does not exist.” is returned. |
| **Postcondition**     | The Environment has been deleted from the database.                                                                                                     |

Other Requirements
==================

The server should support 2 types of databases: MongoDB and Redis. Redis is in memory store that can be used for small scale projects that would enable easy setup and run scenarios. Its main limitation is the memory of the machine it runs on. MongoDb is a fully fledged document based database engine that will be used in enterprise deployments.

<span id="h.4f1mdlm" class="anchor"></span>**Appendix A: Glossary**

<span id="h.19c6y18" class="anchor"></span>**Appendix C: To Be Determined List**

*&lt;Collect a numbered list of the TBD (to be determined) references that remain in the SRS so they can be tracked to closure.&gt;*
