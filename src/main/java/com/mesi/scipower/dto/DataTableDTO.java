package com.mesi.scipower.dto;

import com.mesi.scipower.model.ParseDocument;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DataTableDTO {

    private int draw;
    private int recordsTotal;
    private int recordsFiltered;
    private ParseDocument[] data;
    private String error;

}
