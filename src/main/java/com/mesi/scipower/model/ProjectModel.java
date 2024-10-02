package com.mesi.scipower.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class ProjectModel {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long projectId;

    private String projectName;
    private String projectDescription;

}
