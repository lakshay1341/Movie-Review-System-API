package in.lakshay.service;

import in.lakshay.dto.ComponentTypeDTO;
import in.lakshay.dto.MasterDataDTO;
import in.lakshay.entity.ComponentType;
import in.lakshay.entity.MasterData;
import in.lakshay.exception.ResourceNotFoundException;
import in.lakshay.repo.ComponentTypeRepository;
import in.lakshay.repo.MasterDataRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// handles lookup data/reference data for the system
// things like status codes, reservation types, etc.

@Service
public class MasterDataService {

    private final MasterDataRepository masterDataRepository; // for master data
    private final ComponentTypeRepository componentTypeRepository; // for component types
    private final ModelMapper modelMapper; // for dto conversion

    @Autowired // constructor injection
    public MasterDataService(MasterDataRepository masterDataRepository,
                            ComponentTypeRepository componentTypeRepository,
                            ModelMapper modelMapper) {
        this.masterDataRepository = masterDataRepository;
        this.componentTypeRepository = componentTypeRepository;
        this.modelMapper = modelMapper;
    }

    // get all component types (categories of lookup data)
    public List<ComponentTypeDTO> getAllComponentTypes() {
        return componentTypeRepository.findAll().stream()
                .map(this::mapToComponentTypeDTO) // convert to dtos
                .collect(Collectors.toList());
    }

    // get a single component type by id
    public ComponentTypeDTO getComponentTypeById(Integer id) {
        ComponentType componentType = componentTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Component type not found with id: " + id));
        return mapToComponentTypeDTO(componentType); // convert to dto
    }

    // lookup component type by name (easier than using id)
    public ComponentTypeDTO getComponentTypeByName(String name) {
        ComponentType componentType = componentTypeRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Component type not found with name: " + name));
        return mapToComponentTypeDTO(componentType); // to dto
    }

    // get all lookup values for a specific category
    public List<MasterDataDTO> getMasterDataByComponentType(Integer componentTypeId) {
        ComponentType componentType = componentTypeRepository.findById(componentTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Component type not found with id: " + componentTypeId));

        return masterDataRepository.findByComponentType(componentType).stream()
                .map(this::mapToMasterDataDTO) // convert to dtos
                .collect(Collectors.toList());
    }

    // get all lookup values by category name (more user-friendly)
    // e.g., "RESERVATION_STATUS" instead of component type ID
    public List<MasterDataDTO> getMasterDataByComponentTypeName(String componentTypeName) {
        ComponentType componentType = componentTypeRepository.findByName(componentTypeName)
                .orElseThrow(() -> new ResourceNotFoundException("Component type not found with name: " + componentTypeName));

        return masterDataRepository.findByComponentType(componentType).stream()
                .map(this::mapToMasterDataDTO) // entity -> dto
                .collect(Collectors.toList());
    }

    // get specific lookup value by category id and value id
    // e.g., reservation status with id=2 (PAID)
    public MasterDataDTO getMasterDataByComponentTypeAndMasterDataId(Integer componentTypeId, Integer masterDataId) {
        ComponentType componentType = componentTypeRepository.findById(componentTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Component type not found with id: " + componentTypeId));

        MasterData masterData = masterDataRepository.findByComponentTypeAndMasterDataId(componentType, masterDataId)
                .orElseThrow(() -> new ResourceNotFoundException("Master data not found with id: " + masterDataId));

        return mapToMasterDataDTO(masterData); // convert to dto
    }

    // get specific lookup value by category name and value id
    // e.g., "RESERVATION_STATUS", 2 (PAID) - most user-friendly method
    public MasterDataDTO getMasterDataByComponentTypeNameAndMasterDataId(String componentTypeName, Integer masterDataId) {
        MasterData masterData = masterDataRepository.findByComponentTypeNameAndMasterDataId(componentTypeName, masterDataId)
                .orElseThrow(() -> new ResourceNotFoundException("Master data not found for component type: " + componentTypeName + " and id: " + masterDataId));

        return mapToMasterDataDTO(masterData); // to dto
    }

    // helper to convert ComponentType -> DTO
    private ComponentTypeDTO mapToComponentTypeDTO(ComponentType componentType) {
        return modelMapper.map(componentType, ComponentTypeDTO.class); // simple mapping
    }

    // helper to convert MasterData -> DTO
    // need to manually set the component type id
    private MasterDataDTO mapToMasterDataDTO(MasterData masterData) {
        MasterDataDTO dto = modelMapper.map(masterData, MasterDataDTO.class);
        dto.setComponentTypeId(masterData.getComponentType().getId()); // set parent id
        return dto;
    }
}
