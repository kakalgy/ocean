package com.yusj.seal.io;

import com.yusj.seal.lang.AutoCloseable;

import java.io.IOException;

public interface Closeable extends AutoCloseable {
    public void close() throws IOException;
}
