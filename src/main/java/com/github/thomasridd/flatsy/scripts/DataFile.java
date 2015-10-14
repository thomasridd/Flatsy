package com.github.thomasridd.flatsy.scripts;

import com.github.onsdigital.content.page.base.Page;
import com.github.onsdigital.content.page.statistics.dataset.Dataset;
import com.github.onsdigital.content.partial.DownloadSection;
import com.github.onsdigital.content.util.ContentUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomasridd on 29/09/15.
 */
public class DataFile {
    String pageUri;
    String pageTitle;
    String downloadTitle;
    String subpageTitle;
    String downloadFileName;
    Path subpageDirectory;
    Path parentDirectory;

    String subpageUri;
    String subfileUri;

    public boolean subfolderCopy(Path root) throws IOException {
        cleanStrings();
        if (fileValidForSubfolderCopy() == false) { return false; }

        createSubfolder(root);

        copyFilesToSubfolder(root);

        updateSubfolderJson();

        return true;
    }

    private void cleanStrings() {
        if (pageUri.startsWith("/")) {pageUri = pageUri.substring(1);}
        if (downloadFileName.startsWith("/")) {downloadFileName = downloadFileName.substring(1);}
        subpageTitle = subpageTitle.trim();
        downloadTitle = downloadTitle.trim();
    }

    private boolean fileValidForSubfolderCopy() {
        if (subpageTitle.equalsIgnoreCase("x") || subpageTitle.length() == 0) {
            return false;
        } else {
            return true;
        }
    }

    private void createSubfolder(Path root) throws IOException {
        parentDirectory = root.resolve(Paths.get(pageUri).getParent());
        String subpageName = subpageTitle.replaceAll("[^A-Za-z0-9]", "").toLowerCase();

        subpageDirectory = parentDirectory.resolve(subpageName);

        if (Files.exists(subpageDirectory) == false) {
            Files.createDirectory(subpageDirectory);
            System.out.println("Creating folder: " + subpageDirectory);
        }
    }

    private void copyFilesToSubfolder(Path root) throws IOException {

        subpageUri = root.relativize(subpageDirectory.resolve("data.json")).toString();
        subfileUri = root.relativize(subpageDirectory.resolve(root.resolve(downloadFileName).getFileName())).toString();

        try {
            FileUtils.copyFileToDirectory(root.resolve(downloadFileName).toFile(), subpageDirectory.toFile());
        } catch(FileAlreadyExistsException e) {

        }

        try {
            FileUtils.copyFileToDirectory(root.resolve(pageUri).toFile(), subpageDirectory.toFile());
        } catch (FileAlreadyExistsException e) {

        }

    }

    private void updateSubfolderJson() throws IOException {
        Dataset page;
        try(InputStream stream = new FileInputStream(subpageDirectory.resolve("data.json").toFile())) {
            page = (Dataset) ContentUtil.deserialisePage(stream);

            DownloadSection section = new DownloadSection();
            section.setTitle(downloadTitle);

            section.setFile(subfileUri);

            List<DownloadSection> sectionList = new ArrayList<>();
            sectionList.add(section);

            page.setDownloads(sectionList);

        }

        try(OutputStream stream = new FileOutputStream(subpageDirectory.resolve("data.json").toFile())) {
            IOUtils.write( ContentUtil.serialise(page), stream );
        }
    }

}
