package in.lakshay.controller;

import in.lakshay.dto.ApiResponse;
import in.lakshay.dto.TheaterDTO;
import in.lakshay.dto.TheaterRequest;
import in.lakshay.entity.Theater;
import in.lakshay.service.TheaterService;
import in.lakshay.util.Constants;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constants.THEATERS_PATH)
@Slf4j
@Tag(name = "Theaters", description = "Theater management APIs")
public class TheaterController {
    @Autowired
    private TheaterService theaterService;

    @Autowired
    private MessageSource messageSource;

    @RateLimiter(name = "basic")
    @GetMapping
    @Operation(summary = "Get all theaters", description = "Returns a list of all theaters")
    public ResponseEntity<ApiResponse<List<TheaterDTO>>> getAllTheaters() {
        log.info("Fetching all theaters");
        List<TheaterDTO> theaters = theaterService.getAllTheaters();
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("theaters.retrieved.success", null, LocaleContextHolder.getLocale()),
                theaters
        ));
    }

    @RateLimiter(name = "basic")
    @GetMapping("/{id}")
    @Operation(summary = "Get theater by ID", description = "Returns a theater by its ID")
    public ResponseEntity<ApiResponse<TheaterDTO>> getTheaterById(@PathVariable Long id) {
        log.info("Fetching theater with id: {}", id);
        TheaterDTO theater = theaterService.getTheaterById(id);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("theater.retrieved.success", null, LocaleContextHolder.getLocale()),
                theater
        ));
    }

    @RateLimiter(name = "basic")
    @GetMapping("/search")
    @Operation(summary = "Search theaters by location", description = "Returns theaters matching the location search term")
    public ResponseEntity<ApiResponse<List<TheaterDTO>>> searchTheatersByLocation(@RequestParam String location) {
        log.info("Searching theaters by location: {}", location);
        List<TheaterDTO> theaters = theaterService.getTheatersByLocation(location);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("theaters.retrieved.success", null, LocaleContextHolder.getLocale()),
                theaters
        ));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Add a new theater", description = "Creates a new theater (Admin only)")
    public ResponseEntity<ApiResponse<TheaterDTO>> addTheater(@Valid @RequestBody TheaterRequest theaterRequest) {
        log.info("Adding new theater: {}", theaterRequest.getName());

        Theater theater = new Theater();
        theater.setName(theaterRequest.getName());
        theater.setLocation(theaterRequest.getLocation());
        theater.setCapacity(theaterRequest.getCapacity());

        TheaterDTO savedTheater = theaterService.addTheater(theater);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        messageSource.getMessage("theater.created.success", null, LocaleContextHolder.getLocale()),
                        savedTheater
                ));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update a theater", description = "Updates an existing theater (Admin only)")
    public ResponseEntity<ApiResponse<TheaterDTO>> updateTheater(
            @PathVariable Long id,
            @Valid @RequestBody TheaterRequest theaterRequest) {
        log.info("Updating theater with id: {}", id);

        Theater theater = new Theater();
        theater.setName(theaterRequest.getName());
        theater.setLocation(theaterRequest.getLocation());
        theater.setCapacity(theaterRequest.getCapacity());

        TheaterDTO updatedTheater = theaterService.updateTheater(id, theater);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("theater.updated.success", null, LocaleContextHolder.getLocale()),
                updatedTheater
        ));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete a theater", description = "Deletes an existing theater (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteTheater(@PathVariable Long id) {
        log.info("Deleting theater with id: {}", id);
        theaterService.deleteTheater(id);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("theater.deleted.success", null, LocaleContextHolder.getLocale()),
                null
        ));
    }
}
