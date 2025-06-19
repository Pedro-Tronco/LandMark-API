package br.edu.atitus.api_sample.controllers;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.atitus.api_sample.components.JWTUtil;
import br.edu.atitus.api_sample.dtos.SigninDTO;
import br.edu.atitus.api_sample.dtos.SignupDTO;
import br.edu.atitus.api_sample.entities.UserEntity;
import br.edu.atitus.api_sample.entities.UserType;
import br.edu.atitus.api_sample.services.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private final UserService service;
	private final AuthenticationConfiguration authConfig;
	
	public AuthController(UserService service, AuthenticationConfiguration authConfig) {
		super();
		this.service = service;
		this.authConfig = authConfig;
	}


	@PostMapping("/signup")
	public ResponseEntity<UserEntity> signup(@RequestBody SignupDTO dto) throws Exception {
		UserEntity user = new UserEntity();
		BeanUtils.copyProperties(dto, user);
		user.setType(UserType.Common);
		service.save(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(user);
		}
	
	@PostMapping("/signin")
	public ResponseEntity<String> signin(@RequestBody SigninDTO dto) {
		try {
			authConfig.getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(dto.email(), dto.password()));
		} catch (AuthenticationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok(JWTUtil.generateToken(dto.email()));
	}
		
	
	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<String> handlerException(Exception ex) {
		String message = ex.getMessage().replaceAll("\r\n", "");
		return ResponseEntity.badRequest().body(message);
	}
}
