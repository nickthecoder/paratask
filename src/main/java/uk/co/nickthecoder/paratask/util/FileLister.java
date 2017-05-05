package uk.co.nickthecoder.paratask.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lists the contents of a directory. Does much more than {@link File#list()}!
 *
 */
public class FileLister
    implements FileFilter
{
    /**
     * Compares files based on their path, ignoring case, so that "B" and "b" are both greater than "A" and "a".
     */
    public static final Comparator<File> NAME_ORDER = new Comparator<File>()
    {
        @Override
        public int compare(File a, File b)
        {
            return a.getPath().toLowerCase().compareTo(b.getPath().toLowerCase());
        }

    };

    /**
     * Compares files based on how their path name strings compare. Note this is case sensitive, and is therefore
     * not usually the best solution. Consider {@link #NAME_ORDER} instead.
     */
    public static final Comparator<File> PATH_ORDER = new Comparator<File>()
    {
        @Override
        public int compare(File a, File b)
        {
            return a.getPath().compareTo(b.getPath());
        }

    };

    public static final Comparator<File> SIZE_ORDER = new Comparator<File>()
    {
        @Override
        public int compare(File a, File b)
        {
            long lena = a.length();
            long lenb = b.length();
            if (lena == lenb) {
                return 0;
            }
            if (lena > lenb) {
                return 1;
            } else {
                return -1;
            }
        }

    };

    public static final Comparator<File> LAST_MODIFIED_ORDER = new Comparator<File>()
    {
        @Override
        public int compare(File a, File b)
        {
            long al = a.lastModified();
            long bl = b.lastModified();

            if (al == bl) {
                return 0;
            }
            if (al > bl) {
                return 1;
            } else {
                return -1;
            }
        }

    };

    /**
     * Ways of sorting a recursive tree, either unsorted, each directory sorted individually, or all files sorted
     * together.
     * 
     * @priority 5
     */
    public enum Sort
    {
        UNSORTED, DIRECTORY, ALL
    };

    /**
     * The depth of recursion through the file system. 1 lists a single directory,
     * 0 lists nothing.
     */
    private int _depth = 1;

    /**
     * If true, then the base directory to the listFile method is included in the results.
     * Default is false.
     */
    private boolean _includeBase = false;

    /**
     * Sort a each directory individually, all together or not at all.
     * The default is to sort each directory individually.
     */
    private Sort _sort = Sort.DIRECTORY;

    private Comparator<File> _order = NAME_ORDER;

    /**
     * Includes only sub-directories, and may ignore hidden directories depending on the value of {@link #_enterHidden}
     * and {@link #_includeHidden}.
     * Also the custom filter ({@link #_customFilter}) may also filter out some sub-directories.
     */
    private FileFilter _subDirectoryFilter = new SubDirectoryFilter();

    /**
     * If false, then only directories will included in the listing.
     */
    private boolean _includeFiles = true;

    /**
     * If false then directories will not be included in the listing.
     */
    private boolean _includeDirectories = false;

    /**
     * If false (which is the default), then hidden files and directories will not be in the listing.
     */
    private boolean _includeHidden = false;

    /**
     * If false, then hidden sub-directories will not be visited.
     */
    private boolean _enterHidden = false;

    /**
     * A list of file extensions to be included in the listings. If this is null, then all files are included.
     * Note, the file extension is defined as the part after then final period. For example a file called "foo.tar.gz",
     * the extension is "gz", NOT "tar.gz".
     */
    private String[] _fileExtensions;

    private FileFilter _customFilter = null;

    /**
     * If true, then the canonical names are returned. Note that finding canonical names can cause an exception to be
     * thrown, which will cause the listing to be aborted. If you wish to handle these kind of errors yourself, then
     * set _canonical to false, and call {@link File#getCanonicalFile()} on each item listed.
     */
    private boolean _canonical = false;

    /**
     * If errors are detected along the way, write them out, but carry on going.
     * 
     * @see #getErorrs()
     */
    private StringWriter _errorWriter = new StringWriter();

    private PrintWriter _errors = new PrintWriter(_errorWriter);

    private boolean stopping = false;
    
    /**
     * Constructor
     */
    public FileLister()
    {
    }

    /**
     * Files will not be sorted in any particular order. The value of {@link #setOrder(Comparator)} will be ignored.
     * A fluent version of {@link #setSort(Sort)}.
     * 
     * @return this
     */
    public FileLister unsorted()
    {
        setSort(Sort.UNSORTED);
        return this;
    }

    /**
     * Files will be sorted together in one go, as opposed to the default or sorting the files in each directory
     * separately.
     * A fluent version of {@link #setSort(Sort)}.
     * 
     * @return this
     */
    public FileLister sortTogether()
    {
        setSort(Sort.ALL);
        return this;
    }

    /**
     * Determines when/if the files in the results are sorted.
     * Note : HOW the files are sorted is determined by {@link #setOrder(Comparator)}.
     * 
     * @param value
     *            {@link Sort#UNSORTED} : the results will be grouped by their containing directory, but not sorted in
     *            any particular order.
     * 
     *            {@link Sort#ALL} : then the results will be sorted all together in one go. Files from
     *            different directories will NOT be grouped together.
     * 
     *            {@link Sort#DIRECTORY} : Files are grouped by their containing directory. This is the default.
     * 
     */
    public void setSort(Sort value)
    {
        _sort = value;
    }

    public Sort getSort()
    {
        return _sort;
    }

    /**
     * Determines how files and directories are ordered.
     * A fluent version of {@link #setOrder(Comparator)}.
     * 
     * @param order
     * @return this
     */
    public FileLister order(Comparator<File> order)
    {
        setOrder(order);
        return this;
    }

    /**
     * Determines how files and directories are ordered.
     * 
     * @param order
     */
    public void setOrder(Comparator<File> order)
    {
        _order = order;
    }

    public Comparator<File> getOrder()
    {
        return _order;
    }

    /**
     * Reverses the ordering. If {@link #order(Comparator)} is called after this, then the reverse will be ignored.
     * A fluent API.
     * 
     * @return this
     */
    public FileLister reverse()
    {
        _order = new ReverseComparator<File>(_order);
        return this;
    }

    /**
     * When listing both files and directories, calling this will make the directories come
     * before the files. When combined with the "reverse" method, the order is important.
     * If directoriesFirst is called after "reverse", the directories will still appear first.
     * Whereas is reverse is called after, then the directories will end up at the end.
     * 
     * Methods which set the order (such as order, setOrder) will override this call, so
     * always call directoriesFirst after you have set the general order.
     * 
     * @return this
     */
    public FileLister directoriesFirst()
    {
        _order = new DirectoriesFirstComparator(_order);
        return this;
    }

    /**
     * The depth of recursion when scanning a tree of directories.
     * A fluent version of {@link #setDepth(int)}.
     * 
     * @param value
     * @return this
     */
    public FileLister depth(int value)
    {
        setDepth(value);
        return this;
    }

    /**
     * The depth of recursion when scanning a tree of directories.
     * 
     * @param value
     *            1 to scan a single directory only (which is the default). Any value more than one to recurse into
     *            sub-directories to the given depth.
     */
    public void setDepth(int value)
    {
        _depth = value;
    }

    public int getDepth()
    {
        return _depth;
    }

    /**
     * Canonical files will be returned in the listing. Note that finding canonical names can cause an
     * exception to be thrown, which will cause the listing to be aborted. If you wish to handle these kind of errors
     * yourself, then do NOT call this method, and instead call {@link File#getCanonicalFile()} on each item listed.
     */
    public FileLister canonical()
    {
        setCanonical(true);
        return this;
    }

    /**
     * If true, then the canonical names will be returned in the listing. Note that finding canonical names can cause an
     * exception to be thrown, which will cause the listing to be aborted. If you wish to handle these kind of errors
     * yourself, then keep with the default value "false", and call {@link File#getCanonicalFile()} on each item listed.
     */
    public void setCanonical(boolean value)
    {
        _canonical = value;
    }

    public boolean getCanonical()
    {
        return _canonical;
    }

    /**
     * Include the base directory in the list of results.
     * Fluent API verions.
     * 
     * @return this
     */
    public FileLister includeBase()
    {
        setIncludeBase(true);
        return this;
    }

    /**
     * If value == true, include the base directory in the list of results.
     */
    public void setIncludeBase(boolean value)
    {
        _includeBase = value;
    }

    public boolean getIncludeBase()
    {
        return _includeBase;
    }

    /**
     * Exclude files in the results (only return directories).
     * Fluent version of {@link #setIncludeFiles(boolean)}.
     * 
     * @return this
     */
    public FileLister excludeFiles()
    {
        setIncludeFiles(false);
        return this;
    }

    /**
     * Should files (as opposed to directories) be included in the results.
     */
    public void setIncludeFiles(boolean value)
    {
        _includeFiles = value;
    }

    public boolean getIncludeFiles()
    {
        return _includeFiles;
    }

    /**
     * Files will be excluded and directories will be included in the results.
     * Fluent API version of {@link #setIncludeFiles(boolean)} and {@link #setIncludeDirectories(boolean)}.
     * 
     * @return this
     */
    public FileLister onlyDirectories()
    {
        setIncludeDirectories(true);
        setIncludeFiles(false);
        return this;
    }

    /**
     * Include directories in the results.
     * Fluent API version of {@link #setIncludeDirectories(boolean)}.
     * 
     * @return this
     */
    public FileLister includeDirectories()
    {
        setIncludeDirectories(true);
        return this;
    }

    /**
     * Should directories be included in the list of results. The default is false (only files are listed).
     * 
     * @param value
     */
    public void setIncludeDirectories(boolean value)
    {
        _includeDirectories = value;
    }

    public boolean getIncludeDirectories()
    {
        return _includeDirectories;
    }

    /**
     * Includes hidden files in the list of results.
     * Fluent version of {@link #setIncludeHidden(boolean)}.
     * 
     * @return this
     */
    public FileLister includeHidden()
    {
        setIncludeHidden(true);
        return this;
    }

    /**
     * Hidden files are included in the list of results, only if value is true.
     * The default is to exclude hidden files.
     * 
     * @param value
     */
    public void setIncludeHidden(boolean value)
    {
        _includeHidden = value;
    }

    public boolean getIncludeHidden()
    {
        return _includeHidden;
    }

    /**
     * When recursing through a tree, should causes hidden directories to be visited.
     * Fluent version of {{@link #setEnterHidden(boolean)}.
     * 
     * @return this
     */
    public FileLister enterHidden()
    {
        setEnterHidden(true);
        return this;
    }

    /**
     * When recursing though a tree, should hidden directories be visited?
     * The default is to skip hidden directories.
     * 
     * @param value
     */
    public void setEnterHidden(boolean value)
    {
        _enterHidden = value;
    }

    public boolean getEnterHidden()
    {
        return _enterHidden;
    }

    /**
     * A convenience method for beanshell, which doesn't currently handle varargs, so the extensions( String... ) isn't
     * usable.
     * 
     * @param filextension
     *            The single extension used to filter the results (files only).
     * @return this
     */
    public FileLister extension(String fileExtension)
    {
        setFileExtensions(new String[] { fileExtension });
        return this;
    }

    /**
     * The same as extensions( String... ), but here just for beanshell which doesn't support varargs yet.
     * 
     * @param fileExtensions
     * @return this
     */
    public FileLister extensionArray(String[] fileExtensions)
    {
        setFileExtensions(fileExtensions);
        return this;
    }

    /**
     * A fluent version of {@link #setFileExtensions(String[])}.
     * 
     * @param fileExtensions
     * @return this
     */
    public FileLister extensions(String... fileExtensions)
    {
        setFileExtensions(fileExtensions);
        return this;
    }

    /**
     * Filters the results based on file extensions.
     * 
     * @param fileExtensions
     *            A list of file extensions which will be accepted. Files with other file extensions will not
     *            be included in the results.
     */
    public void setFileExtensions(String[] fileExtensions)
    {
        _fileExtensions = fileExtensions;
    }

    public String[] getFileExtensions()
    {
        return _fileExtensions;
    }

    public FileLister filterFiles(Pattern pattern)
    {
        setFilePattern(pattern);
        return this;
    }

    private Pattern filePattern;

    public void setFilePattern(Pattern pattern)
    {
        filePattern = pattern;
    }

    public Pattern getFilePattern()
    {
        return filePattern;
    }

    public FileLister filePattern(Pattern pattern)
    {
        setFilePattern(pattern);
        return this;
    }

    private Pattern directoryPattern;

    public void setDirectoryPattern(Pattern pattern)
    {
        directoryPattern = pattern;
    }

    public Pattern getDirectoryPattern()
    {
        return directoryPattern;
    }

    public FileLister directoryPattern(Pattern pattern)
    {
        setDirectoryPattern(pattern);
        return this;
    }

    /**
     * A fluent version of {@link #setCustomFilter(FileFilter)}.
     * If this is called twice, the filter from the first call will be ignored. However, future versions of this library
     * may change this behaviour, so do NOT call this twice!
     * 
     * @param filter
     * @return this
     */
    public FileLister filter(FileFilter filter)
    {
        setCustomFilter(filter);
        return this;
    }

    /**
     * Filters the results, to only include files/directories which are accepted by filter.
     * 
     * @param filter
     */
    public void setCustomFilter(FileFilter filter)
    {
        _customFilter = filter;
    }

    public FileFilter getCustomFilter()
    {
        return _customFilter;
    }

    /**
     * If errors occurred while listing, then they are ignored, so that the lister can continue doing as much as
     * possible.
     * This lets you see the errors.
     * 
     * @return The list of error, one error per line, or an empty string if no errors occurred.
     */
    public String getErorrs()
    {
        return _errorWriter.toString();
    }

    /**
     * Performs the listing.
     * 
     * @param directoryPath
     *            The directory to be listed.
     * @return A list of files meeting all of the filtering criteria, sorted as requested.
     * @throws IOException
     */
    public List<File> listFiles(String directoryPath)
    {
        return listFiles(new File(directoryPath));
    }

    /**
     * Performs the listing.
     * 
     * @param directory
     *            The directory to be listed.
     * @return A list of files meeting all of the filtering criteria, sorted as requested.
     * @throws IOException
     */
    public List<File> listFiles(File directory)
    {
        stopping = false;
        
        List<File> results = new ArrayList<File>();

        if (_includeBase) {
            results.add(directory);
        }

        if (_depth > 0) {
            listFiles(results, directory, 1);
        }

        if (_sort == Sort.ALL) {
            Collections.sort(results, _order);
        }
        return results;
    }

    /**
     * Lists a directory, appending the results to the 'results' list.
     * When recursing a tree, this will be called recursively for each directory being scanned.
     * 
     * @param results
     *            List where the results are stored
     * @param directory
     *            The (sub) directory being scanned
     * @param depth
     *            The depth of the tree search (decremented for each level of recursion).
     * @throws IOException
     */
    private void listFiles(List<File> results, File directory, int depth)
    {
        if ( stopping ) {
            return;
        }
        
        File[] files = directory.listFiles(this);
        if (files == null) {
            _errors.println("Failed to list directory " + directory);
            return;
        }

        if (_sort == Sort.DIRECTORY) {
            Arrays.sort(files, _order);
        }
        for (File file : files) {
            if (_canonical) {
                try {
                    file = file.getCanonicalFile();
                } catch (Exception e) {
                    _errors.println(e);
                }
            }
            results.add(file);

            if ((depth < _depth) && _includeDirectories && file.isDirectory()) {
                listFiles(results, file, depth + 1);

            }
        }

        if (depth < _depth && (!_includeDirectories)) {
            File[] subDirs = directory.listFiles(_subDirectoryFilter);
            if (_sort == Sort.DIRECTORY) {
                Arrays.sort(subDirs, _order);
            }
            for (File subDir : subDirs) {
                listFiles(results, subDir, depth + 1);
            }
        }
    }

    /**
     * Will the file (or directory) be included in the list of results?
     * 
     */
    @Override
    public boolean accept(File file)
    {
        boolean isDirectory = file.isDirectory();

        if (!_includeFiles && !isDirectory) {
            return false;
        }

        if (!_includeDirectories && isDirectory) {
            return false;
        }

        if (isDirectory) {
            if (!_enterHidden && !_includeHidden && file.isHidden()) {
                return false;
            }
            if (directoryPattern != null) {
                Matcher directoryMatcher = directoryPattern.matcher(file.getName());
                if (!directoryMatcher.matches()) {
                    return false;
                }
            }

        } else {
            if (!_includeHidden && file.isHidden()) {
                return false;
            }
            if (filePattern != null) {
                Matcher fileMatcher = filePattern.matcher(file.getName());
                if (!fileMatcher.matches()) {
                    return false;
                }
            }
        }

        if (_fileExtensions != null) {
            int lastDot = file.getName().lastIndexOf('.');
            if (lastDot < 0) {
                return false;
            }
            String fe = file.getName().substring(lastDot + 1);
            for (String allowed : _fileExtensions) {
                if (fe.equals(allowed)) {
                    return true;
                }
            }
            return false;
        }

        if (_customFilter != null) {
            return _customFilter.accept(file);
        }
        return true;
    }

    /**
     * Decides if a sub-directory should be entered when recursing through a tree of directories.
     * Based on {@link FileLister#_enterHidden}, {@link FileLister#_includeHidden} and {@link FileLister#_customFilter}.
     */
    class SubDirectoryFilter implements FileFilter
    {
        @Override
        public boolean accept(File file)
        {
            if (!file.isDirectory()) {
                return false;
            }

            if (!_enterHidden && !_includeHidden && file.isHidden()) {
                return false;
            }

            if (_customFilter != null) {
                return _customFilter.accept(file);
            }

            return true;
        }

    }

    // TODO Re-implement Stoppable, and add override
    public void stop()
    {
        stopping = true;
    }

}
