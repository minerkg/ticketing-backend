package org.ubb.ticketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ubb.ticketing.domain.SolutionType;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketCloseRequest implements Serializable {

    private SolutionType solutionType;

    private String solutionDescription;
}
