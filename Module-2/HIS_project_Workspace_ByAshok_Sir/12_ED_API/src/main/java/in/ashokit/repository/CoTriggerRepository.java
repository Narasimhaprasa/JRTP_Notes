package in.ashokit.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import in.ashokit.entity.CoTriggerEntity;

public interface CoTriggerRepository extends JpaRepository<CoTriggerEntity, Serializable> {

}
