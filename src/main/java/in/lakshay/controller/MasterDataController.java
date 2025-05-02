package in.lakshay.controller;

import in.lakshay.dto.ApiResponse;
import in.lakshay.dto.ComponentTypeDTO;
import in.lakshay.dto.MasterDataDTO;
import in.lakshay.service.MasterDataService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/master-data") // base path for master data endpoints
@Slf4j // logging
@Tag(name = "Master Data", description = "Master data management APIs") // swagger docs
public class MasterDataController {

    @Autowired // TODO: switch to constructor injection
    private MasterDataService masterDataService; // handles master data business logic

    @Autowired
    private MessageSource messageSource; // i18n

    @GetMapping("/component-types") // get all component types
    @RateLimiter(name = "basic") // prevent abuse
    @Operation(summary = "Get all component types", description = "Returns all component types")
    public ResponseEntity<ApiResponse<List<ComponentTypeDTO>>> getAllComponentTypes() {
        log.info("Fetching all component types");
        // get all component types from db
        List<ComponentTypeDTO> componentTypes = masterDataService.getAllComponentTypes();

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("component.types.retrieved.success", null, LocaleContextHolder.getLocale()),
                componentTypes
        ));
    }

    @GetMapping("/component-types/{id}") // get component type by id
    @RateLimiter(name = "basic")
    @Operation(summary = "Get component type by ID", description = "Returns a component type by ID")
    public ResponseEntity<ApiResponse<ComponentTypeDTO>> getComponentTypeById(@PathVariable Integer id) {
        log.info("Fetching component type with ID: {}", id);
        // will throw 404 if not found
        ComponentTypeDTO componentType = masterDataService.getComponentTypeById(id);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("component.type.retrieved.success", null, LocaleContextHolder.getLocale()),
                componentType
        ));
    }

    @GetMapping("/component-types/name/{name}") // get component type by name
    @RateLimiter(name = "basic")
    @Operation(summary = "Get component type by name", description = "Returns a component type by name")
    public ResponseEntity<ApiResponse<ComponentTypeDTO>> getComponentTypeByName(@PathVariable String name) {
        log.info("Fetching component type with name: {}", name);
        // easier to use than ID for common lookups
        ComponentTypeDTO componentType = masterDataService.getComponentTypeByName(name);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("component.type.retrieved.success", null, LocaleContextHolder.getLocale()),
                componentType
        ));
    }

    @GetMapping("/component-types/{componentTypeId}/master-data") // get all master data for a component type
    @RateLimiter(name = "basic")
    @Operation(summary = "Get master data by component type", description = "Returns all master data for a component type")
    public ResponseEntity<ApiResponse<List<MasterDataDTO>>> getMasterDataByComponentType(@PathVariable Integer componentTypeId) {
        log.info("Fetching master data for component type ID: {}", componentTypeId);
        // get all master data items for this component type
        List<MasterDataDTO> masterData = masterDataService.getMasterDataByComponentType(componentTypeId);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("master.data.retrieved.success", null, LocaleContextHolder.getLocale()),
                masterData
        ));
    }

    @GetMapping("/component-types/name/{componentTypeName}/master-data") // by name instead of id
    @RateLimiter(name = "basic")
    @Operation(summary = "Get master data by component type name", description = "Returns all master data for a component type by name")
    public ResponseEntity<ApiResponse<List<MasterDataDTO>>> getMasterDataByComponentTypeName(@PathVariable String componentTypeName) {
        log.info("Fetching master data for component type name: {}", componentTypeName);
        // more convenient than using ID for common lookups
        List<MasterDataDTO> masterData = masterDataService.getMasterDataByComponentTypeName(componentTypeName);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("master.data.retrieved.success", null, LocaleContextHolder.getLocale()),
                masterData
        ));
    }

    @GetMapping("/component-types/{componentTypeId}/master-data/{masterDataId}") // get specific master data
    @RateLimiter(name = "basic")
    @Operation(summary = "Get master data by component type and master data ID", description = "Returns a specific master data item")
    public ResponseEntity<ApiResponse<MasterDataDTO>> getMasterDataByComponentTypeAndMasterDataId(
            @PathVariable Integer componentTypeId, @PathVariable Integer masterDataId) {
        log.info("Fetching master data for component type ID: {} and master data ID: {}", componentTypeId, masterDataId);
        // get specific master data item by both IDs
        MasterDataDTO masterData = masterDataService.getMasterDataByComponentTypeAndMasterDataId(componentTypeId, masterDataId);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("master.data.retrieved.success", null, LocaleContextHolder.getLocale()),
                masterData
        ));
    }

    @GetMapping("/component-types/name/{componentTypeName}/master-data/{masterDataId}") // by name instead of id
    @RateLimiter(name = "basic")
    @Operation(summary = "Get master data by component type name and master data ID", description = "Returns a specific master data item")
    public ResponseEntity<ApiResponse<MasterDataDTO>> getMasterDataByComponentTypeNameAndMasterDataId(
            @PathVariable String componentTypeName, @PathVariable Integer masterDataId) {
        log.info("Fetching master data for component type name: {} and master data ID: {}", componentTypeName, masterDataId);
        // most commonly used endpoint for master data lookups
        // e.g. RESERVATION_STATUS with ID 1 = PENDING
        MasterDataDTO masterData = masterDataService.getMasterDataByComponentTypeNameAndMasterDataId(componentTypeName, masterDataId);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("master.data.retrieved.success", null, LocaleContextHolder.getLocale()),
                masterData
        ));
    }
} // end of MasterDataController
