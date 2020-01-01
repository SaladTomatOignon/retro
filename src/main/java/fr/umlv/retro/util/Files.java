package fr.umlv.retro.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Files {
	public static boolean isClassOrJarFile(Path path) {
		Objects.requireNonNull(path);
		
		return java.nio.file.Files.isRegularFile(path) &&
				(path.getFileName().toString().endsWith(".class") || path.getFileName().toString().endsWith(".jar"));
	}
	
	private static Set<Path> getSubFiles(Path dir) {
		Objects.requireNonNull(dir);
		
		try {
			return java.nio.file.Files.list(dir).filter(Files::isClassOrJarFile).collect(Collectors.toSet());
		} catch (IOException e) {
			e.printStackTrace();
			return Collections.emptySet();
		}
	}
	
	public static Set<Path> getUniquePaths(List<String> files) {
		Objects.requireNonNull(files);
		
		Set<Path> paths = new HashSet<Path>(files.size());
		
		files.forEach(file -> {
			Path item = Paths.get(System.getProperty("user.dir"), file);
			
			if ( isClassOrJarFile(item) ) {
				paths.add(item);
			} else if ( java.nio.file.Files.isDirectory(item) ) {
				paths.addAll(getSubFiles(item));
			} else {
				throw new IllegalArgumentException(item + " is not a class/jar file or a directory");
			}
		});
		
		return paths;
	}
	
	public static List<InputStream> getRessourceFiles(List<String> files) {
		Objects.requireNonNull(files);
		
		return getUniquePaths(files).stream().map(path -> {
			try {
				return java.nio.file.Files.newInputStream(path);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}).filter(file -> !Objects.isNull(file)).collect(Collectors.toList());
	}
}
