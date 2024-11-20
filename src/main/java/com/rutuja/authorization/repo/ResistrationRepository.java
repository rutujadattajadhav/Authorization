package com.rutuja.authorization.repo;

import com.rutuja.authorization.bean.ResistrationBean;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResistrationRepository extends ReactiveCrudRepository<ResistrationBean,String> {
}
