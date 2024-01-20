/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package processing.zgg.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import lombok.NonNull;

/**
 *
 * @author pbergeron
 */
public class ResourceUtils {

    private ResourceUtils() {
    }

    public static Properties loadAllProperties(@NonNull final String resourceName,
            @NonNull final ClassLoader classLoader) throws IOException {
        final Properties props = new Properties();

        final Enumeration<URL> locations = classLoader.getResources(resourceName);
        while (locations.hasMoreElements()) {
            final URL url = locations.nextElement();

            try (final InputStream in = url.openStream()) {
                props.load(in);
            }
        }

        return props;
    }
    
    public static Properties loadAllProperties(@NonNull final String resourceName)
            throws IOException {
        final ClassLoader classLoader = Thread.currentThread()
                .getContextClassLoader();
        
        return loadAllProperties(resourceName, classLoader);
    }

    /**
     * Scans all classes accessible from the context class loader which belong
     * to the given package and sub-packages.
     *
     * @param packageName The base package
     * @param classLoader class loader
     * @param superclass If provided must be assignable from this class
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static List<Class> findClasses(@NonNull final String packageName,
            @NonNull final ClassLoader classLoader, final Class<?> superclass)
            throws ClassNotFoundException, IOException {        
        final String path = packageName.replace('.', '/');
        final Enumeration<URL> resources = classLoader.getResources(path);
        final List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }

        final List<Class> classes = new ArrayList<>();
        for (final File directory : dirs) {
            classes.addAll(findClasses(directory, packageName, superclass));
        }
        
        return classes;
    }
    
    public static List<Class> findClasses(@NonNull final String packageName,
            final Class<?> superclass) throws ClassNotFoundException, IOException {
        final ClassLoader classLoader = Thread.currentThread()
                .getContextClassLoader();
        
        return findClasses(packageName, classLoader, superclass);
    }

    /**
     * Recursive method used to find all classes in a given directory and
     * sub-directories.
     *
     * @param directory The base directory
     * @param packageName The package name for classes found inside the base
     * directory
     * @param superclass If provided must have the specified superclass
     * @return The classes
     * @throws ClassNotFoundException
     */
    protected static List<Class> findClasses(@NonNull final File directory,
            @NonNull final String packageName, final Class<?> superclass)
            throws ClassNotFoundException {
        final List<Class> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }

        final File[] files = directory.listFiles();
        for (final File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "."
                        + file.getName(), superclass));
            } else if (file.getName().endsWith(".class")) {
                final Class clazz = Class.forName(packageName + '.' + file.getName()
                        .substring(0, file.getName().length() - 6));
                if (superclass == null || superclass.equals(clazz.getSuperclass())) {
                    classes.add(clazz);
                }
            }
        }

        return classes;
    }
}
