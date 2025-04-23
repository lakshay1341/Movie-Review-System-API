package in.lakshay.service;

import in.lakshay.dto.TheaterDTO;
import in.lakshay.entity.Theater;
import in.lakshay.exception.ResourceNotFoundException;
import in.lakshay.repo.TheaterRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TheaterService {
    private final TheaterRepository theaterRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public TheaterService(TheaterRepository theaterRepository, ModelMapper modelMapper) {
        this.theaterRepository = theaterRepository;
        this.modelMapper = modelMapper;
    }

    public List<TheaterDTO> getAllTheaters() {
        log.info("Fetching all theaters");
        return theaterRepository.findAll().stream()
                .map(theater -> modelMapper.map(theater, TheaterDTO.class))
                .collect(Collectors.toList());
    }

    public TheaterDTO getTheaterById(Long id) {
        log.info("Fetching theater with id: {}", id);
        Theater theater = theaterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theater not found with id: " + id));
        return modelMapper.map(theater, TheaterDTO.class);
    }

    public List<TheaterDTO> getTheatersByLocation(String location) {
        log.info("Fetching theaters by location: {}", location);
        return theaterRepository.findByLocationContainingIgnoreCase(location).stream()
                .map(theater -> modelMapper.map(theater, TheaterDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public TheaterDTO addTheater(Theater theater) {
        log.info("Adding new theater: {}", theater.getName());
        Theater savedTheater = theaterRepository.save(theater);
        log.info("Theater saved with ID: {}", savedTheater.getId());
        return modelMapper.map(savedTheater, TheaterDTO.class);
    }

    @Transactional
    public TheaterDTO updateTheater(Long id, Theater theaterDetails) {
        log.info("Updating theater with id: {}", id);
        Theater theater = theaterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theater not found with id: " + id));

        theater.setName(theaterDetails.getName());
        theater.setLocation(theaterDetails.getLocation());
        theater.setCapacity(theaterDetails.getCapacity());

        Theater updatedTheater = theaterRepository.save(theater);
        return modelMapper.map(updatedTheater, TheaterDTO.class);
    }

    @Transactional
    public void deleteTheater(Long id) {
        log.info("Deleting theater with id: {}", id);
        Theater theater = theaterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theater not found with id: " + id));

        // Check if theater has any showtimes before deleting
        if (theater.getShowtimes() != null && !theater.getShowtimes().isEmpty()) {
            throw new RuntimeException("Cannot delete theater with existing showtimes");
        }

        theaterRepository.delete(theater);
        log.info("Theater with id: {} deleted successfully", id);
    }
}
