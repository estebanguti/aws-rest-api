package com.rest.demoapi.mapper;

import com.rest.demoapi.model.ImageDto;
import com.rest.demoapi.repository.entity.ImageEntity;
import com.rest.demoapi.util.S3Util;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    ImageDto toClientModel(ImageEntity entityModel);

    ImageEntity toEntityModel(ImageDto clientModel);

}
