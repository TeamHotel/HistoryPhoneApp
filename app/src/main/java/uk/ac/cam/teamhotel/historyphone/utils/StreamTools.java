package uk.ac.cam.teamhotel.historyphone.utils;

import android.support.v4.util.Pair;

import java.util.ArrayList;

import io.reactivex.Observable;

public class StreamTools {
    public static <L, R, P extends Pair<L, R>> Observable<Pair<ArrayList<L>, ArrayList<R>>>
            unzipPairs(Observable<ArrayList<P>> input) {
        return input.map(pairs -> {
            Pair<ArrayList<L>, ArrayList<R>> lists = new Pair<>(
                    new ArrayList<L>(),
                    new ArrayList<R>());
            for (int i = 0; i < pairs.size(); i++) {
                lists.first.add(pairs.get(i).first);
                lists.second.add(pairs.get(i).second);
            }
            return lists;
        });
    }

    public static <L, R> Observable<ArrayList<Pair<L, R>>> zipPairs(
            Observable<ArrayList<L>> left, Observable<ArrayList<R>> right) {
        return left.zipWith(right, (leftList, rightList) -> {
            if (leftList.size() != rightList.size()) {
                throw new ZipException("Array sizes must match.");
            }
            ArrayList<Pair<L, R>> pairs = new ArrayList<>();
            for (int i = 0; i < leftList.size(); i++) {
                pairs.add(new Pair<>(leftList.get(i), rightList.get(i)));
            }
            return pairs;
        });
    }

    public static <L, R> Observable<ArrayList<L>> projLeft(
            Observable<Pair<ArrayList<L>, ArrayList<R>>> input) {
        return input.map(lists -> lists.first);
    }

    public static <L, R> Observable<ArrayList<R>> projRight(
            Observable<Pair<ArrayList<L>, ArrayList<R>>> input) {
        return input.map(lists -> lists.second);
    }
}
