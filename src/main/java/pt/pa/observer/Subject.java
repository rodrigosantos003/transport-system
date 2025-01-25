package pt.pa.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Subject abstract class to represent Observable
 *
 * @author Rodrigo Santos - 202100722,
 * João Fernandes - 202100718,
 * Rúben Dâmaso - 202100723
 *
 * [PL2 - Prof. André Sanguinetti]
 */
public abstract class Subject implements Observable {

    private List<Observer> observers;

    /**
     * Subject constructor
     */
    public Subject() {
        observers = new ArrayList<>();
    }

    @Override
    public void addObserver(Observer observer) {
        if(!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Object arg) {
        for(Observer o : observers) {
            o.update(this, arg);
        }
    }
}