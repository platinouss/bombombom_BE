package com.bombombom.devs.algo.service;

import com.bombombom.devs.algo.config.ProbabilityConfig;
import com.bombombom.devs.algo.models.AlgoTag;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AlgoProblemService {
    /*
    A, B, C, ... 등의 태그가 각각 a, b, c, ... 개수의 문제를 가진다.
    이중에 총 N개의 문제를 랜덤으로 선택해야 한다.
    추가 조건으로 각 태그의 문제 개수에 비례해서 해당 태그가 뽑힐 확률이 높아야한다.

    0. 각 태그의 문제 개수에 비례해서 해당 태그가 뽑힐 확률을 계산하여 환경변수로 설정한다.
    1.. 각 태그의 확률을 누적해서 더한 배열을 만든다.
    2. 0부터 1사이의 난수를 생성한다.
    3. 난수가 누적 확률 배열에서 몇 번째에 위치하는지 찾는다.
    4. 해당 위치의 태그를 선택한다.
     */

    /*
    함수 인자로 선택할 Tag, 혹은 제외할 Tag를 받는 것도 고려해봤지만
    오버엔지니어링이 될 수 있으므로 최소 스펙으로 구현하였음
     */

    public Map<String, Integer> getProblemCountForEachTag(Integer totalProblemCount) {
        Map<String, Integer> problemCountByTag = new HashMap<>();
        Random random = new Random();
        IntStream.range(0, totalProblemCount).forEach(i -> {
            double rand = random.nextDouble(ProbabilityConfig.totalProbability);
            Arrays.stream(AlgoTag.values()).forEach(tag -> {
                if (tag.isInRange(rand)) {
                    problemCountByTag.merge(tag.name(), 1, Integer::sum);
                }
            });
        });
        return problemCountByTag;
    }

}
