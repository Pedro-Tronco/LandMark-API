package br.edu.atitus.api_sample.services;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.edu.atitus.api_sample.entities.PointEntity;
import br.edu.atitus.api_sample.entities.UserEntity;
import br.edu.atitus.api_sample.repositories.PointRepository;
import jakarta.transaction.Transactional;

@Service
public class PointService {

	private final PointRepository repository;
	
	public PointService(PointRepository repository) {
		super();
		this.repository = repository;
	}
	
	@Transactional
	public PointEntity save(PointEntity point) throws Exception {
		
		if(point == null)
			throw new Exception("Objeto não pode ser vazio!");
		
		if(point.getDescription() == null || point.getDescription().isEmpty())
			throw new Exception("Descrição do ponto não pode ser vazia!");
		
		if(!(point.getLat() >= -90 && point.getLat() <= 90))
			throw new Exception("Latitude deve estar entre -90 e 90!");
		
		if(!(point.getLng() >= -180 && point.getLng() <= 180))
			throw new Exception("Longitude deve estar entre -180 e 180!");
		
		UserEntity userAuth = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		point.setUser(userAuth);
		
		return repository.save(point);
	}
	
	@Transactional
	public void deleteById(UUID id) throws Exception {
		var point = repository.findById(id)
				.orElseThrow(() -> new Exception("Não existe ponto cadastrado com este ID"));
		
		UserEntity userAuth = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if (!point.getUser().getId().equals(userAuth.getId()))
			throw new Exception("Você não tem permissão para apagar este registro");
		
		repository.deleteById(id);
	}
	
	public List<PointEntity> findAll() {
		UserEntity userAuth = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return repository.findByUser(userAuth);
	}
	
	public PointEntity alterById(UUID id, double lat, double lng, String description) throws Exception{
		
		var point = repository.findById(id)
				.orElseThrow(() -> new Exception("Não existe ponto cadastrado com este ID"));
		
		UserEntity userAuth = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if (!point.getUser().getId().equals(userAuth.getId()))
			throw new Exception("Você não tem permissão para alterar este registro");
		
		if(point.getDescription() == null || point.getDescription().isEmpty())
			throw new Exception("Descrição do ponto não pode ser vazia!");
		
		if(!(point.getLat() >= -90 && point.getLat() <= 90))
			throw new Exception("Latitude deve estar entre -90 e 90!");
		
		if(!(point.getLng() >= -180 && point.getLng() <= 180))
			throw new Exception("Longitude deve estar entre -180 e 180!");
		
		point.setDescription(description);
		point.setLat(lat);
		point.setLng(lng);
		repository.save(point);
		
		return point;	
	}
}