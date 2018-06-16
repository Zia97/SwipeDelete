package swipedelete.swipedelete;

import java.util.ArrayList;

///Folder class, contains name of folder and list of image paths in that folder
public class FolderModel
{
    String folderName;
    ArrayList<String> imagePaths;

    public String getFolderName()
    {
        return folderName;
    }

    public void setFolderName(String folderName)
    {
        this.folderName = folderName;
    }

    public ArrayList<String> getImagePaths()
    {
        return imagePaths;
    }

    public void setImagePaths(ArrayList<String> _imagePaths)
    {
        imagePaths = _imagePaths;
    }
}