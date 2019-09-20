package com.meterware.simplestub.stubs;
/*
 * Copyright (c) 2019 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class InMemoryFileSystemTest {
  private InMemoryFileSystem fileSystem = InMemoryFileSystem.createInstance();

  @Test(expected = NoSuchFileException.class)
  public void whenFileNotDefined_linesThrowsException() throws IOException {
    Files.lines(toPath("/no/such/file"));
  }

  @Test
  public void detectFileExistence() {
    fileSystem.defineFile("/my/file", "data");

    assertThat(Files.exists(toPath("/no/such/file")), is(false));
    assertThat(Files.exists(toPath("/my/file")), is(true));
    assertThat(Files.notExists(toPath("/no/such/file")), is(true));
    assertThat(Files.notExists(toPath("/my/file")), is(false));
  }

  private Path toPath(String filePath) {
    return fileSystem.getPath(filePath);
  }

  @Test
  public void whenFileDefined_accessViaBufferedReader() throws IOException {
    fileSystem.defineFile("/my/file", "line1\nline2\nline3");

    BufferedReader br = Files.newBufferedReader(toPath("/my/file"));
    assertThat(br.readLine(), equalTo("line1"));
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  @Test
  public void whenFileDefined_accessViaInputStream() throws IOException {
    byte[] expectedContents = {0, 1, 2, 3, 4, 5};
    fileSystem.defineFile("/my/file", expectedContents);

    InputStream in = Files.newInputStream(toPath("/my/file"));
    byte[] contents = new byte[expectedContents.length];
    in.read(contents);

    assertThat(contents, equalTo(expectedContents));
  }

  @Test
  public void whenFileDefined_readLinesAsStream() throws IOException {
    fileSystem.defineFile("/my/file", "line1\nline2\nline3");

    List<String> contents = Files.lines(toPath("/my/file")).collect(Collectors.toList());
    
    assertThat(contents, contains("line1", "line2", "line3"));
  }

  @Test
  public void whenFileDefined_readLinesAsList() throws IOException {
    fileSystem.defineFile("/my/file", "line1\nline2\nline3");

    List<String> contents = Files.readAllLines(toPath("/my/file"));

    assertThat(contents, contains("line1", "line2", "line3"));
  }

  @Test
  public void whenFileDefined_readBytesAsStream() throws IOException {
    byte[] expectedContents = {0, 1, 2, 3, 4, 5};
    fileSystem.defineFile("/my/file", expectedContents);

    byte[] contents = Files.readAllBytes(toPath("/my/file"));

    assertThat(contents, equalTo(expectedContents));
  }
}