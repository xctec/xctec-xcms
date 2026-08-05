package com.df4j.xctec.xcms.common.jpa.converter;

import org.mapstruct.MappingTarget;

public interface ModelConverter<E, D, F> {

    D toDto(E entity);

    F toForm(E entity);

    E toEntity(F form);

    void setEntity(F form, @MappingTarget E entity);
}
