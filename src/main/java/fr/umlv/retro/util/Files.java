package fr.umlv.retro.util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;

public class Files {
	public static final String OUTPUT_DIR = "output";
	
	public static boolean isClassOrJarFile(Path path) {
		Objects.requireNonNull(path);
		
		return isClassFile(path) || isJarFile(path);
	}
	
	public static boolean isClassFile(Path path) {
		Objects.requireNonNull(path);
		
		return java.nio.file.Files.isRegularFile(path) &&
				path.getFileName().toString().endsWith(".class");
	}
	
	public static boolean isJarFile(Path path) {
		Objects.requireNonNull(path);
		
		return java.nio.file.Files.isRegularFile(path) &&
				path.getFileName().toString().endsWith(".jar");
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
	
	private static Map<Path, byte[]> getResourcesFromJar(Path jarPath) {
		Objects.requireNonNull(jarPath);
		
		Map<Path, byte[]> resources = new HashMap<Path, byte[]>();
		
		try ( var jar = new JarInputStream(java.nio.file.Files.newInputStream(jarPath)) ) {
			for (var jarEntry = jar.getNextJarEntry(); !Objects.isNull(jarEntry); jarEntry = jar.getNextJarEntry()) {
				if ( jarEntry.getName().endsWith(".class") ) {
					resources.put(
							jarPath.resolve(jarEntry.getName()),
							jar.readAllBytes());
					jar.closeEntry();
				}
			}	
		} catch (IOException e) {
			e.printStackTrace();
			return Map.of(jarPath, new byte[] {});
		}
		
		return resources;
	}
	
	public static Map<Path, byte[]> getResourceFiles(List<String> files) {
		Objects.requireNonNull(files);
		
		Set<Path> uniquePaths = getUniquePaths(files);
		Map<Path, byte[]> resources = uniquePaths.stream().filter(Files::isClassFile).collect(Collectors.toMap(Function.identity(), path -> {
			try {
				return java.nio.file.Files.readAllBytes(path);
			} catch (IOException e) {
				e.printStackTrace();
				return new byte[] {};
			}
		}));
		
		uniquePaths.stream().filter(Files::isJarFile).forEach(jarPath -> resources.putAll(getResourcesFromJar(jarPath)));
		
		return resources;
	}
	
	private static Map<ZipEntry, byte[]> copyJar(Path sourceJarPath) throws IOException {
		Objects.requireNonNull(sourceJarPath);
		
		try ( var jar = new ZipInputStream(java.nio.file.Files.newInputStream(sourceJarPath)) ) {
			var jarEntries = new LinkedHashMap<ZipEntry, byte[]>();
			for (var jarEntry = jar.getNextEntry(); !Objects.isNull(jarEntry); jarEntry = jar.getNextEntry()) {
				jarEntries.put(jarEntry, jar.readAllBytes());
				jar.closeEntry();
			}
			
			return jarEntries;
		}
	}
	
	private static void createJarAndReplaceClass(Map<ZipEntry, byte[]> dataCopy, Path destJar, Path classPath, byte[] classData) throws IOException {
		Objects.requireNonNull(dataCopy);
		Objects.requireNonNull(destJar);
		Objects.requireNonNull(classPath);
		Objects.requireNonNull(classData);
		
		try (var output = new JarOutputStream(java.nio.file.Files.newOutputStream(destJar))) {
			for (var copyFile : dataCopy.entrySet()) {
				var dataEntry = copyFile.getKey().getName().equals(classPath.toString()) ? classData : copyFile.getValue();
				output.putNextEntry(new ZipEntry(copyFile.getKey().getName()));
				output.write(dataEntry);
				output.closeEntry();
			}
			
			output.flush();
		}
	}
	
	private static void generateOutputJarFile(Entry<Path, Path> jarClassPath, Path outputFolder, byte[] classData) throws IOException {
		Objects.requireNonNull(jarClassPath);
		Objects.requireNonNull(outputFolder);
		Objects.requireNonNull(classData);
		
		Path sourceJar;
		
		if ( java.nio.file.Files.exists(outputFolder.resolve(jarClassPath.getKey().getFileName())) ) {
			sourceJar = outputFolder.resolve(jarClassPath.getKey().getFileName());
		} else {
			sourceJar = jarClassPath.getKey();
		}
		
		var dataCopy = copyJar(sourceJar);
		createJarAndReplaceClass(dataCopy, outputFolder.resolve(jarClassPath.getKey().getFileName()), jarClassPath.getValue(), classData);
	}
	
	private static Map.Entry<Path, Path> getClassPathFromJar(Path fullPath) {
		Objects.requireNonNull(fullPath);
		Path p;
		
		// On remonte jusqu'à ce qu'on trouve un fichier jar
		for (p = fullPath; !(Objects.isNull(p) || isJarFile(p)); p = p.getParent());
		
		return Objects.isNull(p) ? null : new AbstractMap.SimpleEntry<Path, Path>(p, fullPath.subpath(p.getNameCount(), fullPath.getNameCount()));
	}

	public static void generateOutputFile(ClassNode cn, Path originPath) throws IOException {
		Objects.requireNonNull(cn);
		Objects.requireNonNull(originPath);
		Path outputFolder = Paths.get(System.getProperty("user.dir"), OUTPUT_DIR);
		Map.Entry<Path, Path> jarClassPath;
		
		ClassWriter cw = new ClassWriter(COMPUTE_MAXS | COMPUTE_FRAMES);
		cn.accept(cw);
		
		if ( !java.nio.file.Files.exists(outputFolder) ) {
			java.nio.file.Files.createDirectory(outputFolder);
		}
		
		System.out.println("Generating " + originPath);
		
		if ( !Objects.isNull(jarClassPath = getClassPathFromJar(originPath)) ) {
			generateOutputJarFile(jarClassPath, outputFolder, cw.toByteArray());
		} else {
			java.nio.file.Files.write(outputFolder.resolve(originPath.getFileName().toString()), cw.toByteArray());
		}
	}
}
