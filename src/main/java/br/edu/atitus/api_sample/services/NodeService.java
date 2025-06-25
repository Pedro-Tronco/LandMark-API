package br.edu.atitus.api_sample.services;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.edu.atitus.api_sample.entities.NodeEntity;
import br.edu.atitus.api_sample.entities.PointEntity;
import br.edu.atitus.api_sample.entities.UserEntity;
import br.edu.atitus.api_sample.repositories.NodeRepository;
import br.edu.atitus.api_sample.repositories.PointRepository;
import jakarta.transaction.Transactional;

@Service
public class NodeService {

	private final NodeRepository repository;
	private final PointRepository pointRepo;
	
	public NodeService(NodeRepository repository, PointRepository pointRepo) {
		super();
		this.repository = repository;
		this.pointRepo = pointRepo;
	}
	
	@Transactional
	public NodeEntity save(NodeEntity node) throws Exception {
		
		if(node == null)
			throw new Exception("Objeto não pode ser vazio!");
		
		if(!(node.getLat() >= -90 && node.getLat() <= 90))
			throw new Exception("Latitude deve estar entre -90 e 90!");
		
		if(!(node.getLng() >= -180 && node.getLng() <= 180))
			throw new Exception("Longitude deve estar entre -180 e 180!");
		
		UserEntity userAuth = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (pointRepo.findByUser(userAuth).isEmpty())
			throw new Exception("Você não tem permissão para alterar este registro");
		
		return repository.save(node);
	}
	
	public List<NodeEntity> findByPointId(UUID id) throws Exception {
		PointEntity point = pointRepo.findById(id)
				.orElseThrow(() -> new Exception("Não existe ponto cadastrado com este ID"));
		
		UserEntity userAuth = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!point.getUser().getId().equals(userAuth.getId()))
			throw new Exception("Você não tem permissão para alterar este registro");
		
		return repository.findByPointId(id);
	}
	
	@Transactional
	public void deleteById(UUID id) throws Exception {
		var node = repository.findById(id)
				.orElseThrow(() -> new Exception("Não existe node cadastrado com este ID"));
		
		//TODO: Add User Auth
		
		repository.deleteById(node.getId());
	}
	
	@Transactional
	public void deleteByPointId(UUID pointId) throws Exception {
		List<NodeEntity> nodes = repository.findByPointId(pointId);
		
		UserEntity userAuth = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		for (NodeEntity node : nodes) {
			if (!node.getPoint().getUser().equals(userAuth))
				throw new Exception("Você não tem permissão para apagar este registro");
		
			UUID id = node.getId();
			repository.deleteById(id);
		}
	}

	public NodeEntity alterById(UUID id, double lat, double lng) throws Exception{
		
		var node = repository.findById(id)
				.orElseThrow(() -> new Exception("Não existe node cadastrado com este ID"));
		
		//TODO: Add User Auth
		
		if(!(node.getLat() >= -90 && node.getLat() <= 90))
			throw new Exception("Latitude deve estar entre -90 e 90!");
		
		if(!(node.getLng() >= -180 && node.getLng() <= 180))
			throw new Exception("Longitude deve estar entre -180 e 180!");
		
		node.setLat(lat);
		node.setLng(lng);
		repository.save(node);
		
		return node;
		}
}
