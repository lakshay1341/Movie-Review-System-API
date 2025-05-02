package in.lakshay.repo;

import in.lakshay.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

// movie data access - spring data jpa magic
public interface MovieRepository extends JpaRepository<Movie, Long> {
    // search by title or genre - case insensitive
    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.reviews WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(m.genre) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Movie> findByTitleOrGenreContainingIgnoreCase(String search, Pageable pageable);

    // get all movies with their reviews
    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.reviews")
    Page<Movie> findAllWithReviews(Pageable pageable);

    // filter movies by multiple criteria
    // todo: this query could be optimized, it's a bit slow with lots of data
    // might need to add an index on releaseYear column?
    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.reviews WHERE " +
            "(:search IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:genre IS NULL OR LOWER(m.genre) = LOWER(:genre)) AND " + // case insensitive genre match
            "(:year IS NULL OR m.releaseYear = :year)") // exact year match
    Page<Movie> findMoviesWithFilters(String search, String genre, Integer year, Pageable pageable); // main search method used by frontend
}