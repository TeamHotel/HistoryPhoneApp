package uk.ac.cam.teamhotel.historyphone.artifact;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class ArtifactLoader {

    private Observable<Artifact[]> artifactStream;

    /**
     * Initialise stream to track incoming UUID arrays and convert them to artifact
     * arrays, making use of a cache (the same UUID will always return the same,
     * immutable artifact object).
     *
     * @param uuidStream Rx observable stream of UUID arrays.
     * @param artifactCache Cache of previously retrieved artifact objects.
     */
    public ArtifactLoader(Observable<Long[]> uuidStream, final ArtifactCache artifactCache
                          /* TODO: Pass db service provider. */) {
        artifactStream = uuidStream
                .map(new Function<Long[], Artifact[]>() {
                    @Override
                    public Artifact[] apply(Long[] uuids) throws Exception {
                        // Map a list of beacon UUIDs to a corresponding list of artifacts.
                        Artifact[] artifacts = new Artifact[uuids.length];
                        for (int i = 0; i < uuids.length; i++) {
                            artifacts[i] = artifactCache.get(uuids[i]);
                            // If the artifact has not been cached, attempt to load it from the db.
                            if (artifacts[i] == null) {
                                // TODO: Request artifact from database.
                            }
                        }
                        return artifacts;
                    }
                });
    }

    /**
     * Get the resultant artifact array stream.
     */
    public Observable<Artifact[]> getArtifactStream() {
        return artifactStream;
    }
}
