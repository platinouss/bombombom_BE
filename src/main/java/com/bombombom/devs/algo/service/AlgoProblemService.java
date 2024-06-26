package com.bombombom.devs.algo.service;

import com.bombombom.devs.algo.config.ProbabilityConfig;
import com.bombombom.devs.algo.models.AlgoTag;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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

    public Map<AlgoTag, Integer> getProblemCountForEachTag(Integer totalProblemCount) {
        Map<AlgoTag, Integer> problemCountByTag = new HashMap<>();
        Random random = new Random();
        for (int i = 0; i < totalProblemCount; i++) {
            double rand = random.nextDouble(ProbabilityConfig.totalProbability);
            if (AlgoTag.MATH.getChoiceSpreadStart() <= rand && rand < AlgoTag.MATH.getChoiceSpreadEnd()) {
                problemCountByTag.put(AlgoTag.MATH, problemCountByTag.getOrDefault(AlgoTag.MATH, 0) + 1);
            } else if (AlgoTag.DP.getChoiceSpreadStart() <= rand && rand < AlgoTag.DP.getChoiceSpreadEnd()) {
                problemCountByTag.put(AlgoTag.DP, problemCountByTag.getOrDefault(AlgoTag.DP, 0) + 1);
            } else if (AlgoTag.GREEDY.getChoiceSpreadStart() <= rand && rand < AlgoTag.GREEDY.getChoiceSpreadEnd()) {
                problemCountByTag.put(AlgoTag.GREEDY, problemCountByTag.getOrDefault(AlgoTag.GREEDY, 0) + 1);
            } else if (AlgoTag.IMPL.getChoiceSpreadStart() <= rand && rand < AlgoTag.IMPL.getChoiceSpreadEnd()) {
                problemCountByTag.put(AlgoTag.IMPL, problemCountByTag.getOrDefault(AlgoTag.IMPL, 0) + 1);
            } else if (AlgoTag.GRAPH.getChoiceSpreadStart() <= rand && rand < AlgoTag.GRAPH.getChoiceSpreadEnd()) {
                problemCountByTag.put(AlgoTag.GRAPH, problemCountByTag.getOrDefault(AlgoTag.GRAPH, 0) + 1);
            } else if (AlgoTag.GEOMETRY.getChoiceSpreadStart() <= rand && rand < AlgoTag.GEOMETRY.getChoiceSpreadEnd()) {
                problemCountByTag.put(AlgoTag.GEOMETRY, problemCountByTag.getOrDefault(AlgoTag.GEOMETRY, 0) + 1);
            } else if (AlgoTag.DS.getChoiceSpreadStart() <= rand && rand < AlgoTag.DS.getChoiceSpreadEnd()) {
                problemCountByTag.put(AlgoTag.DS, problemCountByTag.getOrDefault(AlgoTag.DS, 0) + 1);
            } else if (AlgoTag.STRING.getChoiceSpreadStart() <= rand && rand < AlgoTag.STRING.getChoiceSpreadEnd()) {
                problemCountByTag.put(AlgoTag.STRING, problemCountByTag.getOrDefault(AlgoTag.STRING, 0) + 1);
            } else if (AlgoTag.GAP.getChoiceSpreadStart() <= rand && rand < AlgoTag.GAP.getChoiceSpreadEnd()) {
                problemCountByTag.put(AlgoTag.GAP, problemCountByTag.getOrDefault(AlgoTag.GAP, 0) + 1);
            }
        }
        return problemCountByTag;
    }

}
