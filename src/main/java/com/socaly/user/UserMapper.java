package com.socaly.user;

import com.socaly.image.Image;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Mapping(target = "profileImage", expression = "java(mapImageToString(user.getProfileImage()))")
    public abstract UserResponse mapToUserResponse(User user);

    Image mapStringToImage(String string) {
        Image image = new Image();
        image.setImageUrl(string);

        return image;
    }

    String mapImageToString(Image image) {
        if (image == null) return null;
        return image.getImageUrl();
    }
}
