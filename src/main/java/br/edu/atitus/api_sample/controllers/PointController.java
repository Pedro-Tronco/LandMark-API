package br.edu.atitus.api_sample.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.atitus.api_sample.dtos.PointDTO;
import br.edu.atitus.api_sample.entities.PointEntity;
import br.edu.atitus.api_sample.services.NodeService;
import br.edu.atitus.api_sample.services.PointService;

@RestController
@RequestMapping("/ws/point")
public class PointController {
	private PointService service;
	private NodeService nodeService;
	
	public PointController(PointService service, NodeService nodeService) {
		super();
		this.service = service;
		this.nodeService = nodeService;
	}
	
	@GetMapping()
	public ResponseEntity<List<PointEntity>> findAll() {
		return ResponseEntity.ok().body(service.findAll());
	}
	
	@PostMapping()
	public ResponseEntity<PointEntity> postPoint(@RequestBody PointDTO dto) throws Exception {
		PointEntity point = new PointEntity();
		BeanUtils.copyProperties(dto, point);
		service.save(point);
		return ResponseEntity.status(HttpStatus.CREATED).body(point);
	}
	
	@DeleteMapping("/{idPath}")
	public ResponseEntity<String> deletePoint(
			@RequestParam(required = false) UUID id,
			@PathVariable(required = false) UUID idPath
			) throws Exception {
		
		if (idPath == null)
				throw new Exception("O caminho deve especificar um id de ponto seu guei");
		id = idPath;
		nodeService.deleteByPointId(id);
		service.deleteById(id);
		return ResponseEntity.ok().body("Ponto " + id + " deletado com sucesso!");
	} 
	
	@PutMapping("/{idPath}")
	public ResponseEntity<PointEntity> putPoint(
			@RequestParam(required = false) UUID id,
			@PathVariable(required = false) UUID idPath,
			@RequestBody PointDTO dto
			) throws Exception {
		if (idPath == null)
			throw new Exception("O caminho deve especificar um id de ponto seu guei");
		id = idPath;
		
		PointEntity point = service.alterById(id, dto.lat(), dto.lng(), dto.description());
		
		return ResponseEntity.ok().body(point);
	}
	
	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<String> handlerException(Exception ex) {
		String message = ex.getMessage().replaceAll("\r\n", "");
		return ResponseEntity.badRequest().body(message);
	}
}
