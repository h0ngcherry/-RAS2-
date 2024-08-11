package com.example.documentReview.mapper;

import com.example.documentReview.domain.ApprFile;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApprFileMapper {

    void delAprFile(String id);

    List<ApprFile> findByUserName(String userName);

    ApprFile findById(String id);

    List<ApprFile> findAll();

    void insert(ApprFile apprFile);

    void update(ApprFile apprFile);
}
