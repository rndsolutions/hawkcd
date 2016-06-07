# Hawkengine Test Cases
-------------------------
## Login
----------------

### Login with valid credentials  
 1. Go to the server login screen
 2. Enter valid credentials
 3. Then you should be logged into the system

### Login with invalid credentials 
 1. Go to the server login screen
 2. Enter in-valid credentials
 3. Then you should receive a message "wrong username or password"
 
-------------------------
## Admin\Group Management
-------------------------
### Add new pipeline group
### Delete pipeline group
### Rename pipeline group
### Add pipeline group with duplicate name
### Browser tab name synchronization
 
-------------------------
## Admin\User Management
-------------------------
### Add new user
### Enable a user
### Delete a user
### Edit a user
### Add new user with duplicate name
### Search users
### Sort users
### Browser tab name synchronization
 
---------
## Agents
---------
### Enable and disable agent
### Environment variables are accessible on agent from all levels (pipeline, stage & job)
### Delete agent
### Agent auto register functionality
### Edit agent resources
### Agent status
### Agent host information
### Search agents
### Show various agents records 
### Sort agents
### Start stop agent to test UI is reflecting the state  
 
------------
## Pipelines
------------
### Add new pipeline from pipeline group
### Browser tab name synchronization
### Start pipeline from pipeline group
### Start pipeline with specific arguments
### Pause pipeline
### Stop pipeline
### Pipeline history button
### Parallel Pipeline Execution on multiple agents
### Pipeline edit button
### Pipeline delete button
### Pipeline execution history grid
 
---------
## Pipelines\Run Management
---------
### Rerun pipeline
### Rerun pipeline with advanced options
### Rerun pipeline stages
### Switch pipeline runs
### Switch between stage activity logs
### Edit pipeline button
### Run management breadcrumb
  
------------
## Pipelines\Edit
---------
### Add environment variables for stage
### Add secured environments variables for stage  
### Add environment variables for Job
### Add secured environments variables for Job 
### Add environment variables for pipeline
### Add secured environments variables for pipeline
### Add GIT material public repository
### Add GIT material private repository with credentials
### Edit existing GIT material
### Add NuGet material
### Edit existing NuGet material
 
 
