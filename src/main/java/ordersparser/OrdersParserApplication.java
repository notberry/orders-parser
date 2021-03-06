package ordersparser;

import ordersparser.domain.Order;
import ordersparser.service.Converter;
import ordersparser.service.Reader;
import ordersparser.service.parser.Parser;
import ordersparser.service.parser.Parsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@SpringBootApplication
public class OrdersParserApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(OrdersParserApplication.class, args);
    }

    @Autowired
    private Converter converter;

    @Autowired
    private Reader reader;

    @Bean
    BlockingQueue<Order> getQueue() {
        return new LinkedBlockingQueue<>();
    }

    @Override
    public void run(String... args) throws Exception {
        String filename;
        List<String> result;
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        List<Object[]> parsers = Parsers.getParser(args);
        for (Object[] parser : parsers) {
            filename = (String) parser[0];
            System.out.println("\nFile " + filename + ": ");

            reader.setFileName(filename);
            reader.setParser((Parser) parser[1]);
            long numberLines = reader.numberLines();
            executorService.execute(reader);

            result = converter.convert(numberLines, executorService);
            result.forEach(System.out::println);
        }
        executorService.shutdown();
    }
}