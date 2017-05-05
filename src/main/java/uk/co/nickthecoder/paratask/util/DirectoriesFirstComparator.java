package uk.co.nickthecoder.paratask.util;

import java.io.File;
import java.util.Comparator;

/**
 * Used to order a directory, such that sub-directories come before files.
 * Note, the directories and files will then be ordered according to the Comparator passed
 * to the constructor. If no Comparator is given, then the files and directories will be ordered
 * alphabetically.
 * 
 * @priority 4
 */
public class DirectoriesFirstComparator implements Comparator<File>
{
    private Comparator<File> _comparator;

    public DirectoriesFirstComparator()
    {
        this(FileLister.NAME_ORDER);
    }

    public DirectoriesFirstComparator(Comparator<File> comparator)
    {
        _comparator = comparator;
    }

    @Override
    public int compare(File a, File b)
    {
        if (a.isFile()) {
            if (b.isDirectory()) {
                return 1;
            }
        } else {
            if (b.isFile()) {
                return -1;
            }
        }

        return _comparator.compare(a, b);
    }

}
