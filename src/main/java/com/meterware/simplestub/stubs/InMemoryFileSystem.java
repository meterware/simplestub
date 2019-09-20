package com.meterware.simplestub.stubs;
/*
 * Copyright (c) 2019 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */

import static com.meterware.simplestub.Stub.createStrictStub;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.AccessMode;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A Test stub for the NIO file system. This enables unit tests to create files in memory,
 * and access them using the {@link java.nio.file.Files} class.
 *
 * The methods rely on a {@link Path} value, normally created by
 * calling {@link java.nio.file.Paths#get(String, String...)}. In a unit test, you would instead
 * call {@link #getPath(String, String...)}.
 *
 * Currently, the following methods are supported:
 * &lt;ul&gt;
 * &lt;li&gt;{@link java.nio.file.Files#exists(Path, LinkOption...)}
 * &lt;li&gt;{@link java.nio.file.Files#lines(Path)}
 * &lt;li&gt;{@link java.nio.file.Files#lines(Path, Charset)}
 * &lt;li&gt;{@link java.nio.file.Files#newBufferedReader(Path)}
 * &lt;li&gt;{@link java.nio.file.Files#newBufferedReader(Path, Charset)}
 * &lt;li&gt;{@link java.nio.file.Files#newInputStream(Path, OpenOption...)}
 * &lt;li&gt;{@link java.nio.file.Files#notExists(Path, LinkOption...)}
 * &lt;li&gt;{@link java.nio.file.Files#readAllBytes(Path)}
 * &lt;li&gt;{@link java.nio.file.Files#readAllLines(Path)}
 * &lt;li&gt;{@link java.nio.file.Files#readAllLines(Path, Charset)}
 * &lt;/ul&gt;
 *
 */
@SuppressWarnings("unused")
public abstract class InMemoryFileSystem extends FileSystem {
  private FileSystemProviderStub provider = createStrictStub(FileSystemProviderStub.class);

  private static InMemoryFileSystem instance;

  /**
   * Creates an instance for a test.
   * @return an in-memory file system
   */
  @SuppressWarnings("WeakerAccess")
  public static InMemoryFileSystem createInstance() {
    return instance = createStrictStub(InMemoryFileSystem.class);
  }

  /**
   * Defines the contents for an in-memory file. Will convert the string to bytes using
   * the default platform encoding.
   * @param filePath the path to the file
   * @param contents a string to use as the file constants
   */
  @SuppressWarnings("WeakerAccess")
  public void defineFile(String filePath, String contents) {
    defineFile(filePath, toBytes(contents));
  }

  private byte[] toBytes(String contents) {
    return Optional.ofNullable(contents).map(String::getBytes).orElse(null);
  }

  /**
   * Defines the contents for an in-memory file. Will convert the string to bytes using
   * the specified encoding.
   * @param filePath the path to the file
   * @param contents a string to use as the file constants
   * @param charset the character set to use to translate the string to bytes
   */
  public void defineFile(String filePath, String contents, String charset) {
    defineFile(filePath, toBytes(contents));
  }

  private byte[] toBytes(String contents, String charset) {
    return Optional.ofNullable(contents).map(s-> getBytes(s, charset)).orElse(null);
  }

  private static byte[] getBytes(String string, String charset) {
    try {
      return string.getBytes(charset);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Defines the contents for an in-memory file.
   * @param filePath the path to the file
   * @param contents the file contents
   */
  @SuppressWarnings("WeakerAccess")
  public void defineFile(String filePath, byte... contents) {
    instance.defineFileContents(filePath, contents);
  }

  public Path getPath(String first, String... more) {
    return createStrictStub(PathStub.class, createPathString(first, more));
  }

  private String createPathString(String first, String[] more) {
    return more.length == 0 ? first : first + "/" + String.join("/", more);
  }

  @Override
  public FileSystemProvider provider() {
    return provider;
  }

  private void defineFileContents(String filePath, byte[] contents) {
    provider.fileContents.put(filePath, contents);
  }

  abstract static class PathStub implements Path {
    private String filePath;

    PathStub(String filePath) {
      this.filePath = filePath;
    }

    @Override
    public FileSystem getFileSystem() {
      return instance;
    }

    @Override
    public Path getFileName() {
      return this;
    }

    @Override
    public String toString() {
      return filePath;
    }
  }

  abstract static class FileSystemProviderStub extends FileSystemProvider {
    private Map<String, byte[]> fileContents = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <A extends BasicFileAttributes> A readAttributes(
        Path path, Class<A> type, LinkOption... options) {
      if (!type.equals(BasicFileAttributes.class))
        throw new IllegalArgumentException("attributes type " + type + " not supported");
      return (A) createAttributes(getFilePath(path));
    }

    static String getFilePath(Path path) {
      if (!(path instanceof PathStub))
        throw new IllegalArgumentException(path.getClass() + " not supported");

      return ((PathStub) path).filePath;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DirectoryStream<Path> newDirectoryStream(
        Path dir, DirectoryStream.Filter<? super Path> filter) {
      return createStrictStub(DirectoryStreamStub.class, this, getFilePath(dir));
    }

    @Override
    public SeekableByteChannel newByteChannel(
        Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs)
        throws NoSuchFileException {
      return Optional.ofNullable(fileContents.get(getFilePath(path)))
          .map(s -> createStrictStub(SeekableByteChannelStub.class, (Object) s))
          .orElseThrow(() -> new NoSuchFileException(path.toString()));
    }

    private BasicFileAttributes createAttributes(String filePath) {
      return createStrictStub(BasicFileAttributesStub.class, isDirectory(filePath));
    }

    private PathType isDirectory(String filePath) {
      for (String key : fileContents.keySet()) {
        if (key.startsWith(filePath + '/')) return PathType.DIRECTORY;
        if (key.equals(filePath)) return PathType.FILE;
      }
      return PathType.NONE;
    }

    @Override
    public void checkAccess(Path path, AccessMode... modes) throws IOException {
      String filePath = getFilePath(path);
      if (!fileContents.containsKey(filePath)) throw new NoSuchFileException(filePath);
    }
  }

  enum PathType {
    DIRECTORY {
      @Override
      boolean isDirectory() {
        return true;
      }
    },
    FILE {
      @Override
      boolean isRegularFile() {
        return true;
      }
    },
    NONE;

    boolean isDirectory() {
      return false;
    }

    boolean isRegularFile() {
      return false;
    }
  }

  abstract static class BasicFileAttributesStub implements BasicFileAttributes {
    private PathType pathType;

    BasicFileAttributesStub(PathType pathType) {
      this.pathType = pathType;
    }

    @Override
    public boolean isDirectory() {
      return pathType.isDirectory();
    }

    @Override
    public boolean isRegularFile() {
      return pathType.isRegularFile();
    }

    @Override
    public Object fileKey() {
      return null;
    }
  }

  abstract static class DirectoryStreamStub<T> implements DirectoryStream<T> {
    List<Path> paths = new ArrayList<>();

    public DirectoryStreamStub(FileSystemProviderStub parent, String root) {
      for (String key : parent.fileContents.keySet())
        if (key.startsWith(root + "/")) paths.add(createStrictStub(PathStub.class, key));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<T> iterator() {
      return (Iterator<T>) paths.iterator();
    }

    @Override
    public void close() {}
  }

  abstract static class SeekableByteChannelStub implements SeekableByteChannel {
    private byte[] contents;
    private int index = 0;

    SeekableByteChannelStub(String contents) {
      this.contents = Optional.ofNullable(contents).map(String::getBytes).orElse(null);
    }

    SeekableByteChannelStub(byte[] contents) {
      this.contents = contents;
    }

    @Override
    public long size() {
      return contents.length;
    }

    @Override
    public int read(ByteBuffer buffer) {
      if (index >= contents.length) return -1;

      int numBytesRead = Math.min(numBytesAvailable(), buffer.remaining());
      buffer.put(Arrays.copyOfRange(contents, index, index+numBytesRead));
      index += numBytesRead;
      return numBytesRead;
    }

    private int numBytesAvailable() {
      return contents.length - index;
    }

    @Override
    public SeekableByteChannel position(long newPosition) {
      index = (int) newPosition;
      return this;
    }

    @Override
    public long position() {
      return index;
    }

    @Override
    public void close() {}
  }
}
