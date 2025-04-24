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

@Service
public class MasterDataService {

    private final MasterDataRepository masterDataRepository;
    private final ComponentTypeRepository componentTypeRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public MasterDataService(MasterDataRepository masterDataRepository,
                            ComponentTypeRepository componentTypeRepository,
                            ModelMapper modelMapper) {
        this.masterDataRepository = masterDataRepository;
        this.componentTypeRepository = componentTypeRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Get all component types
     */
    public List<ComponentTypeDTO> getAllComponentTypes() {
        return componentTypeRepository.findAll().stream()
                .map(this::mapToComponentTypeDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get component type by ID
     */
    public ComponentTypeDTO getComponentTypeById(Integer id) {
        ComponentType componentType = componentTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Component type not found with id: " + id));
        return mapToComponentTypeDTO(componentType);
    }

    /**
     * Get component type by name
     */
    public ComponentTypeDTO getComponentTypeByName(String name) {
        ComponentType componentType = componentTypeRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Component type not found with name: " + name));
        return mapToComponentTypeDTO(componentType);
    }

    /**
     * Get all master data for a component type
     */
    public List<MasterDataDTO> getMasterDataByComponentType(Integer componentTypeId) {
        ComponentType componentType = componentTypeRepository.findById(componentTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Component type not found with id: " + componentTypeId));
        
        return masterDataRepository.findByComponentType(componentType).stream()
                .map(this::mapToMasterDataDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all master data for a component type by name
     */
    public List<MasterDataDTO> getMasterDataByComponentTypeName(String componentTypeName) {
        ComponentType componentType = componentTypeRepository.findByName(componentTypeName)
                .orElseThrow(() -> new ResourceNotFoundException("Component type not found with name: " + componentTypeName));
        
        return masterDataRepository.findByComponentType(componentType).stream()
                .map(this::mapToMasterDataDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get master data by component type and master data ID
     */
    public MasterDataDTO getMasterDataByComponentTypeAndMasterDataId(Integer componentTypeId, Integer masterDataId) {
        ComponentType componentType = componentTypeRepository.findById(componentTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Component type not found with id: " + componentTypeId));
        
        MasterData masterData = masterDataRepository.findByComponentTypeAndMasterDataId(componentType, masterDataId)
                .orElseThrow(() -> new ResourceNotFoundException("Master data not found with id: " + masterDataId));
        
        return mapToMasterDataDTO(masterData);
    }

    /**
     * Get master data by component type name and master data ID
     */
    public MasterDataDTO getMasterDataByComponentTypeNameAndMasterDataId(String componentTypeName, Integer masterDataId) {
        MasterData masterData = masterDataRepository.findByComponentTypeNameAndMasterDataId(componentTypeName, masterDataId)
                .orElseThrow(() -> new ResourceNotFoundException("Master data not found for component type: " + componentTypeName + " and id: " + masterDataId));
        
        return mapToMasterDataDTO(masterData);
    }

    /**
     * Map ComponentType to ComponentTypeDTO
     */
    private ComponentTypeDTO mapToComponentTypeDTO(ComponentType componentType) {
        return modelMapper.map(componentType, ComponentTypeDTO.class);
    }

    /**
     * Map MasterData to MasterDataDTO
     */
    private MasterDataDTO mapToMasterDataDTO(MasterData masterData) {
        MasterDataDTO dto = modelMapper.map(masterData, MasterDataDTO.class);
        dto.setComponentTypeId(masterData.getComponentType().getId());
        return dto;
    }
}
