package site.btyhub;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import site.btyhub.batchrun.annotation.EnableBatchRun;
import site.btyhub.test.Test;


@EnableBatchRun
@SpringBootApplication
public class Example implements ApplicationRunner {
    public static void main( String[] args ) {
        SpringApplication.run(Example.class);
    }

    @Autowired
    Test test;

    @Override
    public void run(ApplicationArguments args) throws Exception {


        List<String> t = test.t(null);
        System.out.println(t);
        System.out.println(1);


    }
}
