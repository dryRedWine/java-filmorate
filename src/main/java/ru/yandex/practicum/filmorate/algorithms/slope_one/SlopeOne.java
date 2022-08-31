package ru.yandex.practicum.filmorate.algorithms.slope_one;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SlopeOne {

    // Алгоритм находит пользователей с макс общими лайками(их мб несколько)
    // и возвращает фильмы этих пользователей, которые ты не лайкнул
    public List<Long> getRecommendations(List<Long> mainList, Map<Long, List<Long>> otherMap) {
        int max = 0;
        Long targetId;
        List<Long> targetList = new ArrayList<>();

        for (Map.Entry<Long, List<Long>> entry : otherMap.entrySet()) {
            List<Long> common = new ArrayList<>(mainList);
            common.retainAll(entry.getValue());
            if (common.size() >= max) {
                max = common.size();
                targetList = entry.getValue();
            }
        }
        // Избавляюсь от дубликатов
        Set<Long> set = new HashSet<>(targetList);
        targetList.clear();
        targetList.addAll(set);
        // Формирую список id для рекомендации
        List<Long> diff = new ArrayList<>(targetList);
        diff.removeAll(mainList);
        return diff;
    }


}