package com.mkyong;

import com.mkyong.error.BookNotFoundException;
import com.mkyong.error.BookUnSupportedFieldPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@Validated
public class BookController {

    @Autowired
    private BookRepository repository;

    // Find
    @GetMapping("/books")
    public ResponseEntity<?> getBooks() {
        List<Book> books = repository.findAll();
        
        if(books.isEmpty()) {
        	return ResponseEntity.notFound().build();
        } else {
        	return ResponseEntity.ok(books);
        }
    }

    // Save
    @PostMapping("/books")
    	public ResponseEntity<Book> newBook(@Valid @RequestBody Book newbook) {
    		Book saved = repository.save(newbook);
    		return ResponseEntity.status(HttpStatus.CREATED).body(saved); // 201
    	}
    
    /*
    //return 201 instead of 200
    @ResponseStatus(HttpStatus.CREATED)
    Book newBook(@RequestBody Book newBook) {
        return repository.save(newBook);
    }
    */

    // Find
    @GetMapping("/books/{id}")
    public ResponseEntity<Book> findOne(@PathVariable @Min(1) Long id) {
    	Book book = repository.findById(id).orElse(null);
    	if(book == null) {
    		return ResponseEntity.noContent().build();
    	} else {
    		return ResponseEntity.ok(book);
    	}
    }
    
    /*
     * Book findOne(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }
    */

    // Save or update
    @PutMapping("/books/{id}")
    public ResponseEntity<Book> saveOrUpdate(@RequestBody Book newBook, @PathVariable Long id) {
    	return repository.findById(id)
    			.map(p->{
    		p.setName(newBook.getName());
    		p.setPrice(newBook.getPrice());
    		p.setAuthor(newBook.getAuthor());
    		return ResponseEntity.ok(repository.save(p));
    			})
    			.orElseGet(()->{
    				return ResponseEntity.notFound().build();
    			});
    }
    
    /*
     * Book saveOrUpdate(@RequestBody Book newBook, @PathVariable Long id) {

        return repository.findById(id)
                .map(x -> {
                    x.setName(newBook.getName());
                    x.setAuthor(newBook.getAuthor());
                    x.setPrice(newBook.getPrice());
                    return repository.save(x);
                })
                .orElseGet(() -> {
                    newBook.setId(id);
                    return repository.save(newBook);
                });
    }
    */

    // update author only
    @PatchMapping("/books/{id}")
    public ResponseEntity<?> updateVariable(@RequestBody Book updateBook, @PathVariable Long id) {
    	Book book = repository.findById(id).orElse(null);
    	
    	if (book == null) {
    		return ResponseEntity.notFound().build();
    	} else {
    		if (updateBook.getName() != null) {
    			book.setName(updateBook.getName());
    		}
    		if (updateBook.getPrice() != null) {
    			book.setPrice(updateBook.getPrice());
    		}
    		if (updateBook.getAuthor() != null) {
    			book.setAuthor(updateBook.getAuthor());
    		}
    		return ResponseEntity.ok(repository.save(book));
    	}
    }
    
    /*
     * Book patch(@RequestBody Map<String, String> update, @PathVariable Long id) {
     

        return repository.findById(id)
                .map(x -> {

                    String author = update.get("author");
                    if (!StringUtils.isEmpty(author)) {
                        x.setAuthor(author);

                        // better create a custom method to update a value = :newValue where id = :id
                        return repository.save(x);
                    } else {
                        throw new BookUnSupportedFieldPatchException(update.keySet());
                    }

                })
                .orElseGet(() -> {
                    throw new BookNotFoundException(id);
                });

    }
    */

    @DeleteMapping("/books/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
    	repository.deleteById(id);
    	return ResponseEntity.noContent().build();
    }
    
    /*
     * void deleteBook(@PathVariable Long id) {
        repository.deleteById(id);
    }
    */

}
