package com.socaly.mapper;

import com.socaly.dto.CommunityDto;
import com.socaly.entity.Community;
import com.socaly.entity.Post;
import com.socaly.entity.User;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommunityMapper {
    @Mapping(target = "numberOfPosts", expression = "java(mapPosts(community.getPosts()))")
    @Mapping(target = "numberOfUsers", expression = "java(community.getUsers().size())")
    CommunityDto mapCommunityToDto(Community community);

    default Integer mapPosts(List<Post> numberOfPosts) {
        return numberOfPosts.size();
    }

    @InheritInverseConfiguration
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "users", expression = "java(new java.util.ArrayList<>())")
    Community mapDtoToCommunity(CommunityDto communityDto, User user);
}
