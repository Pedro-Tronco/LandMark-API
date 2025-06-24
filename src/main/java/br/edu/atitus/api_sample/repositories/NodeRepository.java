package br.edu.atitus.api_sample.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.atitus.api_sample.entities.NodeEntity;

@Repository
public interface NodeRepository extends JpaRepository<NodeEntity, UUID>{
	
	List<NodeEntity> findByPointId(UUID id);
	
	Optional<NodeEntity> findById(UUID id);

}
