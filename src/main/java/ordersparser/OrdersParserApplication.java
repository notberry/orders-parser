package ordersparser;

import ordersparser.domain.Order;
import ordersparser.exception.IllegalFileFormatException;
import ordersparser.service.parser.Parser;
import ordersparser.service.parser.Parsers;
import ordersparser.service.Converter;
import ordersparser.service.Reader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@SpringBootApplication
public class OrdersParserApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(OrdersParserApplication.class, args);
    }

    @Autowired
    private Converter converter;

    @Bean
    @Scope(value = "prototype")
    Reader getReader() {
        return new Reader(getQueue());
    }

    @Bean
    BlockingQueue<Order> getQueue() {
        return new LinkedBlockingQueue<>();
    }

    @Override
    public void run(String... args) throws Exception {
        String fileCsv = "test.csv";
        String fileJson = "test.json";
        String badtest = "badtest.json";
        String[] args1 = {fileCsv, fileJson, badtest};
        List<Object[]> parsers = Parsers.getParser(args1);
        for (Object[] parser : parsers) {
            Reader reader = getReader();
            reader.setFileName((String) parser[0]);
            reader.setParser((Parser) parser[1]);
            long numberLines = reader.numberLines();
            new Thread(reader).start();
            converter.convert(numberLines);
        }
    }
}