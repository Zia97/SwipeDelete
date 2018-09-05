package swipedelete.swipedelete;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.Comparator;
import java.util.Date;
import java.nio.file.attribute.BasicFileAttributes;

public class FileComparator implements Comparator<File>
{

    public int compare(File f0, File f1) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                BasicFileAttributes attr = Files.readAttributes(f0.toPath(), BasicFileAttributes.class);
                BasicFileAttributes attr2 = Files.readAttributes(f1.toPath(), BasicFileAttributes.class);

                FileTime t1 = attr.lastModifiedTime();
                t1.toMillis();
                FileTime t2 = attr2.lastModifiedTime();
                t2.toMillis();

                if (t1.compareTo(t2) > 0) {
                    return 1;
                } else if (t1.compareTo(t2) < 0) {
                    return -1;
                } else {
                    return 0;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
}
