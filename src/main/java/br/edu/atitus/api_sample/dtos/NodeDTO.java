package br.edu.atitus.api_sample.dtos;

import br.edu.atitus.api_sample.entities.PointEntity;

public record NodeDTO(double lat, double lng, PointEntity point) {
	
}
