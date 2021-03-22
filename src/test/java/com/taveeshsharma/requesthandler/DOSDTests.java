package com.taveeshsharma.requesthandler;

import com.taveeshsharma.requesthandler.orchestration.ColorAssignment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class DOSDTests {

    public int findFirstConsecutiveSequence(List<Integer> availableColors, int reqCount) {
        int index = 1;
        int currCount = 1;
        int prev = availableColors.get(0);
        while (index < availableColors.size()) {
            int current = availableColors.get(index);
            if (currCount == reqCount)
                break;
            if (current == prev + 1) {
                currCount++;
            } else {
                currCount = 1;
            }
            prev = current;
            index++;
        }
        return index - reqCount;
    }

    private List<ColorAssignment> mergeColorRanges(List<ColorAssignment> rangesToMerge) {
        rangesToMerge.sort(Comparator.comparing(ColorAssignment::getStart));
        int index = 0;
        for (int i = 1; i < rangesToMerge.size(); i++) {
            if (rangesToMerge.get(index).getEnd() >= rangesToMerge.get(i).getStart()) {
                ColorAssignment merged = new ColorAssignment(
                        Math.min(rangesToMerge.get(index).getStart(), rangesToMerge.get(i).getStart()),
                        Math.max(rangesToMerge.get(index).getEnd(), rangesToMerge.get(i).getEnd())
                );
                rangesToMerge.set(index, merged);
            } else {
                index++;
                rangesToMerge.set(index, rangesToMerge.get(i));
            }
        }
        return rangesToMerge.subList(0, index + 1);
    }

    private List<Integer> removeColorRanges(List<Integer> availableColors, List<ColorAssignment> rangesToRemove) {
        rangesToRemove = mergeColorRanges(rangesToRemove);
        for (ColorAssignment range : rangesToRemove) {
            availableColors.removeIf(color -> (color >= range.getStart() && color <= range.getEnd()));
        }
        return availableColors;
    }

    @Test
    void contextLoads() {
    }

    @Test
    void findFirstConsecutiveSequenceTest() {
        List<Integer> availableColors = Arrays.asList(1, 4, 7, 8, 9, 10);
        int result = findFirstConsecutiveSequence(availableColors, 3);
        Assertions.assertEquals(result, 2);
    }

    @Test
    void mergeColorRangesTest() {
        List<ColorAssignment> rangesToMerge = new ArrayList<ColorAssignment>() {{
            add(new ColorAssignment(6, 8));
            add(new ColorAssignment(1, 9));
            add(new ColorAssignment(2, 4));
            add(new ColorAssignment(4, 7));
        }};
        List<ColorAssignment> expectedResult = new ArrayList<ColorAssignment>() {{
            add(new ColorAssignment(1, 9));
        }};
        List<ColorAssignment> actualResult = mergeColorRanges(rangesToMerge);
        Assertions.assertEquals(actualResult.size(), expectedResult.size());
        for (int i = 0; i < actualResult.size(); i++) {
            Assertions.assertEquals(actualResult.get(i).getStart(), expectedResult.get(i).getStart());
            Assertions.assertEquals(actualResult.get(i).getEnd(), expectedResult.get(i).getEnd());
        }
    }

    @Test
    void removeColorRangesTest() {
        List<Integer> allColors = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            allColors.add(i);
        }
        List<ColorAssignment> rangesToRemove = new ArrayList<ColorAssignment>() {{
            add(new ColorAssignment(3, 4));
            add(new ColorAssignment(7, 9));
        }};
        List<Integer> actual = removeColorRanges(allColors, rangesToRemove);
        List<Integer> expected = Arrays.asList(1, 2, 5, 6, 10);
        Assertions.assertEquals(actual.size(), expected.size());
        for (int i = 0; i < actual.size(); i++){
            Assertions.assertEquals(actual.get(i), expected.get(i));
        }
    }
}
