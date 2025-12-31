import java.util.Map;
import java.util.HashMap;

class MediaFactoryRegistry {
    private static MediaFactoryRegistry instance;
    private Map<String, MediaFactory> factories = new HashMap<>();

    private MediaFactoryRegistry() {
        registerFactory("document", new DocumentMediaFactory());
        registerFactory("video", new VideoFactory());
        registerFactory("quiz", new QuizFactory());
    }

    public static MediaFactoryRegistry getInstance() {
        if (instance == null)
            instance = new MediaFactoryRegistry();
        return instance;
    }

    public void registerFactory(String type, MediaFactory factory) {
        factories.put(type.toLowerCase(), factory);
    }

    public MediaFactory getFactory(String type) {
        MediaFactory factory = factories.get(type.toLowerCase());
        if (factory == null)
            throw new IllegalArgumentException("Unknown type: " + type);
        return factory;
    }
}