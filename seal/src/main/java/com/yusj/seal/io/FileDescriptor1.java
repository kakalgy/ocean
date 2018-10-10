package com.yusj.seal.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.SyncFailedException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author kakalgy
 * @Date 2018/10/10 22:31
 **/
public final class FileDescriptor1 {
    private int fd;

    private long handle;

    private java.io.Closeable parent;
    private List<java.io.Closeable> otherParents;
    private boolean closed;

    /**
     * Constructs an (invalid) FileDescriptor1
     * object.
     */
    public /**/ FileDescriptor1() {
        fd = -1;
        handle = -1;
    }

    static {
        initIDs();
    }

    // Set up JavaIOFileDescriptorAccess in SharedSecrets
    static {
        //由于内部原因无法生成代码
//        sun.misc.SharedSecrets.setJavaIOFileDescriptorAccess(
//                new sun.misc.JavaIOFileDescriptorAccess() {
//                    public void set(FileDescriptor1 obj, int fd) {
//                        obj.fd = fd;
//                    }
//
//                    public int get(FileDescriptor1 obj) {
//                        return obj.fd;
//                    }
//
//                    public void setHandle(FileDescriptor1 obj, long handle) {
//                        obj.handle = handle;
//                    }
//
//                    public long getHandle(FileDescriptor1 obj) {
//                        return obj.handle;
//                    }
//                }
//        );
    }

    /**
     * A handle to the standard input stream. Usually, this file
     * descriptor is not used directly, but rather via the input stream
     * known as {@code System.in}.
     *
     * @see     java.lang.System#in
     */
    public static final FileDescriptor1 in = standardStream(0);

    /**
     * A handle to the standard output stream. Usually, this file
     * descriptor is not used directly, but rather via the output stream
     * known as {@code System.out}.
     * @see     java.lang.System#out
     */
    public static final FileDescriptor1 out = standardStream(1);

    /**
     * A handle to the standard error stream. Usually, this file
     * descriptor is not used directly, but rather via the output stream
     * known as {@code System.err}.
     *
     * @see     java.lang.System#err
     */
    public static final FileDescriptor1 err = standardStream(2);

    /**
     * Tests if this file descriptor object is valid.
     *
     * @return  {@code true} if the file descriptor object represents a
     *          valid, open file, socket, or other active I/O connection;
     *          {@code false} otherwise.
     */
    public boolean valid() {
        return ((handle != -1) || (fd != -1));
    }

    /**
     * Force all system buffers to synchronize with the underlying
     * device.  This method returns after all modified data and
     * attributes of this FileDescriptor1 have been written to the
     * relevant device(s).  In particular, if this FileDescriptor1
     * refers to a physical storage medium, such as a file in a file
     * system, sync will not return until all in-memory modified copies
     * of buffers associated with this FileDesecriptor have been
     * written to the physical medium.
     *
     * sync is meant to be used by code that requires physical
     * storage (such as a file) to be in a known state  For
     * example, a class that provided a simple transaction facility
     * might use sync to ensure that all changes to a file caused
     * by a given transaction were recorded on a storage medium.
     *
     * sync only affects buffers downstream of this FileDescriptor1.  If
     * any in-memory buffering is being done by the application (for
     * example, by a BufferedOutputStream object), those buffers must
     * be flushed into the FileDescriptor1 (for example, by invoking
     * OutputStream.flush) before that data will be affected by sync.
     *
     * @exception SyncFailedException
     *        Thrown when the buffers cannot be flushed,
     *        or because the system cannot guarantee that all the
     *        buffers have been synchronized with physical media.
     * @since     JDK1.1
     */
    public native void sync() throws SyncFailedException;

    /* This routine initializes JNI field offsets for the class */
    private static native void initIDs();

    private static native long set(int d);

    private static FileDescriptor1 standardStream(int fd) {
        FileDescriptor1 desc = new FileDescriptor1();
        desc.handle = set(fd);
        return desc;
    }

    /*
     * Package private methods to track referents.
     * If multiple streams point to the same FileDescriptor1, we cycle
     * through the list of all referents and call close()
     */

    /**
     * Attach a Closeable to this FD for tracking.
     * parent reference is added to otherParents when
     * needed to make closeAll simpler.
     */
    synchronized void attach(java.io.Closeable c) {
        if (parent == null) {
            // first caller gets to do this
            parent = c;
        } else if (otherParents == null) {
            otherParents = new ArrayList<>();
            otherParents.add(parent);
            otherParents.add(c);
        } else {
            otherParents.add(c);
        }
    }

    /**
     * Cycle through all Closeables sharing this FD and call
     * close() on each one.
     *
     * The caller closeable gets to call close0().
     */
    @SuppressWarnings("try")
    synchronized void closeAll(java.io.Closeable releaser) throws IOException {
        if (!closed) {
            closed = true;
            IOException ioe = null;
            try (java.io.Closeable c = releaser) {
                if (otherParents != null) {
                    for (Closeable referent : otherParents) {
                        try {
                            referent.close();
                        } catch(IOException x) {
                            if (ioe == null) {
                                ioe = x;
                            } else {
                                ioe.addSuppressed(x);
                            }
                        }
                    }
                }
            } catch(IOException ex) {
                /*
                 * If releaser close() throws IOException
                 * add other exceptions as suppressed.
                 */
                if (ioe != null)
                    ex.addSuppressed(ioe);
                ioe = ex;
            } finally {
                if (ioe != null)
                    throw ioe;
            }
        }
    }
}
