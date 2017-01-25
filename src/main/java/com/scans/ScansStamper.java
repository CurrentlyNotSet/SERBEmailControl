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
import java.util.List;

/**
 *
 * @author Andrew
 */
public class ScansStamper {
    
    public static void stampScans(){
        List<ActivityModel> list = Activity.getFilesToStamp();
        
        for (ActivityModel item: list){
            String path = (item.getCaseType().equals("CSC") || item.getCaseType().equals("ORG")) 
                    ? FileService.getCaseFolderORGCSCFileLocation(item) : FileService.getCaseFolderFileLocation(item);
            
            if (FileService.testFileLock(path)){
                StampPDF.stampDocument(path, item.getDate());
                Activity.markEntryStamped(item.getId());
            }
        }
    }
    
}