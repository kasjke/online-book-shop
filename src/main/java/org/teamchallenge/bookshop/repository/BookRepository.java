package org.teamchallenge.bookshop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.teamchallenge.bookshop.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findFirstByTitleContainingIgnoreCase(String title);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.images WHERE b.isThisSlider = true")
    List<Book> findSliderBooks();

    @Query("SELECT b FROM Book b ORDER BY RANDOM() LIMIT :count")
    List<Book> getRandom(@Param("count") Integer count);
    @Query("SELECT b FROM Book b " +
           "WHERE (:publisher IS NULL OR b.characteristic.publisher = :publisher) " +
           "AND (:language IS NULL OR b.characteristic.language = :language) " +
           "AND (:bookType IS NULL OR b.characteristic.bookType = :bookType) " +
           "AND (:coverType IS NULL OR b.characteristic.coverType = :coverType)")
    Page<Book> findBooksByCharacteristics(@Param("publisher") String publisher,
                                          @Param("language") String language,
                                          @Param("bookType") String bookType,
                                          @Param("coverType") String coverType,
                                          Pageable pageable);


}