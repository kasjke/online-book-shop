package org.teamchallenge.bookshop.service.Impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.teamchallenge.bookshop.dto.BookCharacteristicDto;
import org.teamchallenge.bookshop.dto.BookDto;
import org.teamchallenge.bookshop.dto.BookInCatalogDto;
import org.teamchallenge.bookshop.dto.CategoryDto;
import org.teamchallenge.bookshop.enums.Category;
import org.teamchallenge.bookshop.exception.BookNotFoundException;
import org.teamchallenge.bookshop.exception.DropboxFolderCreationException;
import org.teamchallenge.bookshop.exception.ImageUploadException;
import org.teamchallenge.bookshop.mapper.BookMapper;
import org.teamchallenge.bookshop.model.Book;
import org.teamchallenge.bookshop.repository.BookRepository;
import org.teamchallenge.bookshop.service.BookService;
import org.teamchallenge.bookshop.service.DropboxService;
import org.teamchallenge.bookshop.util.ImageUtil;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;


@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final DropboxService dropboxService;
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Page<BookCharacteristicDto> getBooksByCharacteristics(Pageable pageable,
                                                                 String publisher,
                                                                 String language,
                                                                 String bookType,
                                                                 String coverType) {
        Page<Book> books = bookRepository.findBooksByCharacteristics(
                publisher, language, bookType, coverType, pageable);

        return books.map(bookMapper::entityToBookCharacteristicDto);
    }

    @Override
    @Transactional
    public BookCharacteristicDto createBookWithCharacteristics(BookCharacteristicDto bookCharacteristicDto) {
        Book book = bookMapper.bookCharacteristicDtoToEntity(bookCharacteristicDto);
//        processBookImages(book, bookCharacteristicDto.getTitleImage(), bookCharacteristicDto.getImages());
        Book savedBook = bookRepository.save(book);

        return bookMapper.entityToBookCharacteristicDto(savedBook);
    }

    @Override
    public BookCharacteristicDto update(Long id, BookCharacteristicDto bookCharacteristicDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(BookNotFoundException::new);
        bookMapper.updateUserFromDto(bookCharacteristicDto, book);
        Book updatedUserEntity = bookRepository.save(book);

        return bookMapper.entityToBookCharacteristicDto(updatedUserEntity);
    }

    @Override
    public String addBook(BookDto bookDto, MultipartFile titleImageFile) {
        if (titleImageFile == null || titleImageFile.isEmpty()) {
            throw new IllegalArgumentException("Title image file is null or empty");
        }

        Book book = bookMapper.dtoToEntity(bookDto);

        String folderName = "/" + UUID.randomUUID();
        dropboxService.createFolder(folderName);

        String titleImageLink = dropboxService.uploadImage(
                folderName + "/title.png",
                titleImageFile
        );
        book.setTitleImage(titleImageLink);

        if (bookDto.getImages() != null && !bookDto.getImages().isEmpty()) {
            AtomicInteger counter = new AtomicInteger(1);
            List<String> imageLinks = bookDto.getImages().stream()
                    .map(imageBase64 -> {
                        try {
                            return dropboxService.uploadImage(
                                    folderName + "/" + counter.getAndIncrement() + ".png",
                                    ImageUtil.base64ToMultipartFile(imageBase64)
                            );
                        } catch (Exception e) {
                            throw new RuntimeException("Error uploading additional image", e);
                        }
                    })
                    .toList();
            book.setImages(imageLinks);
        }

        bookRepository.save(book);
        return titleImageLink;
    }
//
//    private String processBookImages(Book book, String titleImageBase64, List<String> imagesBase64) {
//        String folderName = "/" + UUID.randomUUID();
//
//        try {
//            dropboxService.createFolder(folderName);
//        } catch (DropboxFolderCreationException e) {
//            throw new RuntimeException("Error creating Dropbox folder", e);
//        }
//
//        try {
//            book.setTitleImage(dropboxService.uploadImage(
//                    folderName + "/title.png",
//                    ImageUtil.base64ToBufferedImage(titleImageBase64))
//            );
//        } catch (ImageUploadException e) {
//            throw new RuntimeException("Error uploading title image", e);
//        }
//
//        if (imagesBase64 != null && !imagesBase64.isEmpty()) {
//            AtomicInteger counter = new AtomicInteger(1);
//            List<String> links = imagesBase64.stream()
//                    .map(image -> {
//                        try {
//                            return dropboxService.uploadImage(
//                                    folderName + "/" + counter.getAndIncrement() + ".png",
//                                    ImageUtil.base64ToBufferedImage(image)
//                            );
//                        } catch (ImageUploadException e) {
//                            throw new RuntimeException("Error uploading additional image", e);
//                        }
//                    })
//                    .toList();
//
//            book.setImages(links);
//        }
//        return book.getTitleImage();
//    }


    private String encodeToBase64(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Provided file is null or empty");
        }
        return Base64.getEncoder().encodeToString(file.getBytes());
    }

    @Override
    public BookCharacteristicDto getBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(BookNotFoundException::new);
        List<String> images = book.getImages();
        images.add(0, book.getTitleImage());
        book.setQuantity(1);
        book.setImages(images);
        return bookMapper.entityToBookCharacteristicDto(book);
    }

    @Override
    public List<BookInCatalogDto> getBooksForSlider() {
            return bookRepository.findSliderBooks()
                    .stream()
                    .map(bookMapper::entityToBookCatalogDTO)
                    .toList();
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        Book book = bookRepository.findById(bookDto.getId()).orElseThrow(BookNotFoundException::new);
        Book updatedBook = Book.builder()
                .id(bookDto.getId())
                .title(book.getTitle())
                .price(bookDto.getPrice())
                .timeAdded(book.getTimeAdded())
                .titleImage(bookDto.getTitleImage())
                .build();
        bookRepository.save(updatedBook);
        return bookMapper.entityToDTO(updatedBook);
    }

    @Override
    public void deleteBook(Long id) {
        bookRepository.findById(id).orElseThrow(BookNotFoundException::new);
        bookRepository.deleteById(id);
    }

    @Override
    public List<CategoryDto> getAllCategory() {
        return Arrays.stream(Category.values())
                .map(category -> new CategoryDto(category.getId(),category.name(), category.getName()))
                .toList();
    }

    public BookInCatalogDto getFirstBookByTitle(String title) {
        return bookRepository.findFirstByTitleContainingIgnoreCase(title)
                .map(bookMapper::entityToBookCatalogDTO)
                .orElseThrow(BookNotFoundException::new);
    }

    public Page<BookCharacteristicDto> getAllBooks(Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> query = criteriaBuilder.createQuery(Book.class);
        Root<Book> root = query.from(Book.class);
        root.fetch("images", JoinType.LEFT);
        List<Predicate> predicates = getPredicatesForBooks(criteriaBuilder, root);
        query.where(predicates.toArray(new Predicate[0]));
        TypedQuery<Book> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        List<Book> bookList = typedQuery.getResultList();
        List<BookCharacteristicDto> bookDtoList = bookList.stream()
                .map(bookMapper::entityToBookCharacteristicDto)
                .toList();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Book> countRoot = countQuery.from(Book.class);
        List<Predicate> newPredicates = getPredicatesForBooks(criteriaBuilder, countRoot);
        countQuery.select(criteriaBuilder.count(countRoot)).where(newPredicates.toArray(new Predicate[0]));
        long totalCount = entityManager.createQuery(countQuery).getSingleResult();
        return new PageImpl<>(bookDtoList, pageable, totalCount);
    }

    @Override
    public Page<BookCharacteristicDto> getSorted(Pageable pageable,
                                   Integer categoryId,
                                   String timeAdded,
                                   String price,
                                   String author,
                                   Float priceMin,
                                   Float priceMax) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> query = criteriaBuilder.createQuery(Book.class);
        Root<Book> root = query.from(Book.class);
        root.fetch("images", JoinType.LEFT);
        List<Predicate> predicates = getPredicatesForFilter(criteriaBuilder, categoryId, author, priceMin, priceMax, root);
        query.where(predicates.toArray(new Predicate[0]));

        List<Order> orders = new ArrayList<>();
        if (timeAdded != null) {
            if (timeAdded.equalsIgnoreCase("asc")) {
                orders.add(criteriaBuilder.asc(root.get("timeAdded")));
            } else {
                orders.add(criteriaBuilder.desc(root.get("timeAdded")));
            }
        }
        if (price != null) {
            if (price.equalsIgnoreCase("asc")) {
                orders.add(criteriaBuilder.asc(root.get("price")));
            } else {
                orders.add(criteriaBuilder.desc(root.get("price")));
            }
        }
        query.orderBy(orders);

        TypedQuery<Book> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        List<Book> bookList = typedQuery.getResultList();
        List<BookCharacteristicDto> bookDtoList = bookList.stream()
                .map(bookMapper::entityToBookCharacteristicDto)
                .toList();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Book> countRoot = countQuery.from(Book.class);
        List<Predicate> newPredicates = getPredicatesForFilter(criteriaBuilder, categoryId, author, priceMin, priceMax, countRoot);
        countQuery.select(criteriaBuilder.count(countRoot)).where(newPredicates.toArray(new Predicate[0]));
        long totalCount = entityManager.createQuery(countQuery).getSingleResult();
        return new PageImpl<>(bookDtoList, pageable, totalCount);
    }


    private List<Predicate> getPredicatesForBooks(CriteriaBuilder criteriaBuilder, Root<Book> root) {
        return List.of(criteriaBuilder.equal(root.get("isThisSlider"), false));
    }

    private static List<Predicate> getPredicatesForFilter(CriteriaBuilder criteriaBuilder,
                                                          Integer categoryId,
                                                          String author,
                                                          Float priceMin,
                                                          Float priceMax,
                                                          Root<Book> root) {
        List<Predicate> predicates = new ArrayList<>();
        if (categoryId != null) {
            Category categoryEnum = Category.getFromId(categoryId);
            Predicate categoryPredicate = criteriaBuilder.equal(root.get("category"), categoryEnum);
            predicates.add(categoryPredicate);
        }
        if (author != null) {
            Predicate authorPredicate = criteriaBuilder.equal(root.get("authors"), author);
            predicates.add(authorPredicate);
        }
        if (priceMin != null) {
            Predicate priceMinPredicate = criteriaBuilder.ge(root.get("price"), priceMin);
            predicates.add(priceMinPredicate);
        }
        if (priceMax != null) {
            Predicate priceMaxPredicate = criteriaBuilder.le(root.get("price"), priceMax);
            predicates.add(priceMaxPredicate);
        }
        return predicates;
    }
};
