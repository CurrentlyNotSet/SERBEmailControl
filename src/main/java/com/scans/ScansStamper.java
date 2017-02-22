/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scans;

import com.fileOperations.StampPDF;
import com.model.ActivityModel;
import com.sql.Activity;
import com.util.FileService;
import com.util.StringUtilities;
import java.util.List;

/**
 *
 * @author Andrew
 */
public class ScansStamper {

    /**
     * This method loops through all of the available scans in the Activity
     * table and applies the stamp to them if they are available.
     */
    public static void stampScans() {
        List<ActivityModel> list = Activity.getFilesToStamp();

        for (ActivityModel item : list) {
            String path = (item.getCaseType().equals("CSC") || item.getCaseType().equals("ORG"))
                    ? FileService.getCaseFolderORGCSCFileLocation(item) : FileService.getCaseFolderFileLocation(item);

            if (FileService.testFileLock(path)) {
                StampPDF.stampDocument(path, item.getDate(), StringUtilities.getDepartmentByCaseType(item.getCaseType()));
                Activity.markEntryStamped(item.getId());
            }
        }
    }

}
