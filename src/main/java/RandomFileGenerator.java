import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RandomFileGenerator {

    private static final String FILE_TEMPLATE = "randomFile%05d.txt";
    private static final String PATH_SEPARATOR_LNX = "/";
    private static final String PATH_SEPARATOR_WIN = "\\";
    private static final int CHUNK_LIMIT = 4000;

    public static void main(String[] args) {
        if (args.length > 0) {
            final int fileCount = Integer.parseInt(args[0]);
            final int fileSizeKb = Integer.parseInt(args[1]);
            String destinationPath = args[2];

            //Linux or Mac
            if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC) {
                destinationPath = StringUtils.appendIfMissing(destinationPath, PATH_SEPARATOR_LNX);
            }
            //Windows
            if (SystemUtils.IS_OS_WINDOWS) {
                destinationPath = StringUtils.appendIfMissing(destinationPath, PATH_SEPARATOR_WIN);
            }

            destinationPath = StringUtils.appendIfMissing(destinationPath, FILE_TEMPLATE);

            generateFiles(fileCount, fileSizeKb, destinationPath);

            System.out.println("Done!");
        } else {
            System.out.println("Usage: <file-count> <file-size-kb> <destination-path>");
        }
    }

    private static void generateFiles(int fileCount, int sizeKb, String destPath) {
        int writeIterations = sizeKb / CHUNK_LIMIT;
        if (writeIterations < 1) {
            writeIterations = 1;
        }

        for (int i = 0; i < fileCount; i++) {
            File file = new File(getFilePath(destPath, i).toUri());

            for (int a = 0; a < writeIterations; a++) {
                try (FileOutputStream fos = new FileOutputStream(file, true)) {
                    try (BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                        bos.write(RandomStringUtils.randomAlphanumeric((sizeKb/writeIterations)*1024).getBytes());
                        bos.flush();
                    }
                    fos.flush();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    private static Path getFilePath(String pathTemplate, int fileIndex) {
        return Paths.get(String.format(pathTemplate, fileIndex));
    }

}
