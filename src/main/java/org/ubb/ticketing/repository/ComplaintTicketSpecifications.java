package org.ubb.ticketing.repository;

import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.ubb.ticketing.domain.TicketStatus;
import org.ubb.ticketing.domain.complaint.ComplaintTicket;

import java.util.UUID;

public class ComplaintTicketSpecifications {


    public static Specification<ComplaintTicket> containsKeyword(String keyword) {
        return (root, query, cb) -> {
            String likePattern = "%" + keyword.toLowerCase() + "%";


            return cb.or(
                    cb.like(cb.lower(root.get("description")), likePattern),
                    cb.like(cb.lower(root.get("ticketStatus").as(String.class)), likePattern),
                    cb.like(cb.lower(root.join("assignedTo", JoinType.LEFT).get("username")), likePattern),
                    cb.like(cb.lower(root.join("createdBy", JoinType.LEFT).get("username")), likePattern),
                    cb.like(cb.lower(root.join("closedBy", JoinType.LEFT).get("username")), likePattern),
                    cb.like(cb.lower(root.join("ticketElement", JoinType.LEFT).get("name")), likePattern)
            );
        };
    }

    public static Specification<ComplaintTicket> hasStatus(String status) {
        return (root, query, cb) ->
                cb.equal(root.get("ticketStatus"), TicketStatus.valueOf(status));
    }

    public static Specification<ComplaintTicket> assignedToUser(String userId) {
        return (root, query, cb) -> {
            UUID uuid = UUID.fromString(userId);
            return cb.equal(root.get("assignedTo").get("id"), uuid);
        };
    }
}


