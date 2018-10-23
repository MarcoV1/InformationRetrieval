/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package corpus;

import documents.Document;
import java.io.File;

public interface CorpusReader {
    
    public Document nextDocument();
    public File checkFile(String dir);
}
