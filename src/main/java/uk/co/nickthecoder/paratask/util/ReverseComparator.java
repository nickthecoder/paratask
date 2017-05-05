package uk.co.nickthecoder.paratask.util;

import java.util.Comparator;

/**
 * Reverses the normal ordering of a comparator.
 *
 * @param <T>
 * 
 * @priority 4
 */
public class ReverseComparator<T>
    implements Comparator<T>
{
    private Comparator<T> _comparator;

    public ReverseComparator(Comparator<T> comparator)
    {
        _comparator = comparator;
    }

    @Override
    public int compare(T o1, T o2)
    {
        return _comparator.compare(o2, o1);
    }

}
