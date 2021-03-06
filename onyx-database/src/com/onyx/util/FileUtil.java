package com.onyx.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * Created by tosborn1 on 3/25/16.
 *
 * This class handles the opening and closing of a file channel
 */
public class FileUtil
{
    /**
     * Open the data file
     *
     * @param filePath File path to open
     * @return File channel
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static FileChannel openFileChannel(String filePath)
    {
        FileChannel channel;
        final File file = new File(filePath);
        try
        {
            // Create the data file if it does not exist
            if (!file.exists())
            {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            // Open the random access file
            final RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "rw");
            channel = randomAccessFile.getChannel();
            channel.position(channel.size());
        } catch (FileNotFoundException e)
        {
            return null;
        } catch (IOException e)
        {
            return null;
        }

        return channel;
    }

    /**
     * Close a file channel.  First syncs the channel and then closes it
     *
     * @param channel File Channel intended to close
     * @throws IOException Basic IO Exception.  Nothing special
     */
    public static void closeFileChannel(FileChannel channel) throws IOException
    {
        channel.force(true);
        channel.close();
    }

}
