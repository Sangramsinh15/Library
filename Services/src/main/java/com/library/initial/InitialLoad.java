package com.library.initial;

import com.library.entity.Author;
import com.library.entity.Book;
import com.library.entity.Configuration;
import com.library.modal.Status;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import com.library.repository.ConfigurationRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class InitialLoad {


    private static final Logger LOGGER = LogManager.getLogger(InitialLoad.class);

    @Autowired
    BookRepository bookRepository;

    @Autowired
    ConfigurationRepository configurationRepository;

    @Autowired
    AuthorRepository authorRepository;

    public void load() {

        if(configurationRepository.findAll().size() > 0 && configurationRepository.findAll().get(0).getStatus().equals(Status.COMPLETED)){
            LOGGER.info("Data has been loaded");
            return;
        }

        LOGGER.info("Initial Load started");

        BufferedReader bufferedReader;
        String line;
        try {
            ClassLoader classLoader = InitialLoad.class.getClassLoader();
            bufferedReader = new BufferedReader(new FileReader(classLoader.getResource("books.csv").getFile()));
            Configuration configuration = new Configuration();
            configuration.setStatus(Status.COMPLETED);
            configurationRepository.save(configuration);
            while ((line = bufferedReader.readLine()) != null) {
                String[] s = line.split("\\t");
                if (s[0].equalsIgnoreCase("isbn10")) continue;
                new LoadBook(s);
            }
            LOGGER.info("Initial Load finished");
        } catch (FileNotFoundException e) {
            LOGGER.error("Error in input file", e);
        } catch (IOException e) {
            LOGGER.error("Exception occurred", e);
        }
    }

   

}
