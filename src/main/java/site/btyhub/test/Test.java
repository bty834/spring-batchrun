package site.btyhub.test;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import site.btyhub.batchrun.annotation.BatchRun;

/**
 *
 * @author: baotingyu
 * @date: 2023/7/20
 **/
@Service
public class Test {

    @BatchRun(batchSize = 2)
    public List<String> t(List<String> input){

        List<String> result = new ArrayList<>();

        input.forEach(i->result.add("test:"+i));

        return result;
    }
}
