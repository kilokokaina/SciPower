package com.mesi.scipower.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@ToString(includeFieldNames = false)
public class ParseDocument implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String authors;
    private String authorFullNames;
    private String authorID;
    private String title;
    private String year;
    private String sourceTitle;
    private String volume;
    private String issue;
    private String artNo;
    private String pageStart;
    private String pageEnd;
    private String pageCount;
    private String citedBy;
    private String DOI;
    private String link;
    private String affiliations;
    private String authorsWithAffiliations;
    private String abstracts;
    private String authorKeywords;
    private String indexKeywords;
    private String molecularSequenceNumbers;
    private String chemicalsCAS;
    private String tradenames;
    private String manufacturers;
    private String fundingDetails;
    private String fundingTexts;
    private String references;
    private String correspondenceAddress;
    private String editors;
    private String publisher;
    private String sponsors;
    private String conferenceName;
    private String conferenceDate;
    private String conferenceLocation;
    private String conferenceCode;
    private String ISSN;
    private String ISBN;
    private String CODEN;
    private String pubMedID;
    private String languageOfOriginalDocument;
    private String abbreviatedSourceTitle;
    private String documentType;
    private String publicationStage;
    private String openAccess;
    private String source;
    private String EID;

}
