package org.ubb.ticketing.repository;

import org.springframework.data.jpa.domain.Specification;
import org.ubb.ticketing.domain.complaint.ComplaintTicket;

public class ComplaintTicketSpecifications {


    public static Specification<ComplaintTicket> containsKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String likePattern = "%" + keyword.toLowerCase() + "%";

            return  criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("ticketStatus").as(String.class)), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("solutionDescription")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("createdBy").get("username")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("assignedTo").get("username")), likePattern)
            );
        };
    }
}


