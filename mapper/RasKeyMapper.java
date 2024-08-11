package com.example.documentReview.mapper;

import com.example.documentReview.domain.RasKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RasKeyMapper {

    public List<RasKey> findByUserId(String userName);

    public List<RasKey> findAll();

    public void insert(RasKey rasKey);
}
