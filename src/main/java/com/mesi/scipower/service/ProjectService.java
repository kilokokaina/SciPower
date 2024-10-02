package com.mesi.scipower.service;

import com.mesi.scipower.model.ProjectModel;

public interface ProjectService {

    ProjectModel save(ProjectModel project);
    ProjectModel findById(Long projectId);
    void delete(ProjectModel project);
    void deleteById(Long projectId);

}
