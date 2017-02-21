package uk.ac.cam.teamhotel.historyphone.utils;

import android.support.v4.util.Pair;

import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;

public class StreamTools {

    public static <L1, L2, R, P extends Pair<L1, R>>
        ObservableTransformer<P, Pair<L2, R>> mapLeft(final Function<L1, L2> map) {
        return upstream -> upstream.map(pair -> new Pair<>(map.apply(pair.first), pair.second));
    }

    public static <L, R1, R2, P extends Pair<L, R1>>
        ObservableTransformer<P, Pair<L, R2>> mapRight(final Function<R1, R2> map) {
        return upstream -> upstream.map(pair -> new Pair<>(pair.first, map.apply(pair.second)));
    }
}
