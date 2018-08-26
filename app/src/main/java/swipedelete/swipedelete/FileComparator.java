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

//        attr.creation
//        Date dt1 = new Date();
//        Date dt2 = new Date(f1.lastModified());

//        if(date1.compareTo(date2)>0){
//            System.out.println("Date1 is after Date2");
//        }else if(date1.compareTo(date2)<0){
//            System.out.println("Date1 is before Date2");
//        }else{
//            System.out.println("Date1 is equal to Date2");
//        }



//        if (f0.lastModified() > f1.lastModified())
//        {
//            return 1;
//        }
//        else if (f1.lastModified() > f0.lastModified())
//        {
//            return -1;
//        }

       // return 0;
