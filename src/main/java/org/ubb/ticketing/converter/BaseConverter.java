package org.ubb.ticketing.converter;

import java.util.List;

public abstract class BaseConverter<T, U> {

    abstract T convertDtoToModel(U dto);
    abstract U convertModelToDto(T entity);

    public List<U> convertModelListToDtoList(List<T> models) {
        return models.stream().map(this::convertModelToDto).toList();
    }

    public List<T> convertDtoListToModelList(List<U> dtos) {
        return dtos.stream().map(this::convertDtoToModel).toList();
    }

}
