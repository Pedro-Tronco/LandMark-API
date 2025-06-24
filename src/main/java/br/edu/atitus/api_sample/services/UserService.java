package br.edu.atitus.api_sample.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.edu.atitus.api_sample.entities.UserEntity;
import br.edu.atitus.api_sample.repositories.UserRepository;

@Service
public class UserService implements UserDetailsService {
	
	private final UserRepository repository;
	private final PasswordEncoder encoder;
	
	public UserService(UserRepository repository, PasswordEncoder encoder) {
		super();
		this.repository = repository;
		this.encoder = encoder;
	}

	public UserEntity save(UserEntity user) throws Exception {
		
		if(user == null) 
			throw new Exception("Objeto não pode ser nulo");
		
		if(user.getName() == null || user.getName().isEmpty())
			throw new Exception("Nome inválido!");
		user.setName(user.getName().trim());
		
		if(user.getEmail() == null || user.getEmail().isEmpty())
			throw new Exception("E-mail inválido!");
		String regex = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
	    if(user.getEmail() == null 
	    || user.getEmail().isEmpty() 
	    || !user.getEmail().matches(regex))
	    	throw new Exception("E-mail inválido");
		user.setEmail(user.getEmail().trim().toLowerCase());
		if(repository.existsByEmail(user.getEmail()))
			throw new Exception("E-mail já cadastrado");
		
		String regexPassword = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$";
		if(user.getPassword() == null 
			|| user.getPassword().isEmpty()
			|| user.getPassword().length() < 8
			||!user.getPassword().matches(regexPassword))
			throw new Exception("Senha inválida! A senha precisa ter mais de 8 caractéres, letra maíuscula, minúscula, e um número");
		user.setPassword(encoder.encode(user.getPassword()));
		
		if(user.getType() == null)
			throw new Exception("Tipo de usuário inválido");
		
		repository.save(user);
		
		return user;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var user = repository.findByEmail(username)
		.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
		return user;
	}
	
}
