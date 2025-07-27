package org.ubb.ticketing.controller;

import lombok.*;

import java.io.Serializable;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> implements Serializable {

    private String message;
    private T data;
}
