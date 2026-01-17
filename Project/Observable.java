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

    public void notifyObservers(Object info) {
        for (Observer o : observers)
            o.update(info);
    }
}