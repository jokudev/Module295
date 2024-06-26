package ch.kunz.joshua.flugbuchungsapi.controller;

import ch.kunz.joshua.flugbuchungsapi.data.Airline;
import ch.kunz.joshua.flugbuchungsapi.security.Roles;
import ch.kunz.joshua.flugbuchungsapi.service.AirlineService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/airline")
public class AirlineController {
    private AirlineService airlineService;

    public AirlineController(AirlineService airlineService) {
        this.airlineService = airlineService;
    }

    @RolesAllowed({Roles.User, Roles.Staff, Roles.Admin})
    @GetMapping
    public ResponseEntity<List<Airline>> getAirlines(@RequestParam(required = false) String country, @RequestParam(required = false) String name) {
        if (country != null && name != null) {
            return new ResponseEntity<>(airlineService.findByNameAndCountry(name, country), HttpStatus.OK);
        } else if (country != null) {
            return new ResponseEntity<>(airlineService.findByCountry(country), HttpStatus.OK);
        } else if (name != null) {
            return new ResponseEntity<>(airlineService.findByName(name), HttpStatus.OK);
        }
        return new ResponseEntity<>(airlineService.findAll(), HttpStatus.OK);
    }

    @RolesAllowed({Roles.Staff, Roles.Admin})
    @PostMapping
    public ResponseEntity<Airline> createAirline(@RequestBody Airline airline) {
        return new ResponseEntity<>(airlineService.save(airline), HttpStatus.CREATED);
    }

    @RolesAllowed({Roles.User, Roles.Staff, Roles.Admin})
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Airline>> getAirlineById(@PathVariable Long id) {
        return new ResponseEntity<>(airlineService.findById(id), HttpStatus.OK);
    }

    @RolesAllowed({Roles.Staff, Roles.Admin})
    @PutMapping("/{id}")
    public ResponseEntity<Airline> updateAirline(@PathVariable Long id, @RequestBody Airline airline) {
        airline.setId(id);
        try {
        return new ResponseEntity<>(airlineService.update(airline), HttpStatus.OK);
        } catch (
            IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RolesAllowed(Roles.Admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAirline(@PathVariable Long id) {
        try {
            airlineService.deleteById(id);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
