package uk.ac.cam.teamhotel.historyphone.utils;

import android.support.v4.util.Pair;
import android.util.Log;

import java.util.ArrayList;

public class ListTools {

    public static <L, R, P extends Pair<L, R>>
            Pair<ArrayList<L>, ArrayList<R>> unzipPairs(ArrayList<P> pairs) {
        Pair<ArrayList<L>, ArrayList<R>> lists = new Pair<>(
                new ArrayList<L>(),
                new ArrayList<R>());
        for (int i = 0; i < pairs.size(); i++) {
            lists.first.add(pairs.get(i).first);
            lists.second.add(pairs.get(i).second);
        }
        return lists;
    }

    public static <L, R> ArrayList<Pair<L, R>> zipPairs(ArrayList<L> left,
                                                        ArrayList<R> right) throws ZipException {
        if (left.size() != right.size()) {
            throw new ZipException("Array sizes must match.");
        }
        ArrayList<Pair<L, R>> pairs = new ArrayList<>();
        for (int i = 0; i < left.size(); i++) {
            pairs.add(new Pair<>(left.get(i), right.get(i)));
        }
        return pairs;
    }

    public static <L, R> ArrayList<L> projLeft(Pair<ArrayList<L>, ArrayList<R>> input) {
        return input.first;
    }

    public static <L, R> ArrayList<R> projRight(Pair<ArrayList<L>, ArrayList<R>> input) {
        return input.second;
    }
}
