package io.hawkcd.core.anotations;

/**
 * Created by rado on 11.11.16.
 */
public @interface Authorization {
    Scope scope();
    Permission permission();
    EntityType permissionEntityType();
}


/*
* Defines the permission
*/
enum Scope {
    Server,
    PipelineGroup,
    Pipeldine
}

/*
* Defines what user can do for a given scope and/or entity e.g. PipelineGroup, Pipeline
*/
enum Permission {
    Admin,
    Operator,
    Viewer
}

/*
* Defines the entity type a permissin is applied to, e.g.  A user may have "Admin" permission for concrete Pipeline Object
*/

enum EntityType {
    PipelineGroup,
    Pipeline,
}