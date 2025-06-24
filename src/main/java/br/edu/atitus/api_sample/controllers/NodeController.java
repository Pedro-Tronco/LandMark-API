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

import br.edu.atitus.api_sample.dtos.NodeDTO;
import br.edu.atitus.api_sample.entities.NodeEntity;
import br.edu.atitus.api_sample.services.NodeService;

@RestController
@RequestMapping("/ws/node")
public class NodeController {
	private final NodeService service;
	
	public NodeController (NodeService service) {
		super();
		this.service = service;
	}
	
	@PostMapping()
	public ResponseEntity<NodeEntity> postNode(@RequestBody NodeDTO dto) throws Exception {
		NodeEntity node = new NodeEntity();
		BeanUtils.copyProperties(dto, node);
		service.save(node);
		return ResponseEntity.status(HttpStatus.CREATED).body(node);
	}
	
	@GetMapping("/{idPath}")
	public ResponseEntity<List<NodeEntity>> getByPointId(
			@RequestParam(required = false) UUID id,
			@PathVariable(required = false) UUID idPath
			) throws Exception {
		
		if (idPath == null)
			throw new Exception("O caminho deve especificar um id de ponto seu guei");
		id = idPath;
		return ResponseEntity.ok().body(service.findByPointId(id));
	}
	
	@DeleteMapping("/{idPath}")
	public ResponseEntity<String> deleteNode(
			@RequestParam(required = false) UUID id,
			@PathVariable(required = false) UUID idPath
			) throws Exception {
		
		if (idPath == null)
				throw new Exception("O caminho deve especificar um id de ponto seu guei");
		id = idPath;
		service.deleteById(id);
		return ResponseEntity.ok().body("Node " + id + " deletado com sucesso!");
	} 
	
	@PutMapping("/{idPath}")
	public ResponseEntity<NodeEntity> putNode(
			@RequestParam(required = false) UUID id,
			@PathVariable(required = false) UUID idPath,
			@RequestBody NodeDTO dto
			) throws Exception {
		if (idPath == null)
			throw new Exception("O caminho deve especificar um id de ponto seu guei");
		id = idPath;
		
		NodeEntity node = service.alterById(id, dto.lat(), dto.lng());
		
		return ResponseEntity.ok().body(node);
	}
	
	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<String> handlerException(Exception ex) {
		String message = ex.getMessage().replaceAll("\r\n", "");
		return ResponseEntity.badRequest().body(message);
	}
}
