import java.util.ArrayList;
import java.util.List;

abstract class Observable {
    private List<Observer> observers = new ArrayList<>();

    public void registerObserver(Observer o) {
        if (!observers.contains(o))
            observers.add(o);
    }

    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    public void clearObservers() {
        observers.clear();
    }

    public void notifyObservers(Object info) {
        // Créer une copie de la liste pour éviter ConcurrentModificationException
        List<Observer> observersCopy = new ArrayList<>(observers);
        for (Observer o : observersCopy) {
            o.update(info);
        }
    }

    public int countObservers() {
        return observers.size();
    }
}