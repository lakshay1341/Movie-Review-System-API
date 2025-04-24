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
@RequestMapping("/api/v1/master-data")
@Slf4j
@Tag(name = "Master Data", description = "Master data management APIs")
public class MasterDataController {

    @Autowired
    private MasterDataService masterDataService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping("/component-types")
    @RateLimiter(name = "basic")
    @Operation(summary = "Get all component types", description = "Returns all component types")
    public ResponseEntity<ApiResponse<List<ComponentTypeDTO>>> getAllComponentTypes() {
        log.info("Fetching all component types");
        List<ComponentTypeDTO> componentTypes = masterDataService.getAllComponentTypes();
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("component.types.retrieved.success", null, LocaleContextHolder.getLocale()),
                componentTypes
        ));
    }

    @GetMapping("/component-types/{id}")
    @RateLimiter(name = "basic")
    @Operation(summary = "Get component type by ID", description = "Returns a component type by ID")
    public ResponseEntity<ApiResponse<ComponentTypeDTO>> getComponentTypeById(@PathVariable Integer id) {
        log.info("Fetching component type with ID: {}", id);
        ComponentTypeDTO componentType = masterDataService.getComponentTypeById(id);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("component.type.retrieved.success", null, LocaleContextHolder.getLocale()),
                componentType
        ));
    }

    @GetMapping("/component-types/name/{name}")
    @RateLimiter(name = "basic")
    @Operation(summary = "Get component type by name", description = "Returns a component type by name")
    public ResponseEntity<ApiResponse<ComponentTypeDTO>> getComponentTypeByName(@PathVariable String name) {
        log.info("Fetching component type with name: {}", name);
        ComponentTypeDTO componentType = masterDataService.getComponentTypeByName(name);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("component.type.retrieved.success", null, LocaleContextHolder.getLocale()),
                componentType
        ));
    }

    @GetMapping("/component-types/{componentTypeId}/master-data")
    @RateLimiter(name = "basic")
    @Operation(summary = "Get master data by component type", description = "Returns all master data for a component type")
    public ResponseEntity<ApiResponse<List<MasterDataDTO>>> getMasterDataByComponentType(@PathVariable Integer componentTypeId) {
        log.info("Fetching master data for component type ID: {}", componentTypeId);
        List<MasterDataDTO> masterData = masterDataService.getMasterDataByComponentType(componentTypeId);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("master.data.retrieved.success", null, LocaleContextHolder.getLocale()),
                masterData
        ));
    }

    @GetMapping("/component-types/name/{componentTypeName}/master-data")
    @RateLimiter(name = "basic")
    @Operation(summary = "Get master data by component type name", description = "Returns all master data for a component type by name")
    public ResponseEntity<ApiResponse<List<MasterDataDTO>>> getMasterDataByComponentTypeName(@PathVariable String componentTypeName) {
        log.info("Fetching master data for component type name: {}", componentTypeName);
        List<MasterDataDTO> masterData = masterDataService.getMasterDataByComponentTypeName(componentTypeName);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("master.data.retrieved.success", null, LocaleContextHolder.getLocale()),
                masterData
        ));
    }

    @GetMapping("/component-types/{componentTypeId}/master-data/{masterDataId}")
    @RateLimiter(name = "basic")
    @Operation(summary = "Get master data by component type and master data ID", description = "Returns a specific master data item")
    public ResponseEntity<ApiResponse<MasterDataDTO>> getMasterDataByComponentTypeAndMasterDataId(
            @PathVariable Integer componentTypeId, @PathVariable Integer masterDataId) {
        log.info("Fetching master data for component type ID: {} and master data ID: {}", componentTypeId, masterDataId);
        MasterDataDTO masterData = masterDataService.getMasterDataByComponentTypeAndMasterDataId(componentTypeId, masterDataId);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("master.data.retrieved.success", null, LocaleContextHolder.getLocale()),
                masterData
        ));
    }

    @GetMapping("/component-types/name/{componentTypeName}/master-data/{masterDataId}")
    @RateLimiter(name = "basic")
    @Operation(summary = "Get master data by component type name and master data ID", description = "Returns a specific master data item")
    public ResponseEntity<ApiResponse<MasterDataDTO>> getMasterDataByComponentTypeNameAndMasterDataId(
            @PathVariable String componentTypeName, @PathVariable Integer masterDataId) {
        log.info("Fetching master data for component type name: {} and master data ID: {}", componentTypeName, masterDataId);
        MasterDataDTO masterData = masterDataService.getMasterDataByComponentTypeNameAndMasterDataId(componentTypeName, masterDataId);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("master.data.retrieved.success", null, LocaleContextHolder.getLocale()),
                masterData
        ));
    }
}
