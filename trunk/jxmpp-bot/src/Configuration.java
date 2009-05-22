import java.util.Properties;
import java.io.*;

public class Configuration {
    String fileName;

    String ownerJid;
    String botJid;
    String botJidPassword;
    String conferenceJid;

    public Configuration(String fileName) {
	this.fileName = fileName;
    }

    public boolean Load() {
	boolean result = false;

	try {
	    File configFile = new File(fileName);
	    
	    if (configFile.exists()) {
		Properties props = new Properties();
		
		props.load(new FileInputStream(configFile));
		
		ownerJid = props.getProperty("ownerJid");
		botJid = props.getProperty("botJid");
		botJidPassword = props.getProperty("botJidPassword");
		conferenceJid = 
		    props.getProperty("conferenceJid");
		
		result = ValidateProperties();
	    }

	} catch (Exception e) {
	    
	}

	return result;
    }
    
    public String getOwnerJid() {
	return ownerJid;
    }
    
    public String getBotJid() {
	return botJid;
    }
    
    public String getBotJidPassword() {
	return botJidPassword;
    }
    
    public String getConferenceJid() {
	return conferenceJid;
    }
    
    protected boolean ValidateProperties() {
	
	return !IsNullOrEmpty(ownerJid) &&
	!IsNullOrEmpty(botJid) &&
	!IsNullOrEmpty(botJidPassword) &&
	!IsNullOrEmpty(conferenceJid);
    }
    
    protected boolean IsNullOrEmpty(String str) {
	return str == null || str.length() == 0;
    }
}
