package com.hkg.mircronaut.controller;

import javax.inject.Inject;

import com.hkg.micronaut.airport.entity.Airport;
import com.hkg.micronaut.airport.service.AirportService;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Put;

@Controller("/airport")
public class AirportController {
	
	/**
	 * 
	 */
	@Inject
	AirportService airportService;
	
	/**
	 * 	
	 * @return
	 */
	@Get("/getAll")
    @Produces(MediaType.APPLICATION_JSON) 
    public Iterable<Airport> getAll() {
        return  airportService.getAllAirports();
	}
	
	/**
	 * 
	 * @param airportCode
	 * @return
	 */
	@Get("/find/{airportCode}") 
    @Produces(MediaType.APPLICATION_JSON) 
	public Airport getAirportByCode(String airportCode) {
		return airportService.getAirportByCode(airportCode);
	}
	
	/**
	 * 
	 * @param airport
	 * @return
	 */
	@Post("/saveAirport")
	@Produces(MediaType.APPLICATION_JSON) 
	public Airport saveOrUpdate(@Body Airport airport) {
		return airportService.saveOrupdateAirport(airport);
		
	}
	
	/**
	 * 
	 * @param airportId
	 * @return
	 */
	@Delete("delete/{airportId}")
	public HttpResponse delete(Long airportId) {
		airportService.deleteAirport(airportId);
        return HttpResponse.ok();
	}
	
}
