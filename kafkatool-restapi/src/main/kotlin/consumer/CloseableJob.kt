package domain

import java.io.Closeable

interface CloseableJob : Closeable, Runnable