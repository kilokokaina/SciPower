package com.mesi.scipower.service.impl;

import com.mesi.scipower.model.ProjectModel;
import com.mesi.scipower.repository.ProjectRepository;
import com.mesi.scipower.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public ProjectModel save(ProjectModel project) {
        return projectRepository.save(project);
    }

    @Override
    public ProjectModel findById(Long projectId) {
        return projectRepository.findById(projectId).orElse(null);
    }

    @Override
    public void delete(ProjectModel project) {
        projectRepository.delete(project);
    }

    @Override
    public void deleteById(Long projectId) {
        projectRepository.deleteById(projectId);
    }
}
