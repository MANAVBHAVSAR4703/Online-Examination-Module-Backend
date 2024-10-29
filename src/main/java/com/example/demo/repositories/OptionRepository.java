package com.example.demo.repositories;

import com.example.demo.models.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository extends JpaRepository<Option,Long> {

}
