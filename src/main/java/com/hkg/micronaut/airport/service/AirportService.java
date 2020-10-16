package com.hkg.micronaut.airport.service;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.hkg.micronaut.airport.entity.Airport;
import com.hkg.micronaut.airport.repository.AirportRepository;

@Singleton
public class AirportService {

	@Inject
	AirportRepository airportRepository;

	/**
	 * 
	 * @return
	 */
	public Iterable<Airport> getAllAirports() {
		return airportRepository.findAll();
	}

	/**
	 * 
	 */
	public Airport getAirportByCode(String airportCode) {
		return airportRepository.findByAirportCode(airportCode);
	}

	/**
	 * 
	 * @param airport
	 * @return
	 */
	public Airport saveOrupdateAirport(Airport airport) {

		Airport airportSaved = null;

		if (airport.getId()!=null && airportRepository.existsById(airport.getId())) {
			airportSaved = airportRepository.update(airport);
		} else {
			airportSaved = airportRepository.save(airport);
		}

		return airportSaved;

	}

	/**
	 * 
	 * @param airportId
	 */
	public void deleteAirport(Long airportId) {
		airportRepository.deleteById(airportId);
	}

}
