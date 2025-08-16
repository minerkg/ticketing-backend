package org.ubb.ticketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketCloseRequest implements Serializable {

    private String solutionTypeName;

    private String solutionDescription;
}
